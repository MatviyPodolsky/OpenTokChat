package com.way.mat.opentokchat.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.way.mat.opentokchat.rest.models.TokenData;
import com.way.mat.opentokchat.utils.PermissionsUtil;
import com.way.mat.opentokchat.utils.PrefKeys;
import com.way.mat.opentokchat.views.MeterView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConferenceActivity extends BaseActivity implements CallbackSession {

    //popup
    private List<PopupItem> popupItems = new ArrayList<>();
    //popup end

    private static final String TAG = ConferenceActivity.class.getSimpleName();

    private OpenTokSession mSession;
    private boolean resumeHasRun = false;

    private boolean isCalling = false;
    private boolean isVolumeOn = true;
    private boolean isHeld = false;

//    private PopupMenu mPopup;
    private ListPopupWindow mPopup;
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

    @BindViews({R.id.user_name_1, R.id.user_name_2})
    List<TextView> mUserNames;
    @BindViews({R.id.pb_volume_1, R.id.pb_volume_2})
    List<ProgressBar> mAudioLevels;
    @BindViews({R.id.preview2, R.id.preview3, R.id.preview4})
    List<ViewGroup> mPreviews;

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

        loadToken();
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
            mSession.setAudioLevelViews(mAudioLevels);
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
            imgHold.setImageResource(R.drawable.ic_tb_view_normal);
        } else {
            hold();
            imgHold.setImageResource(R.drawable.ic_tb_view_off);
        }
    }

    @OnClick(R.id.settings)
    public void settingsClick(View v) {
        if (mPopup != null) {
            mPopup.show();//showing popup menu
        }
    }

    private void initPopup() {
        popupItems.add(new PopupItem(R.drawable.ic_menu_edit_normal, getString(R.string.action_login), PopupItem.Type.USERNAME));
        popupItems.add(new PopupItem(R.drawable.ic_menu_list_normal, getString(R.string.action_about), PopupItem.Type.ABOUT));

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
            imgVolume.setImageResource(R.drawable.ic_tb_volume_off);
        } else {
            turnVolumeOn();
            imgVolume.setImageResource(R.drawable.ic_tb_volume_normal);
        }
    }

    private void updateControlsUI() {
        if (isCalling) {
            imgCall.setImageResource(R.drawable.ic_stop);
            rlButtons.setVisibility(View.VISIBLE);
        } else {
            imgCall.setImageResource(R.drawable.ic_camera);
            rlButtons.setVisibility(View.GONE);
        }

        if (isHeld) {
            imgHold.setImageResource(R.drawable.ic_tb_view_off);
        } else {
            imgHold.setImageResource(R.drawable.ic_tb_view_normal);
        }

        if (isVolumeOn) {
            imgVolume.setImageResource(R.drawable.ic_tb_volume_normal);
        } else {
            imgVolume.setImageResource(R.drawable.ic_tb_volume_off);
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
    }

    @Override
    public void onSessionDisconnected() {
        invalidateDisconnection();
    }

    @Override
    public void onSessionError(final String error) {
        invalidateDisconnection();
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
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
        Call<TokenData> call = RestClient.getApiService().getToken(Prefs.getString(PrefKeys.SESSION_ID, ""));
        call.enqueue(new Callback<TokenData>() {
            @Override
            public void onResponse(Call<TokenData> call, Response<TokenData> response) {
                if (response != null && response.body() != null && !TextUtils.isEmpty(response.body().getToken())) {
                    onTokenObtained(response.body().getToken());
                } else {
                    onTokenError();
                }
            }

            @Override
            public void onFailure(Call<TokenData> call, Throwable t) {
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
