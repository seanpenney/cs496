package com.seanpenney.photoapp;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Sean on 5/5/2015.
 */
public class MainActivity extends ActionBarActivity {
    private Camera camera;
    private int cameraId = 0;
    SurfaceView surfaceViewPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceViewPreview = (SurfaceView) findViewById(R.id.surfaceView1);

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
                surfaceViewPreview.getLayoutParams().height = (int) (.75 * params.getPreviewSize().width);
                surfaceViewPreview.getLayoutParams().width = (int) (.75 * params.getPreviewSize().height);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
        camera.takePicture(null, null, new PhotoHandler(getApplicationContext()));
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
}