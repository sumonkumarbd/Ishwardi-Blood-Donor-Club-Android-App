package com.sumonkmr.ibdc;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {
    MediaPlayer btn,okkBtn,cbtN,great_sound,national_aunt;
    Activity activity;

    protected SoundManager() {
    }

    protected SoundManager(Activity activity) {

        this.activity = activity;
        SoundsList();
    }

    protected void SoundsList() {
        btn = MediaPlayer.create(activity, R.raw.mousemp3);
        okkBtn = MediaPlayer.create(activity, R.raw.positive_beeps);
        cbtN = MediaPlayer.create(activity, R.raw.stop);
        great_sound = MediaPlayer.create(activity, R.raw.decide);
        national_aunt = MediaPlayer.create(activity, R.raw.bangladesh_national_anthem);
    }
}

