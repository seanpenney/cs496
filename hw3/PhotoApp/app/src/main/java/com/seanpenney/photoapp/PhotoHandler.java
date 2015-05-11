package com.seanpenney.photoapp;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sean on 5/6/2015.
 */
public class PhotoHandler implements Camera.PictureCallback {
    private final Context context;

    public PhotoHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Log.d("PhotoHandler", "Can't create directory to save image");
            Toast.makeText(context, "Can't create directory to save image", Toast.LENGTH_LONG).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" + date + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream outputStream = new FileOutputStream(pictureFile);
            outputStream.write(data);
            outputStream.close();
            scanFile(pictureFile.getAbsolutePath());  // updates Gallery immediately
            Toast.makeText(context, "New image saved: " + photoFile, Toast.LENGTH_LONG).show();
        } catch (Exception error) {
            Log.d("PhotoHandler", "File " + filename + " not saved: " + error.getMessage());
            Toast.makeText(context, "Image could not be saved", Toast.LENGTH_LONG).show();
        }
    }

    private File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "CameraFolder");
    }

    private void scanFile(String path) {
        MediaScannerConnection.scanFile(context.getApplicationContext(),
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("PhotoHandler", "Finished scanning " + path);
                    }
                });
    }
}