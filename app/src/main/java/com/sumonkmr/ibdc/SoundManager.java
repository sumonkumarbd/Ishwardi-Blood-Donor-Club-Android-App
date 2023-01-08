package com.sumonkmr.ibdc;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {
    MediaPlayer btn,okkBtn,cbtN,great_sound,national_aunt,uiClick,light_switch;
    Context context;

    public SoundManager() {
        SoundsList();
    }

    public SoundManager(Context context) {
        this.context = context;
        SoundsList();
    }

    protected void SoundsList() {
        btn = MediaPlayer.create(context, R.raw.mousemp3);
        okkBtn = MediaPlayer.create(context, R.raw.positive_beeps);
        cbtN = MediaPlayer.create(context, R.raw.stop);
        great_sound = MediaPlayer.create(context, R.raw.decide);
        national_aunt = MediaPlayer.create(context, R.raw.bangladesh_national_anthem);
        uiClick = MediaPlayer.create(context, R.raw.uiclick);
        light_switch = MediaPlayer.create(context, R.raw.light_switch);
    }
}

