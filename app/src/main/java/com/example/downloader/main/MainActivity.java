package com.example.downloader.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.downloader.R;
import com.example.downloader.services.DownloadService;

public class MainActivity extends AppCompatActivity {

    private TextView statusTextView;
    private Button downloadBtn;
    private EditText linkEditText;
    private EditText fileNameEditText;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String string = bundle.getString(DownloadService.FILEPATH);
                int resultCode = bundle.getInt(DownloadService.RESULT);
                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this,
                            "Download finish. Download URI: " + string,
                            Toast.LENGTH_LONG).show();
                    statusTextView.setText("Download done");
                } else {
                    Toast.makeText(MainActivity.this, "Download failed",
                            Toast.LENGTH_LONG).show();
                    statusTextView.setText("Download failed");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        requestFilePermission();
    }

    private void initViews() {
        statusTextView = findViewById(R.id.status_text_view_id);
        downloadBtn = findViewById(R.id.download_button_id);
        linkEditText = findViewById(R.id.download_link_text_edit_id);
        fileNameEditText = findViewById(R.id.download_file_name_text_edit_id);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DownloadService.class);

                intent.putExtra(DownloadService.FILENAME, fileNameEditText.getText().toString());
                intent.putExtra(DownloadService.URLPATH, linkEditText.getText().toString());
                startService(intent);
                statusTextView.setText("Service started");
            }
        });
    }

    private void requestFilePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

}
