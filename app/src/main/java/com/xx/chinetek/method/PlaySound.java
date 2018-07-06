package com.xx.chinetek.method;

import android.media.AudioManager;
import android.media.SoundPool;

import com.xx.chinetek.method.DB.DbManager;
import com.xx.chinetek.mitsubshi.R;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;

/**
 * Created by GHOST on 2017/12/14.
 */

public class PlaySound {
    private SoundPool soundPool;
    private int playSoundID,playSoundErrID;
    public static PlaySound mPlaySound;

    private PlaySound() {
        soundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM, 8);
      //  playSoundID = soundPool.load(context, R.raw.error1, 0);
        //  playSoundErrID = soundPool.load(context, R.raw.error2, 0);
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
        playSoundErrID = soundPool.load(context, R.raw.error2, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(playSoundErrID, 2, 2, 0, 1, 1);
            }
        });

       // soundPool.play(playSoundErrID, 2, 2, 0, 1, 1);
    }
}
