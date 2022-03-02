package com.sekhgmainuddin.calculator.whatsappclone.tools;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.widget.ImageButton;

import java.io.IOException;

public class AudioService {

    private Context context;
    private MediaPlayer tmpMediaPlayer;
    private OnPlayCallBack onPlayCallBack;

    public AudioService(Context context) {
        this.context = context;
    }
    public void playAudioFromUrl(String url,final OnPlayCallBack onPlayCallBack){
        this.onPlayCallBack=onPlayCallBack;
        if(tmpMediaPlayer!=null){
            tmpMediaPlayer.stop();
        }
        MediaPlayer mediaPlayer=new MediaPlayer();
        try{
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch(IOException e){
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                onPlayCallBack.onFinished();
            }
        });
    }
    public interface OnPlayCallBack{
        void onFinished();
    }
}
