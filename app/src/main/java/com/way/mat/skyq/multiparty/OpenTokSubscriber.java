package com.way.mat.skyq.multiparty;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

public class OpenTokSubscriber extends Subscriber {

    private String userId;
    private String name;
    private ProgressBar pbAudioLevel;
    private TextView tvUsername;

    public OpenTokSubscriber(Context context, Stream stream) {
        super(context, stream);
        // With the userId we can query our own database
        // to extract player information
        setName(stream.getName());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String name) {
        this.userId = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAudioLevelView(ProgressBar pb) {
        pbAudioLevel = pb;
    }

    public void setUsernameView(TextView tv) {
        tvUsername = tv;
        tv.setText(name);
    }

    public void setUsername(String name) {
        this.tvUsername.setText(name);
    }

    public void setAudioLevel(float value) {
        final double db = 20 * Math.log10(value);
        final float floor = -40;
        float level = 0;
        if (db > floor) {
            level = (float) db - floor;
            level /= -floor;
        }
        if (pbAudioLevel != null) {
            this.pbAudioLevel.setProgress((int) (level * 100));
        }
        Log.d("audio_level", "setAudioLevel: " + level);
    }
}
