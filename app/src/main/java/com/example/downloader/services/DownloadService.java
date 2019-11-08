package com.example.downloader.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class DownloadService extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String URLPATH = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.example.downloader.services";


    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String fileName = intent.getStringExtra(FILENAME);
        String urlPath = intent.getStringExtra(URLPATH);
        File output = new File(Environment.getExternalStorageDirectory(), fileName);

        if (output.exists()) {
            boolean isDeleted = output.delete();
            Toast.makeText(this, "is Deleted: " + isDeleted, Toast.LENGTH_SHORT).show();
        }

        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            URL url = new URL(urlPath);
            inputStream = url.openConnection().getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            fileOutputStream = new FileOutputStream(output.getPath());
            int next = -1;
            while ((next = reader.read()) != -1) {
                fileOutputStream.write(next);
            }
            result = Activity.RESULT_OK;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        publishResults(output.getAbsolutePath(), result);
    }

    private void publishResults(String outputPath, int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(FILEPATH, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

}
