package com.example.lab1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class MyReciever2 extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int id=intent.getIntExtra("btn",-1);
        if(id==R.id.play_noti){
            MyService.mp.start();
            MainActivity.toggleButton.setChecked(true);
        }
        if(id==R.id.pause_noti){
            MyService.mp.pause();
            MainActivity.toggleButton.setChecked(false);
        }
        if(id==R.id.prev_noti){
            MainActivity.pause = true;
            MainActivity.toggleButton.setChecked(false);
            for (int i = 0; i < MainActivity.list.size(); i++) {
                if (MainActivity.media_active == MainActivity.list.get(i).getMedia()) {
                    if (i == 0)
                        MainActivity.media_active = MainActivity.list.get(MainActivity.list.size() - 1).getMedia();
                    else
                        MainActivity.media_active = MainActivity.list.get(i - 1).getMedia();
                    break;
                }
            }
            for (int i = 0; i < MainActivity.list.size(); i++) {
                if (MainActivity.media_active == MainActivity.list.get(i).getMedia()) {
                    MyService.mp.stop();
                    MyService.mp = MediaPlayer.create(context, MainActivity.list.get(i).getMedia());
                    MainActivity.media_active = MainActivity.list.get(i).getMedia();
                    MainActivity.image.setImageResource(MainActivity.list.get(i).getImage());
                    MainActivity.name.setText(MainActivity.list.get(i).getName());
                    MainActivity.toggleButton.setChecked(true);
                    MainActivity.pause = false;
                    break;
                }
            }

        }
        if(id==R.id.next_noti){
            MainActivity.pause = true;
            MainActivity.toggleButton.setChecked(false);
            for (int i = 0; i <  MainActivity.list.size(); i++) {
                if ( MainActivity.media_active ==  MainActivity.list.get(i).getMedia()) {
                    if (i ==  MainActivity.list.size() - 1)
                        MainActivity.media_active =  MainActivity.list.get(0).getMedia();
                    else
                        MainActivity.media_active =  MainActivity.list.get(i + 1).getMedia();
                    break;
                }
            }
            for (int i = 0; i <  MainActivity.list.size(); i++) {
                if ( MainActivity.media_active ==  MainActivity.list.get(i).getMedia()) {
                    MyService.mp.stop();
                    MyService.mp = MediaPlayer.create(context,  MainActivity.list.get(i).getMedia());
                    MainActivity.media_active =  MainActivity.list.get(i).getMedia();
                    MainActivity.image.setImageResource( MainActivity.list.get(i).getImage());
                    MainActivity.name.setText( MainActivity.list.get(i).getName());
                    MainActivity.toggleButton.setChecked(true);
                    MainActivity.pause = false;
                    break;
                }
            }
        }

    }


}
