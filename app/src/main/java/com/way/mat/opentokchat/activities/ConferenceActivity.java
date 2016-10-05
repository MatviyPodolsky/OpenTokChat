package com.way.mat.opentokchat.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.pixplicity.easyprefs.library.Prefs;
import com.way.mat.opentokchat.R;
import com.way.mat.opentokchat.adapters.PopupAdapter;
import com.way.mat.opentokchat.config.Globals;
import com.way.mat.opentokchat.items.PopupItem;
import com.way.mat.opentokchat.multiparty.CallbackSession;
import com.way.mat.opentokchat.multiparty.OpenTokSession;
import com.way.mat.opentokchat.rest.client.RestClient;
import com.way.mat.opentokchat.rest.models.Room;
import com.way.mat.opentokchat.rest.responses.GetTokenResponse;
import com.way.mat.opentokchat.utils.ImageUrlUtils;
import com.way.mat.opentokchat.utils.PermissionsUtil;
import com.way.mat.opentokchat.utils.PrefKeys;
import com.way.mat.opentokchat.views.MeterView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConferenceActivity extends BaseActivity implements CallbackSession {

    //popup
    private List<PopupItem> popupItems = new ArrayList<>();
    private ListPopupWindow mPopup;
    //popup end

    private static final String TAG = "ConferenceActivity";

    private OpenTokSession mSession;
    private boolean resumeHasRun = false;

    private boolean isCalling = false;
    private boolean isVolumeOn = true;
    private boolean isHeld = false;

//    private PopupMenu mPopup;
//    private boolean isPreviewCamera = true;

    @BindView(R.id.settings)
    ImageButton btnSettings;
    @BindView(R.id.img_call)
    ImageView imgCall;
    @BindView(R.id.img_hold)
    ImageView imgHold;
    @BindView(R.id.img_mute)
    MeterView imgMute;
    @BindView(R.id.img_volume)
    ImageView imgVolume;
    @BindView(R.id.img_turn_camera)
    ImageView imgTurnCamera;
    @BindView(R.id.rl_hold)
    RelativeLayout rlHold;
    @BindView(R.id.rl_mute)
    RelativeLayout rlMute;
    @BindView(R.id.preview)
    ViewGroup mPreview;
    @BindView(R.id.progress)
    FrameLayout mProgress;
    @BindView(R.id.rl_buttons)
    RelativeLayout rlButtons;
    @BindView(R.id.root)
    LinearLayout llPreviews;
    @BindView(R.id.tv_start_watching)
    TextView tvStartWatching;
    @BindView(R.id.rl_description)
    RelativeLayout rlDescription;
    @BindView(R.id.description)
    TextView tvDescription;
    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.logo)
    ImageView imgLogo;

    @BindViews({R.id.user_name_2, R.id.user_name_3, R.id.user_name_4, R.id.user_name_5, R.id.user_name_6, R.id.user_name_7})
    List<TextView> mUserNames;
//    @BindViews({R.id.pb_volume_1, R.id.pb_volume_2})
//    List<ProgressBar> mAudioLevels;
    @BindViews({R.id.preview2, R.id.preview3, R.id.preview4, R.id.preview5, R.id.preview6, R.id.preview7})
    List<ViewGroup> mPreviews;
    @BindViews({R.id.ll_top, R.id.ll_mid})
    List<ViewGroup> mPreviewLayouts;
    @BindViews({R.id.container2, R.id.container3, R.id.container4, R.id.container5, R.id.container6, R.id.container7})
    List<ViewGroup> mPreviewContainers;

    private String mToken = "";
    private boolean needConnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionsUtil.needPermissions(this)) {
                PermissionsUtil.requestPermissions(this);
            }
        }

        initSession();
        initPopup();

        Room room = Room.create(getIntent().getExtras().getString("room"));
        setTitle(room.getName());
        tvTitle.setText(room.getName());
        tvDescription.setText(room.getDescription());
        final String realUrl = ImageUrlUtils.getRealUrl(room.getImageUrl());
        Log.d(TAG, "url: " + realUrl);
        Glide.with(this).load(realUrl).centerCrop().bitmapTransform( new CropCircleTransformation(this)).into(imgLogo);

//        setTitle("test");
//        tvTitle.setText("test");
//        tvDescription.setText("description");
//        Picasso.with(this).load(R.mipmap.ic_launcher).fit().centerInside().into(imgLogo);

        loadToken();
