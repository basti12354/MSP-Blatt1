package com.basti12354.mps1;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button addBtn, killBtn;
    private BroadcastReceiver receiver;
    private static String TAG = "MainActivity";

    ArrayList<ImageView> imageViewList;
    ArrayList<ProgressBar> progressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeImageViewsAndAddToList();
        initializeProgressList();

        addBtn = (Button) findViewById(R.id.addBtn);
        killBtn = (Button) findViewById(R.id.killBtn);

        addBtn.setOnClickListener(this);
        killBtn.setOnClickListener(this);


        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                   // String string = bundle.getString(DownloadService.FILEPATH);
                    int resultCode = bundle.getInt(ImageLoader.RESULT);
                    String fileName = bundle.getString(ImageLoader.NAME);
                    int imagePosition = bundle.getInt(ImageLoader.IMAGE_POSITION);

                    if (resultCode == RESULT_OK) {
//                        Log.d(TAG, "onReceive");
//                        Log.d("ImageLoader", "SUCCESS!");

                        // make progressbar invisible!
                        ProgressBar progressBar = progressList.get(imagePosition);
                        progressBar.setVisibility(View.GONE);

                        // set Image to ImageView -> ImageView has to be set to VISIBLE!!!
                        ImageView imageView = imageViewList.get(imagePosition);
                        imageView.setImageBitmap(loadImageBitmap(getApplicationContext(), fileName));
                        imageView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(MainActivity.this, "Download failed",
                                Toast.LENGTH_LONG).show();
                       // textView.setText("Download failed");
                    }
                }
            }
        };

    }

    private void initializeProgressList() {
        progressList = new ArrayList<>();

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        ProgressBar progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);
        ProgressBar progressBar4 = (ProgressBar) findViewById(R.id.progressBar4);
        ProgressBar progressBar5 = (ProgressBar) findViewById(R.id.progressBar5);

        progressList.addAll(Arrays.asList(progressBar, progressBar2, progressBar3, progressBar4, progressBar5));
    }

    private void initializeImageViewsAndAddToList() {
        imageViewList = new ArrayList<>();

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        ImageView imageView3 = (ImageView) findViewById(R.id.imageView3);
        ImageView imageView4 = (ImageView) findViewById(R.id.imageView4);
        ImageView imageView5 = (ImageView) findViewById(R.id.imageView5);

        imageViewList.addAll(Arrays.asList(imageView, imageView2, imageView3, imageView4, imageView5));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addBtn:
                startImageLoadingService();
                break;
            case R.id.killBtn:
                exitApp();
                break;
        }
    }

    private void startImageLoadingService() {
        startService(new Intent(this, ImageLoader.class));
    }


    public Bitmap loadImageBitmap(Context context, String imageName) {
//        File file            = getApplicationContext().getFileStreamPath(imageName);
//        if (file.exists()) Log.d("file", "my_image.jpeg exists!");

        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            fiStream    = context.openFileInput(imageName);
            bitmap      = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Log.d(TAG,  "loadImageBitmap(): Image can't open!");
            e.printStackTrace();
        }
        return bitmap;
    }

    private void exitApp(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(this.getString(R.string.exit_app));
        alertDialogBuilder.setMessage(this.getString(R.string.exit_app_dialog));

        //Dialog Buttons
        alertDialogBuilder.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                MainActivity.this.finish();
                System.exit(0);

            }
        });

        alertDialogBuilder.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {


            }
        });
        alertDialogBuilder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                ImageLoader.NOTIFICATION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

}
