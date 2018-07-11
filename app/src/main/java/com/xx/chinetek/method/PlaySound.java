package com.xx.chinetek.method;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.xx.chinetek.method.DB.DbManager;
import com.xx.chinetek.mitsubshi.R;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;

/**
 * Created by GHOST on 2017/12/14.
 */

public class PlaySound {
    private SoundPool soundPool;
    private int playSoundErrID;
    public static PlaySound mPlaySound;

    private PlaySound() {
        try {
            if (Build.VERSION.SDK_INT > 21) {
                SoundPool.Builder builder = new SoundPool.Builder();
                builder.setMaxStreams(30);
                AudioAttributes.Builder builder1 = new AudioAttributes.Builder();
                builder1.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                builder.setAudioAttributes(builder1.build());
                soundPool = builder.build();

            } else {
                //21版本以前使用SoundPool(int maxStreams, int streamType, int srcQuality)
                soundPool = new SoundPool(30, AudioManager.STREAM_MUSIC, 0);
            }
            playSoundErrID= soundPool.load(context, R.raw.error4, 1);

        }catch (Exception ex){

        }

    }

    public static PlaySound getInstance() {
        if (null == mPlaySound) {
            synchronized (DbManager.class) {
                if (null == mPlaySound) {
                    mPlaySound = new PlaySound();
                }
            }
        }
        return mPlaySound;
    }

//    public void PlayNormal(){
//        soundPool.play(playSoundID, 2, 2, 0, 0, 1);
//    }

    public void PlayError() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                   soundPool.play(playSoundErrID, 1, 1, 0, 0, 1);
                }catch (Exception ex){
                }
            }
        }).start();
    }
}
