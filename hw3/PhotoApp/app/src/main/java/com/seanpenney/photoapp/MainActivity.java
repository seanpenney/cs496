package com.seanpenney.photoapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Sean on 5/5/2015.
 */
public class MainActivity extends ActionBarActivity {
    private Camera camera;
    private int cameraId = 0;
    SurfaceView surfaceViewPreview;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private EditText caption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        caption = (EditText) findViewById(R.id.caption);
        surfaceViewPreview = (SurfaceView) findViewById(R.id.surfaceView1);

        addDrawerItems();
        setupDrawer();

        /* Add action bar toggle switch */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
            finish();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);

                camera.setDisplayOrientation(90);

                Camera.Parameters params = camera.getParameters();
                surfaceViewPreview.getLayoutParams().height = (int) (.90 * params.getPreviewSize().width);
                surfaceViewPreview.getLayoutParams().width = (int) (.90 * params.getPreviewSize().height);
            }
        }

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void takePicture(View view) {
        if (camera != null) {
            Log.d("MainActivity", "takePicture(), camera is NOT null");
        } else {
            Log.d("MainActivity", "takePicture(), camera is null");
            camera = android.hardware.Camera.open(cameraId);
            camera.setDisplayOrientation(90);
        }
        camera.takePicture(null, null, new PhotoHandler(getApplicationContext(), caption));
    }

    public void previewPicture(View view) {
        if (camera != null) {
            Log.d("MainActivity", "previewPicture(), camera is NOT null");
        } else {
            Log.d("MainActivity", "previewPicture(), camera is null");
            camera = android.hardware.Camera.open(cameraId);
            camera.setDisplayOrientation(90);
        }

        try {
            camera.setPreviewDisplay(surfaceViewPreview.getHolder());
            camera.startPreview();
        } catch (IOException e) {
            Log.d("MainActivity", "Start preview error " + e.toString());
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d("MainActivity", "Front facing camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    private void addDrawerItems() {
        String[] itemsArray = {"Take Photos", "View Photos", "About"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemsArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Intent startViewPhotos = new Intent(MainActivity.this, ViewPhotos.class);
                    MainActivity.this.startActivity(startViewPhotos);
                } else {
                    Toast.makeText(MainActivity.this, "Sorry, not implemented yet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
}