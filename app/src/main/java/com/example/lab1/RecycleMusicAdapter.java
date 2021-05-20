package com.example.lab1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecycleMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private ArrayList<Music> list;
    public RecycleMusicAdapter(Context context,ArrayList<Music> list){
        this.context=context;
        this.list=list;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView id;
        public ImageView image;
        public TextView name;
        public TextView name_casi;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id=(TextView)itemView.findViewById(R.id.id_music);
            image=(ImageView)itemView.findViewById(R.id.image_music);
            name=(TextView)itemView.findViewById(R.id.name_music);
            name_casi=(TextView)itemView.findViewById(R.id.name_casi);

        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.music,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Music music=list.get(position);
        ((MyViewHolder)holder).id.setText(music.getId()+"");
        ((MyViewHolder)holder).image.setImageResource(music.getImage());
        ((MyViewHolder)holder).name.setText(music.getName()+"");
        ((MyViewHolder)holder).name_casi.setText(music.getName_casi()+"");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