//        mToken = OpenTokConfig.TOKEN;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSession != null) {
            mSession.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!resumeHasRun) {
            resumeHasRun = true;
        } else {
            if (mSession != null) {
                mSession.onResume();
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        if (isFinishing()) {
            if (mSession != null) {
                mSession.disconnect();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mSession != null) {
            mSession.disconnect();
        }
        super.onDestroy();
        finish();
    }

    @Override
    protected int getActivityResource() {
        return R.layout.activity_main;
    }

    @Override
    public void onBackPressed() {
        if (mSession != null) {
            mSession.disconnect();
        }
        super.onBackPressed();
    }

    private void initSession() {
        if (mSession == null) {
            mSession = new OpenTokSession(this, this);
            mSession.setPreviewView(mPreview);
            mSession.setPlayersViewContainers(mPreviews);
            mSession.setLayouts(mPreviewLayouts);
            mSession.setContainers(mPreviewContainers);
//            mSession.setAudioLevelViews(mAudioLevels);
            mSession.setUsernameViews(mUserNames);

            // Set meter view icons for publisher
            imgMute.setIcons(BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_tb_mic_normal), BitmapFactory.decodeResource(
                    getResources(), R.drawable.ic_tb_mic_off));
            mSession.setPublisherMeter(imgMute);

            mSession.setCameraListener(new Publisher.CameraListener() {
                @Override
                public void onCameraChanged(Publisher publisher, int i) {
//                    isPreviewCamera = 1 == i;
//                    Log.i("CameraId", "Current Camera Id: " + i + " is preview camera active: " + isPreviewCamera);
                }

                @Override
                public void onCameraError(Publisher publisher, OpentokError opentokError) {
                    Log.e(TAG, "Camera Error: " + opentokError.getMessage());
                }
            });
        }
    }

    private void goFullScreen() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
    }

    private void leaveFullScreen() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
    }

    @OnClick(R.id.img_call)
    public void toggleCall(View v) {
        if (isCalling) {
            hangUp();
        } else {
            doCall();
        }
    }

    @OnClick(R.id.img_hold)
    public void toggleHold(View v) {
        if (isHeld) {
            unhold();
            imgHold.setImageResource(R.drawable.ic_view_selector);
        } else {
            hold();
            imgHold.setImageResource(R.drawable.ic_view_off_selector);
        }
    }

    @OnClick(R.id.settings)
    public void settingsClick(View v) {
        if (mPopup != null) {
            mPopup.show();//showing popup menu
        }
    }

    private void initPopup() {
        popupItems.add(new PopupItem(R.drawable.ic_submenu_username, getString(R.string.action_login), PopupItem.Type.USERNAME));
        popupItems.add(new PopupItem(R.drawable.ic_submenu_about, getString(R.string.action_about), PopupItem.Type.ABOUT));

        mPopup = new ListPopupWindow(this);

        final PopupAdapter adapter = new PopupAdapter(this, popupItems);

        mPopup.setAnchorView(btnSettings);
        mPopup.setAdapter(adapter);
        mPopup.setWidth(getResources().getDimensionPixelSize(R.dimen.popup_width)); // note: don't use pixels, use a dimen resource
        mPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                launchActivity(popupItems.get(i).getType());
                if (mPopup != null) {
                    mPopup.dismiss();
                }
            }
        }); // the callback for when a list item is selected

    }

    @OnClick(R.id.img_turn_camera)
    public void turnCamera(View v) {
        turnCamera();
    }

    @OnClick(R.id.img_volume)
    public void toggleVolume(View v) {
        if (isVolumeOn) {
            turnVolumeOff();
            imgVolume.setImageResource(R.drawable.ic_volume_off_selector);
        } else {
            turnVolumeOn();
            imgVolume.setImageResource(R.drawable.ic_volume_selector);
        }
    }

    private void updateControlsUI() {
        if (isCalling) {
            goFullScreen();
            imgCall.setImageResource(R.drawable.btn_large_stop_watching);
            rlButtons.setVisibility(View.VISIBLE);
            tvStartWatching.setVisibility(View.GONE);
            getSupportActionBar().hide();
        } else {
            leaveFullScreen();
            imgCall.setImageResource(R.drawable.btn_large_start_watching);
            rlButtons.setVisibility(View.GONE);
            tvStartWatching.setVisibility(View.VISIBLE);
            getSupportActionBar().show();
        }

        if (isHeld) {
            imgHold.setImageResource(R.drawable.ic_view_off_selector);
        } else {
            imgHold.setImageResource(R.drawable.ic_view_selector);
        }

        if (isVolumeOn) {
            imgVolume.setImageResource(R.drawable.ic_volume_selector);
        } else {
            imgVolume.setImageResource(R.drawable.ic_volume_off_selector);
        }
    }

    private void doCall() {
        if (!TextUtils.isEmpty(mToken)) {
            showProgress();
            if (mSession != null) {
                mSession.setToken(mToken);
                mSession.connect();
            }
        } else {
            needConnect = true;
            loadToken();
        }
    }

    private void hangUp() {
        showProgress();
        if (mSession != null) {
            mSession.hangUp();
            mPreview.removeAllViews();
        }
    }

    private void hold() {
        if (mSession != null) {
            mSession.getPublisher().setPublishAudio(false);
            mSession.getPublisher().setPublishVideo(false);
        }
        isHeld = true;
    }

    private void unhold() {
        if (mSession != null) {
            mSession.getPublisher().setPublishAudio(!imgMute.isMuted());
            mSession.getPublisher().setPublishVideo(true);
        }
        isHeld = false;
    }

    private void turnVolumeOff() {
        isVolumeOn = false;
        if (mSession != null) {
            mSession.unsubscribeAudio();
        }
    }

    private void turnVolumeOn() {
        isVolumeOn = true;
        if (mSession != null) {
            mSession.subscribeAudio();
        }
    }

    private void turnCamera() {
        if (mSession != null) {
            try {
                mSession.getPublisher().cycleCamera();
            } catch (Exception e) {

            }
        }
    }

    private void showProgress() {
        if (btnSettings != null) {
            btnSettings.setEnabled(false);
        }
        if (mProgress != null) {
            mProgress.setVisibility(View.VISIBLE);
        }
        if (mPopup != null) {
            mPopup.dismiss();
        }
    }

    private void hideProgress() {
        if (btnSettings != null) {
            btnSettings.setEnabled(true);
        }
        if (mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Globals.REQUEST_PERMISSIONS) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                boolean isPermissionGranted = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isPermissionGranted = false;
                    }
                }
                if (isPermissionGranted) {
                    Toast.makeText(ConferenceActivity.this, "Permissions granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConferenceActivity.this, "Permissions not granted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSessionConnected() {
        isCalling = true;
        hideProgress();
        updateControlsUI();

        llPreviews.setVisibility(View.VISIBLE);
        mPreview.setVisibility(View.VISIBLE);
        btnSettings.setVisibility(View.GONE);
        rlDescription.setVisibility(View.GONE);
    }

    @Override
    public void onSessionDisconnected() {
        invalidateDisconnection();
    }

    @Override
    public void onSessionError(final String error) {
        invalidateDisconnection();
//        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void invalidateDisconnection() {
        isCalling = false;
        isHeld = false;
        isVolumeOn = true;
        hideProgress();
        updateControlsUI();

        btnSettings.setVisibility(View.VISIBLE);
        mPreview.setVisibility(View.GONE);
        llPreviews.setVisibility(View.GONE);
        rlDescription.setVisibility(View.VISIBLE);
    }

    private void launchActivity(PopupItem.Type type) {
        if (type != null) {
            switch (type) {
                case USERNAME:
                    startActivity(new Intent(ConferenceActivity.this, LoginActivity.class));
                    break;
                case ABOUT:
                    startActivity(new Intent(ConferenceActivity.this, AboutActivity.class));
                    break;
            }
        }
    }

    private void loadToken() {
        showProgress();
        Call<GetTokenResponse> call = RestClient.getApiService().getToken(Prefs.getString(PrefKeys.SESSION_ID, ""));
        call.enqueue(new Callback<GetTokenResponse>() {
            @Override
            public void onResponse(Call<GetTokenResponse> call, Response<GetTokenResponse> response) {
                if (response != null && response.body() != null && response.body().getResponse() != null && !TextUtils.isEmpty(response.body().getResponse().getToken())) {
                    onTokenObtained(response.body().getResponse().getToken());
                } else {
                    onTokenError();
                }
            }

            @Override
            public void onFailure(Call<GetTokenResponse> call, Throwable t) {
                onTokenError();
            }
        });
    }

    private void onTokenObtained(String token) {
        hideProgress();
        mToken = token;
        if (needConnect) {
            needConnect = false;
            showProgress();
            if (mSession != null) {
                mSession.setToken(mToken);
                mSession.connect();
            } else {
                hideProgress();
                Toast.makeText(ConferenceActivity.this, "null session", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onTokenError() {
        hideProgress();
        Toast.makeText(ConferenceActivity.this, "Error loading token", Toast.LENGTH_SHORT).show();
    }

}
