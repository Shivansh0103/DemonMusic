package com.example.demonmusic;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ArrayList<File> mySongs=fetchSongs(Environment.getExternalStorageDirectory());
                        String[] items=new String[mySongs.size()];
                        for(int i=0;i<mySongs.size();i++)
                        {
                            items[i]=mySongs.get(i).getName().replace(".mp3","");
                        }
                        CustomAdapter ad= new CustomAdapter(MainActivity.this, R.layout.mylayout, items);
                        listView.setAdapter(ad);
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Intent intent=new Intent(MainActivity.this,PlaySong.class);
                            String currSong=listView.getItemAtPosition(position).toString();
                            intent.putExtra("SongList",mySongs);
                            intent.putExtra("CurrentSong",currSong);
                            intent.putExtra("Position",position);
                            startActivity(intent);
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    ArrayList<File> fetchSongs(File file)
    {
        ArrayList<File> l=new ArrayList<>();
        File[] songs=file.listFiles();
        if(songs!=null) {
            for (File myFile : songs) {
                if(myFile.isDirectory() && !myFile.isHidden())
                l.addAll(fetchSongs(myFile));
                else
                {
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith("."))
                        l.add(myFile);
                }
            }
        }
        return l;
    }
}