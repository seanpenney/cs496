package com.seanpenney.photoapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sean on 5/13/2015.
 */
public class ViewPhotos extends ActionBarActivity {
    ListView listView;
    ImageView photoDisplay;
    File[] photoFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);
        listView = (ListView) findViewById(R.id.list);
        photoDisplay = (ImageView) findViewById(R.id.photo_display);
        photoFiles = getPhotosFromStorage();

        setupList();

    }

    private void setupList() {
        List<String> fileNamesList = new LinkedList<>();

        for (File f : photoFiles) {
            fileNamesList.add(f.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, fileNamesList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                photoDisplay.setImageBitmap(BitmapFactory.decodeFile(photoFiles[position].getAbsolutePath()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private File[] getPhotosFromStorage() {
        File[] photoFiles;

        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File pictureDir = new File(sdDir, "CameraFolder");

        if (!pictureDir.exists()) {
            Log.d("ViewPhotos", "No picture directory");
            Toast.makeText(getApplicationContext(), "No picture directory", Toast.LENGTH_LONG).show();
            return null;
        }

        photoFiles = pictureDir.listFiles();
        return photoFiles;
    }
}
