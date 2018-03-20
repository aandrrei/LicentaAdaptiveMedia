package com.example.andre.licentaadaptivemedia;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by andre on 20.02.2018.
 */

public class SongAdapter extends android.widget.BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    public SongAdapter(Context c, ArrayList<Song> songsList) {
        songs = songsList;
        songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // mapam la song layout
        LinearLayout songLay = (LinearLayout) songInf.inflate(R.layout.song, parent, false);

        // obtinem artistul si numele melodiei
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);

        // obtinem melodia folosind pozitia
        Song currentSong = songs.get(position);

        // obtinem stringuri cu titlul si artistul
        songView.setText(currentSong.getTitle());
        artistView.setText(currentSong.getArtist());

        // setam pozitia ca tag
        songLay.setTag(position);

        return songLay;
    }
}
