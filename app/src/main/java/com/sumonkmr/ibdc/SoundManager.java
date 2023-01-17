package com.sumonkmr.ibdc;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

public class SoundManager {
    MediaPlayer btn,okkBtn,cbtN,great_sound,national_aunt,uiClick,light_switch,reFresh;
    Context context;

    public SoundManager() {
        SoundsList();
    }

    public SoundManager(Context context) {
        this.context = context;
            SoundsList();
    }

    protected void SoundsList() {
        btn = MediaPlayer.create(context, R.raw.uiclick);
        okkBtn = MediaPlayer.create(context, R.raw.select);
        cbtN = MediaPlayer.create(context, R.raw.cancel);
        great_sound = MediaPlayer.create(context, R.raw.decide);
        reFresh = MediaPlayer.create(context, R.raw.blood_drop);
        national_aunt = MediaPlayer.create(context, R.raw.bangladesh_national_anthem);
        uiClick = MediaPlayer.create(context, R.raw.click);
        light_switch = MediaPlayer.create(context, R.raw.light_switch);
    }
}

