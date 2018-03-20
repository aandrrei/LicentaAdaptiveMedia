package com.example.andre.licentaadaptivemedia;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;

/**
 * Created by andre on 20.02.2018.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    // media player
    private MediaPlayer player;
    // lista melodii
    private ArrayList<Song> songs;
    // pozitia curenta
    private int songPosn;

    private final IBinder musicBind = new MusicBinder();

    private String songTitle = "";

    private static final int NOTIFY_ID = 1;

    private boolean shuffle = false;
    private Random rand;

    @Override
    public void onCreate() {
        super.onCreate();

        // initializare pozitie
        songPosn = 0;

        // creare player
        player = new MediaPlayer();

        // initializare player
        initMusicPlayer();

        rand = new Random();

    }

    // metoda pt initializare player
    public void initMusicPlayer() {
        //setare proprietati player
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> songsList) {
        songs = songsList;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    //playback methods
    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //player.stop();
        //player.release();
        return false;
    }

    public void playSong() {
        //play o melodie
        player.reset();

        //obtinere melodie
        Song playSong = songs.get(songPosn);
        //obtinere id
        long currentSong = playSong.getId();

        songTitle = playSong.getTitle();
        //setare uri
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
    }

    public void setSong(int songIndex){
        songPosn = songIndex;
    }

    public void playPrev() {
        songPosn--;
        if (songPosn < 0)
            songPosn = songs.size() - 1;
        playSong();
    }

    public void playNext() {
        if (shuffle) {
            int newSong = songPosn;
            while (newSong == songPosn) {
                newSong = rand.nextInt(songs.size());
            }
            songPosn = newSong;
        } else {
            songPosn++;
            if (songPosn >= songs.size())
                songPosn = 0;
        }
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        player.stop();
        player.release();
    }

    public void setShuffle() {
        if (shuffle)
            shuffle = false;
        else
            shuffle = true;
    }

}
