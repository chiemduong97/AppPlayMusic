package com.example.lab1;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    public static ArrayList<Music> list;
    public static ToggleButton toggleButton, toggleButtonReplay;
    public static int media_active;
    public static ImageView image;
    public static TextView name;
    private ImageView prev, next;
    public static boolean pause;
    private boolean replay;
    private Intent intent;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        toggleButton = findViewById(R.id.toggle);
        toggleButtonReplay = findViewById(R.id.toggle_replay);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        progressBar = findViewById(R.id.progessBar);
        intent = new Intent(MainActivity.this, MyService.class);

        list = new ArrayList<>();
        list.add(new Music(1, R.drawable.tutam, "Tự tâm", R.raw.tutam));
        list.add(new Music(2, R.drawable.hoanokhongmau, "Hoa nở không màu", R.raw.hoanokhongmau));
        list.add(new Music(3, R.drawable.niuduyen, "Níu duyên", R.raw.niuduyen));
        list.add(new Music(4, R.drawable.thethai, "Thế thái", R.raw.thethai));
        list.add(new Music(5, R.drawable.tinhbandieuky, "Tình bạn diệu kỳ", R.raw.tinhbandieuky));
        media_active = list.get(0).getMedia();
        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);
        Music music = new Music();
        restore(music);
        toggleButtonReplay.setChecked(replay);
        boolean contain = false;
        for (int i = 0; i < list.size(); i++) {
            if (music.getId() == list.get(i).getId()) {
                contain = true;
                break;
            }
        }
        if (contain == false) {
            media_active = list.get(0).getMedia();
            image.setImageResource(list.get(0).getImage());
            name.setText(list.get(0).getName());
            intent.putExtra("media", media_active);
            toggleButton.setChecked(true);
            stopService(intent);
            startService(intent);
            pause = true;
            toggleButton.setChecked(false);
            image.clearAnimation();
            save();
            Log.d("not contain", "not contain");
        } else {
            media_active = music.getMedia();
            image.setImageResource(music.getImage());
            name.setText(music.getName());
            intent.putExtra("media", media_active);
            toggleButton.setChecked(true);
            save();
            Log.d("contain", "contain");
        }
        image.startAnimation(animation);
        if (MyService.mp != null) {
            progressBar.setProgress(MyService.mp.getCurrentPosition() / 1000);
            progressBar.setMax(MyService.mp.getDuration() / 1000);
            new Thread() {
                @Override
                public void run() {
                    for (int i = MyService.mp.getCurrentPosition(); i < MyService.mp.getDuration(); i = i + 1000) {
                        try {
                            progressBar.setProgress(MyService.mp.getCurrentPosition() / 1000);
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            break;
                        }
                    }
                }
            }.start();
        }
        if (pause == true) {
            stopService(intent);
            startService(intent);
            pause = true;
            toggleButton.setChecked(false);
            image.clearAnimation();
        } else {
            if (MyService.mp == null) {
                stopService(intent);
                startService(intent);
                MyService.mp = MediaPlayer.create(MainActivity.this, media_active);
                pause = true;
                toggleButton.setChecked(false);
                image.clearAnimation();

                Log.d("mp null", "mp null");
            }
        }
        save();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        RecycleMusicAdapter recycleMusicAdapter = new RecycleMusicAdapter(this, list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recycleMusicAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                pause = true;
                toggleButton.setChecked(false);
                MyService.mp.stop();
                MyService.mp = MediaPlayer.create(MainActivity.this, list.get(position).getMedia());
                media_active = list.get(position).getMedia();
                image.setImageResource(list.get(position).getImage());
                name.setText(list.get(position).getName());
                toggleButton.setChecked(true);
                pause = false;
                save();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        toggleButtonReplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    replay = true;
                    save();
                } else {
                    replay = false;
                    save();
                }
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (MyService.mp != null) {
                        MyService.mp.start();
                        pause = false;
                        progressBar.setMax(MyService.mp.getDuration() / 1000);
                        new Thread() {
                            @Override
                            public void run() {
                                for (int i = MyService.mp.getCurrentPosition(); i < MyService.mp.getDuration(); i = i + 1000) {
                                    try {
                                        progressBar.setProgress(MyService.mp.getCurrentPosition() / 1000);
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ex) {
                                        break;
                                    }
                                }
                            }
                        }.start();
                        image.startAnimation(animation);
                        MyService.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (replay == true) {
                                    replay();
                                } else
                                    autoPlay();
                            }
                        });
                        save();
                    }
                } else {
                    if (MyService.mp != null) {
                        MyService.mp.pause();
                        pause = true;
                        image.clearAnimation();
                        save();
                    }
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause = true;
                toggleButton.setChecked(false);
                for (int i = 0; i < list.size(); i++) {
                    if (media_active == list.get(i).getMedia()) {
                        if (i == 0)
                            media_active = list.get(list.size() - 1).getMedia();
                        else
                            media_active = list.get(i - 1).getMedia();
                        break;
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    if (media_active == list.get(i).getMedia()) {
                        MyService.mp.stop();
                        MyService.mp = MediaPlayer.create(MainActivity.this, list.get(i).getMedia());
                        media_active = list.get(i).getMedia();
                        image.setImageResource(list.get(i).getImage());
                        name.setText(list.get(i).getName());
                        toggleButton.setChecked(true);
                        pause = false;
                        save();
                        break;
                    }
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause = true;
                toggleButton.setChecked(false);
                for (int i = 0; i < list.size(); i++) {
                    if (media_active == list.get(i).getMedia()) {
                        if (i == list.size() - 1)
                            media_active = list.get(0).getMedia();
                        else
                            media_active = list.get(i + 1).getMedia();
                        break;
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    if (media_active == list.get(i).getMedia()) {
                        MyService.mp.stop();
                        MyService.mp = MediaPlayer.create(MainActivity.this, list.get(i).getMedia());
                        media_active = list.get(i).getMedia();
                        image.setImageResource(list.get(i).getImage());
                        name.setText(list.get(i).getName());
                        toggleButton.setChecked(true);
                        pause = false;
                        save();
                        break;
                    }
                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onDestroy() {
        if (pause == true) {
            stopService(intent);
        } else {
            showNotification(getNotification());
        }
        save();
        super.onDestroy();
    }

    public void replay() {
        if (MyService.mp != null) {
            toggleButton.setChecked(false);
            toggleButton.setChecked(true);
            MyService.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (replay == true) {
                        replay();
                    } else
                        autoPlay();
                }
            });
        }
    }

    public void autoPlay() {
        pause = true;
        toggleButton.setChecked(false);
        for (int i = 0; i < list.size(); i++) {
            if (media_active == list.get(i).getMedia()) {
                if (i == list.size() - 1)
                    media_active = list.get(0).getMedia();
                else
                    media_active = list.get(i + 1).getMedia();
                break;
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (media_active == list.get(i).getMedia()) {
                MyService.mp.stop();
                MyService.mp = MediaPlayer.create(MainActivity.this, list.get(i).getMedia());
                media_active = list.get(i).getMedia();
                image.setImageResource(list.get(i).getImage());
                name.setText(list.get(i).getName());
                toggleButton.setChecked(true);
                pause = false;
                save();
                break;
            }
        }
        MyService.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                autoPlay();
            }
        });
    }

    public void save() {
        SharedPreferences sharedPreferences = getSharedPreferences("music.txt", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = -1;
        int img = -1;
        String name = "";
        int media = -1;
        for (int i = 0; i < list.size(); i++) {
            if (media_active == list.get(i).getMedia()) {
                id = list.get(i).getId();
                img = list.get(i).getImage();
                name = list.get(i).getName();
                media = list.get(i).getMedia();
                break;
            }
        }
        editor.putInt("id", id);
        editor.putInt("img", img);
        editor.putString("name", name);
        editor.putInt("media", media);
        editor.putBoolean("pause", pause);
        editor.putBoolean("replay", replay);
        editor.commit();
    }

    public void restore(Music music) {
        SharedPreferences sharedPreferences = getSharedPreferences("music.txt", MODE_PRIVATE);
        music.setId(sharedPreferences.getInt("id", -1));
        music.setImage(sharedPreferences.getInt("img", -1));
        music.setName(sharedPreferences.getString("name", ""));
        music.setMedia(sharedPreferences.getInt("media", -1));
        pause = sharedPreferences.getBoolean("pause", false);
        replay = sharedPreferences.getBoolean("replay", false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showNotification(Notification notification) {
        Intent notificationIntent = new Intent(this, MyReciever.class);
        notificationIntent.putExtra("noti", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, pendingIntent);
    }

    private Notification getNotification() {
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification);
        notificationLayout.setOnClickPendingIntent(R.id.play_noti,onButtonNotificationClick(R.id.play_noti));
        notificationLayout.setOnClickPendingIntent(R.id.pause_noti,onButtonNotificationClick(R.id.pause_noti));
        notificationLayout.setOnClickPendingIntent(R.id.prev_noti,onButtonNotificationClick(R.id.prev_noti));
        notificationLayout.setOnClickPendingIntent(R.id.next_noti,onButtonNotificationClick(R.id.next_noti));

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),1,resultIntent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notification-id");
        builder.setSmallIcon(R.drawable.ic_music);
        builder.setContentTitle("Music is playing");
        builder.setCustomBigContentView(notificationLayout);
        builder.setAutoCancel(true);
        builder.setContentIntent(resultPendingIntent);
        return builder.build();
    }
    private PendingIntent onButtonNotificationClick(@IdRes int id) {
        Intent intent = new Intent(this,MyReciever2.class);
        intent.putExtra("btn", id);
        return PendingIntent.getBroadcast(this, id, intent, 0);
    }
}


