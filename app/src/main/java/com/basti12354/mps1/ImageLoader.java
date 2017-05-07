package com.basti12354.mps1;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by Basti on 01.05.2017.
 */

public class ImageLoader extends IntentService {
    private int result = Activity.RESULT_CANCELED;
    public static final String RESULT = "result";
    public static final String NAME = "name";
    public static final String IMAGE_POSITION = "image_position";
    private static String TAG = "ImageLoader";
    public static String NOTIFICATION = "com.basti12354.android.service.receiver";

    public ImageLoader() {
        super("ImageLoader");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandle!");
        Bitmap bitmap = getBitmapFromURL("http://mytoys.scene7.com/is/image/myToys/ext/3639850-01.jpg?wid=400&hei=400&fmt=jpeg&qlt=25,1&resMode=trilin&op_usm=0.9,1,5,1", "Logo1",0);
        Bitmap bitmap2 = getBitmapFromURL("https://images-na.ssl-images-amazon.com/images/I/817XeVCxM6L._SY355_.jpg", "Logo2",1);
        Bitmap bitmap3 = getBitmapFromURL("https://static1.wall-art.de/out/pictures/generated/product/2/780_542_80/Wandaufkleber_Bayern_Muenchen_Logo_einzel.jpg", "Logo3",2);
        Bitmap bitmap4 = getBitmapFromURL("http://img.webme.com/pic/s/schlaudraff/fcb-logo_-_xvii_g_kr.jpg", "Logo4",3);
        Bitmap bitmap5 = getBitmapFromURL("https://upload.wikimedia.org/wikipedia/de/thumb/archive/8/84/20140112215554%21FC_Bayern_M%C3%BCnchen_Logo_%281965-1970%29.svg/658px-FC_Bayern_M%C3%BCnchen_Logo_%281965-1970%29.svg.png", "Logo5",4);

    }




    public Bitmap getBitmapFromURL(String src, String name, int downloadPosition) {
        try {
            Log.d(TAG, "getBitmapFromURL!");
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            //save Bitmap
            saveImageToStorage(getApplicationContext(), myBitmap,  name + ".png", downloadPosition);


            Log.d(TAG, "getBitmapFromURL" + " READY");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveImageToStorage(Context context, Bitmap bitmap, String fileName, int downloadPosition){
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();

            // send result to MainActivity
            result = Activity.RESULT_OK;
            sendBroadcastToActivity(result, fileName, downloadPosition);
        } catch (Exception e) {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }

    private void sendBroadcastToActivity(int result, String name, int downloadPosition){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        intent.putExtra(NAME, name);
        intent.putExtra(IMAGE_POSITION, downloadPosition);
        sendBroadcast(intent);
        Log.d(TAG, "sendBroadcastToActivity");
    }
}
