package com.example.lab1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Music> list;
    private ToggleButton toggleButton,toggleButtonReplay;
    private int media_active;
    private ImageView image;
    private TextView name;
    private ImageView prev,next;
    private boolean pause;
    private boolean replay;
    private Intent intent;
    private ProgressBar progressBar;
    private Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerView);
        toggleButton=findViewById(R.id.toggle);
        toggleButtonReplay=findViewById(R.id.toggle_replay);
        image=findViewById(R.id.image);
        name=findViewById(R.id.name);
        prev=findViewById(R.id.prev);
        next=findViewById(R.id.next);
        progressBar=findViewById(R.id.progessBar);
        intent=new Intent(MainActivity.this,MyService.class);

        list=new ArrayList<>();
        list.add(new Music(1,R.drawable.tutam,"Tự tâm",R.raw.tutam));
        list.add(new Music(2,R.drawable.hoanokhongmau,"Hoa nở không màu",R.raw.hoanokhongmau));
        list.add(new Music(3,R.drawable.niuduyen,"Níu duyên",R.raw.niuduyen));
        list.add(new Music(4,R.drawable.thethai,"Thế thái",R.raw.thethai));
        list.add(new Music(5,R.drawable.tinhbandieuky,"Tình bạn diệu kỳ",R.raw.tinhbandieuky));
        media_active=list.get(0).getMedia();
        Music music=new Music();
        restore(music);
        media_active=music.getMedia();
        image.setImageResource(music.getImage());
        name.setText(music.getName());
        intent.putExtra("media",media_active);
        toggleButton.setChecked(true);
        myThread();
        if (pause == true) {
            stopService(intent);
            startService(intent);
            pause=true;
            toggleButton.setChecked(false);
            save();
        }
        toggleButtonReplay.setChecked(replay);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(MainActivity.this);
        RecycleMusicAdapter recycleMusicAdapter=new RecycleMusicAdapter(this,list);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recycleMusicAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                pause=true;
                toggleButton.setChecked(false);
                MyService.mp.stop();
                MyService.mp=MediaPlayer.create(MainActivity.this,list.get(position).getMedia());
                media_active=list.get(position).getMedia();
                image.setImageResource(list.get(position).getImage());
                name.setText(list.get(position).getName());
                toggleButton.setChecked(true);
                pause=false;
                save();
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        toggleButtonReplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    replay=true;
                }
                else{
                    replay=false;
                }
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(MyService.mp!=null){

                        MyService.mp.start();
                        pause=false;
                        save();
                        progressBar.setMax(MyService.mp.getDuration()/1000);
                        thread=new Thread(){
                            @Override
                            public void run() {
                                for (int i = MyService.mp.getCurrentPosition(); i < MyService.mp.getDuration(); i=i+1000) {
                                    try {
                                        progressBar.setProgress(MyService.mp.getCurrentPosition()/1000);
                                        Thread.sleep(1000);
                                    }
                                    catch (InterruptedException ex) {
                                        break;
                                    }
                                }
                            }
                        };
                        thread.start();
                        MyService.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if(replay==true){
                                    MyService.mp.start();
                                    myThread();
                                }
                                else
                                    autoPlay();
                            }
                        });
                    }
                } else {
                    if(MyService.mp!=null){
                        MyService.mp.pause();
                        pause=true;
                        save();
                    }
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause=true;
                toggleButton.setChecked(false);
                for(int i=0;i<list.size();i++){
                    if(media_active==list.get(i).getMedia()){
                        if(i==0)
                            media_active=list.get(list.size()-1).getMedia();
                        else
                            media_active=list.get(i-1).getMedia();
                        break;
                    }
                }
                for(int i=0;i<list.size();i++){
                    if(media_active==list.get(i).getMedia()){
                        MyService.mp.stop();
                        MyService.mp=MediaPlayer.create(MainActivity.this,list.get(i).getMedia());
                        media_active=list.get(i).getMedia();
                        image.setImageResource(list.get(i).getImage());
                        name.setText(list.get(i).getName());
                        toggleButton.setChecked(true);
                        pause=false;
                        save();
                        break;
                    }
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause=true;
                toggleButton.setChecked(false);
                for(int i=0;i<list.size();i++){
                    if(media_active==list.get(i).getMedia()){
                        if(i==list.size()-1)
                            media_active=list.get(0).getMedia();
                        else
                            media_active=list.get(i+1).getMedia();
                        break;
                    }
                }
                for(int i=0;i<list.size();i++){
                    if(media_active==list.get(i).getMedia()){
                        MyService.mp.stop();
                        MyService.mp=MediaPlayer.create(MainActivity.this,list.get(i).getMedia());
                        media_active=list.get(i).getMedia();
                        image.setImageResource(list.get(i).getImage());
                        name.setText(list.get(i).getName());
                        toggleButton.setChecked(true);
                        pause=false;
                        save();
                        break;
                    }
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        Toast.makeText(MainActivity.this,"onDestroy",Toast.LENGTH_SHORT).show();
        if(pause==true){
            stopService(intent);
        }
        super.onDestroy();
    }

    public void autoPlay(){
        pause=true;
        toggleButton.setChecked(false);
        for(int i=0;i<list.size();i++){
            if(media_active==list.get(i).getMedia()){
                if(i==list.size()-1)
                    media_active=list.get(0).getMedia();
                else
                    media_active=list.get(i+1).getMedia();
                break;
            }
        }
        for(int i=0;i<list.size();i++){
            if(media_active==list.get(i).getMedia()){
                MyService.mp.stop();
                MyService.mp=MediaPlayer.create(MainActivity.this,list.get(i).getMedia());
                media_active=list.get(i).getMedia();
                image.setImageResource(list.get(i).getImage());
                name.setText(list.get(i).getName());
                toggleButton.setChecked(true);
                pause=false;
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

    public void save(){
        SharedPreferences sharedPreferences=getSharedPreferences("music.txt",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        int id = -1;
        int img = -1;
        String name = "";
        int media = -1;
        for(int i=0;i<list.size();i++){
            if(media_active==list.get(i).getMedia()){
                id=list.get(i).getId();
                img=list.get(i).getImage();
                name=list.get(i).getName();
                media=list.get(i).getMedia();
                break;
            }
        }
        editor.putInt("id",id);
        editor.putInt("img",img);
        editor.putString("name",name);
        editor.putInt("media",media);
        editor.putBoolean("pause",pause);
        editor.putBoolean("replay",replay);
        editor.commit();
    }
    public void restore(Music music){
        SharedPreferences sharedPreferences=getSharedPreferences("music.txt",MODE_PRIVATE);
        music.setId(sharedPreferences.getInt("id",-1));
        music.setImage(sharedPreferences.getInt("img",-1));
        music.setName(sharedPreferences.getString("name",""));
        music.setMedia(sharedPreferences.getInt("media",-1));
        pause=sharedPreferences.getBoolean("pause",false);
        replay=sharedPreferences.getBoolean("replay",false);
    }
    public void myThread(){
        if(MyService.mp!=null){
            thread=new Thread(){
                @Override
                public void run() {
                    for (int i = MyService.mp.getCurrentPosition(); i < MyService.mp.getDuration(); i=i+1000) {
                        try {
                            progressBar.setProgress(MyService.mp.getCurrentPosition()/1000);
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException ex) {
                            break;
                        }
                    }
                }
            };
            thread.start();
        }
    }
}