package me.franciscoigor.mediaplayersample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<File> files;
    int currentFile=-1;
    VideoView media;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        files = new ArrayList<File>();
        media = findViewById(R.id.videoView);

        final ImageButton prevButton = findViewById(R.id.prevButton);
        final ImageButton playButton = findViewById(R.id.playButton);
        final ImageButton nextButton = findViewById(R.id.nextButton);

        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(prevButton)){
                    System.out.println("PREV");
                    if (currentFile>=0) {
                        prevMedia();
                    }
                }
                if (v.equals(playButton)){
                    System.out.println("PLAY");
                    if (currentFile>=0){
                        playMedia(files.get(currentFile));
                    }
                }
                if (v.equals(nextButton)){
                    System.out.println("NEXT");
                    if (currentFile>=0) {
                        nextMedia();
                    }
                }
            }
        };

        prevButton.setOnClickListener(listener);
        playButton.setOnClickListener(listener);
        nextButton.setOnClickListener(listener);


    }

    private void playMedia(File file){
        updateTitle();

        media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
        media.setVideoPath(files.get(currentFile).getAbsolutePath());
        media.start();
    }

    private void nextMedia(){
        if (currentFile<files.size()-1){
            currentFile++;
        }else{
            currentFile = 0;
        }
        playMedia(files.get(currentFile));
    }

    private void prevMedia(){
        if (currentFile>0){
            currentFile--;
        }else{
            currentFile = files.size()-1;
        }
        playMedia(files.get(currentFile));
    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("START");
        checkPermissions();
        appendSongList(files);

    }

    private void appendSongList(ArrayList<File> list){

        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        System.out.println("Files: "+ directory.getAbsolutePath());
        File[] files = directory.listFiles();
        System.out.println(files);
        if (files==null) return;
        System.out.println("Files: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            System.out.println("FileName:" + files[i]);
            if (!list.contains(files[i])){
                list.add(files[i]);
            }
        }
        System.out.println(list);
        if (list.size()>0 && currentFile==-1){
            currentFile=0;
            updateTitle();
        }

    }

    private void updateTitle() {
        File file=files.get(currentFile);
        setTitle(String.format("%d. %s",currentFile+1,file.getName()));
    }

    public final String[] EXTERNAL_PERMS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public final int EXTERNAL_REQUEST = 138;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EXTERNAL_REQUEST){
            appendSongList(files);
        }
    }

    public boolean checkPermissions() {

        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                isPermissionOn = false;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }
        return isPermissionOn;
    }

    public boolean canAccessExternalSd() {
        return (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));

    }
}
