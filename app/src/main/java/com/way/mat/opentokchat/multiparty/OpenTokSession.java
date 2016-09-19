package com.way.mat.opentokchat.multiparty;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.SubscriberKit;
import com.pixplicity.easyprefs.library.Prefs;
import com.way.mat.opentokchat.config.OpenTokConfig;
import com.way.mat.opentokchat.utils.PrefKeys;
import com.way.mat.opentokchat.views.MeterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpenTokSession extends Session {

    private Context mContext;

    // Interface
    private List<ViewGroup> mPreviews;
    private ViewGroup mPreview;
    private List<ProgressBar> mAudioLevels;
    private List<TextView> mUserNames;
    final private CallbackSession callbackSession;
    private MeterView mPublisherMeter;

    private Publisher mPublisher;

    private Publisher.CameraListener mCameraListener;

    private boolean isVolumeOff = false;

    // Players status
    private ArrayList<OpenTokSubscriber> mSubscribers = new ArrayList<>();
    private HashMap<Stream, OpenTokSubscriber> mSubscriberStream = new HashMap<Stream, OpenTokSubscriber>();
    private HashMap<String, OpenTokSubscriber> mSubscriberConnection = new HashMap<String, OpenTokSubscriber>();

    public OpenTokSession(final Context context, final CallbackSession pCallbackSession) {
        super(context, OpenTokConfig.API_KEY, Prefs.getString(PrefKeys.SESSION_ID, ""));
        this.mContext = context;
        callbackSession = pCallbackSession;
    }

    // public methods
    public void setPreviewView(ViewGroup preview) {
        this.mPreview = preview;
    }

    public void setPlayersViewContainers(List<ViewGroup> containers) {
        this.mPreviews = new ArrayList<>();
        this.mPreviews.addAll(containers);
    }

    private void setPublisher() {
        mPublisher = new Publisher(mContext, getCurrentPublishName());

        // Add video preview
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mPreview.addView(mPublisher.getView(), lp);
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        if (mCameraListener != null) {
            mPublisher.setCameraListener(mCameraListener);
        }
    }

    public void setPublisherMeter(MeterView meterView) {
        this.mPublisherMeter = meterView;
    }

    public void setAudioLevelViews(List<ProgressBar> views) {
        this.mAudioLevels = new ArrayList<>();
        mAudioLevels.addAll(views);
    }

    public void setUsernameViews(List<TextView> views) {
        this.mUserNames = new ArrayList<>();
        mUserNames.addAll(views);
    }

    public void setCameraListener(Publisher.CameraListener listener) {
        this.mCameraListener = listener;
    }

    public void connect() {
        setPublisher();
        this.connect(Prefs.getString(PrefKeys.TOKEN, ""));
    }

    // callbacks
    @Override
    protected void onConnected() {
        isVolumeOff = false;
        publish(mPublisher);
        callbackSession.onSessionConnected();
        // Initialize publisher meter view
        mPublisher.setAudioLevelListener(new PublisherKit.AudioLevelListener() {
            @Override
            public void onAudioLevelUpdated(PublisherKit publisher,
                                            float audioLevel) {
                mPublisherMeter.setMeterValue(audioLevel);
            }
        });
        mPublisherMeter.setOnClickListener(new MeterView.OnClickListener() {
            @Override
            public void onClick(MeterView view) {
                if (mPublisher != null) {
                    mPublisher.setPublishAudio(!view.isMuted());
                }
            }
        });
    }

    @Override
    protected void onDisconnected() {
        isVolumeOff = false;
        callbackSession.onSessionDisconnected();
        super.onDisconnected();
    }

    @Override
    protected void onStreamReceived(Stream stream) {

        //3 users activity_main limitation
//        if (mSubscribers != null && mSubscribers.size() < 2) {
        if (mSubscribers != null) {

            final OpenTokSubscriber p = new OpenTokSubscriber(mContext, stream);

            // we can use connection data to obtain each user id
            p.setUserId(stream.getConnection().getData());

            p.setSubscribeToVideo(false);
            p.setSubscribeToAudio(!isVolumeOff);
            //Subscribe only for first 2 users in room
            if (mSubscribers.size() < 3) {
                this.subscribe(p);
            }

            p.setAudioLevelListener(new SubscriberKit.AudioLevelListener() {
                @Override
                public void onAudioLevelUpdated(
                        SubscriberKit subscriber, final float audioLevel) {
                    ((OpenTokSubscriber) subscriber).setAudioLevel(audioLevel);
                }
            });


            mSubscribers.add(p);
            mSubscriberStream.put(stream, p);
            mSubscriberConnection.put(stream.getConnection().getConnectionId(), p);

            updateSubscribers();
        }
    }

    @Override
    protected void onError(OpentokError error) {
        super.onError(error);
        callbackSession.onSessionError(error.getMessage());
        mSubscribers.clear();
        mSubscriberStream.clear();
        mSubscriberConnection.clear();
        updateSubscribers();
    }

    @Override
    protected void onStreamDropped(Stream stream) {
        OpenTokSubscriber p = mSubscriberStream.get(stream);
        if (p != null) {
            mSubscribers.remove(p);
            mSubscriberStream.remove(stream);
            mSubscriberConnection.remove(stream.getConnection().getConnectionId());
        }
        updateSubscribers();
    }

    public void updatePreviews() {
        for (ViewGroup vg : mPreviews) {
            vg.removeAllViews();
        }

        for (int i = 0; i < mSubscribers.size(); i++) {
            OpenTokSubscriber s = mSubscribers.get(i);
            ViewGroup viewGroup = mPreviews.get(i);
            viewGroup.setVisibility(View.VISIBLE);
            viewGroup.addView(s.getView());
        }

        for (int i = mSubscribers.size(); i < mPreviews.size(); i++) {
            mPreviews.get(i).setVisibility(View.GONE);
        }
    }

    private void updateSubscribers() {
        final int size = mSubscribers.size();
        final int maxUsersInRoom = 2;

        final int min = Math.min(mSubscribers.size(), maxUsersInRoom);

        //subscribe to all users' audio if their count below maxUsersInRoom
        for (int i = 0; i < min; i++) {
            OpenTokSubscriber customSubscriber = mSubscribers.get(i);
            customSubscriber.setSubscribeToAudio(!isVolumeOff);
            customSubscriber.setSubscribeToVideo(true);

            TextView tv = mUserNames.get(i);
            customSubscriber.setUsernameView(tv);
            tv.setVisibility(View.VISIBLE);

            ProgressBar pb = mAudioLevels.get(i);
            customSubscriber.setAudioLevelView(pb);
            pb.setVisibility(View.VISIBLE);

            customSubscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        }

        //unsubscribe audio from users above maxUsersInRoom and remove audio level view
        for (int i = min; i < size; i++) {
            OpenTokSubscriber customSubscriber = mSubscribers.get(i);
            customSubscriber.setSubscribeToAudio(false);
            customSubscriber.setAudioLevelView(null);
        }

        //hide audio level bars if users count less than maxUsersInRoom
        for (int i = size; i < maxUsersInRoom; i++) {
            mUserNames.get(i).setVisibility(View.GONE);
            mAudioLevels.get(i).setVisibility(View.GONE);
        }

        updatePreviews();
    }

    public Publisher getPublisher() {
        return mPublisher;
    }

    public void subscribeAudio() {
        isVolumeOff = false;
        if (mSubscribers != null) {
            for (OpenTokSubscriber s : mSubscribers) {
                s.setSubscribeToAudio(true);
            }
        }
    }

    public void unsubscribeAudio() {
        isVolumeOff = true;
        if (mSubscribers != null) {
            for (OpenTokSubscriber s : mSubscribers) {
                s.setSubscribeToAudio(false);
            }
        }
    }

    public void hangUp() {
        mSubscribers.clear();
        mSubscriberStream.clear();
        mSubscriberConnection.clear();
        if (mPublisherMeter != null && mPublisher != null) {
            mPublisher.setAudioLevelListener(null);
            mPublisherMeter.clear();
        }
        disconnect();
        updateSubscribers();
    }

    private String getCurrentPublishName() {
//        return TextUtils.isEmpty(Prefs.getString(PrefKeys.LOGIN_USER, PrefKeys.EMPTY_STRING)) ?
//                Build.BRAND + " " + Build.MODEL :
//                Prefs.getString(PrefKeys.LOGIN_USER, PrefKeys.EMPTY_STRING);
        return TextUtils.isEmpty(Prefs.getString(PrefKeys.LOGIN_USER, PrefKeys.EMPTY_STRING)) ?
                "Viewer" :
                Prefs.getString(PrefKeys.LOGIN_USER, PrefKeys.EMPTY_STRING);
    }

}
