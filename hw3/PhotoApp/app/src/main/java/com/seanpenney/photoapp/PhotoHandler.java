package com.seanpenney.photoapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sean on 5/6/2015.
 */
public class PhotoHandler implements Camera.PictureCallback {
    private final Context context;
    EditText caption;

    public PhotoHandler(Context context, EditText caption) {
        this.context = context;
        this.caption = caption;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Bitmap photo = BitmapFactory.decodeByteArray(data, 0, data.length).copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvasView = new Canvas(photo);
        Paint paintText = new Paint();
        paintText.setTextSize(52);
        paintText.setColor(Color.BLACK);

        Paint paintBackground = new Paint();
        paintBackground.setColor(Color.LTGRAY);
        paintBackground.setAlpha(60);

        canvasView.save();
        canvasView.rotate((float) (90), 50, 50);
        canvasView.drawRect(0, 0, canvasView.getMaximumBitmapWidth(), 100, paintBackground);
        canvasView.drawText(caption.getText().toString(), 0, 50, paintText);
        canvasView.restore();

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
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Matrix matrix = new Matrix();
            matrix.postRotate((float) -90);
            photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, false);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapData = bos.toByteArray();
            outputStream.write(bitmapData);
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