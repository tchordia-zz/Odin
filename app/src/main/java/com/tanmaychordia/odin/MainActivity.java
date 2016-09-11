package com.tanmaychordia.odin;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button button;
    final String apk="newapk.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.b_refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get();
            }
        });
    }

    public void get() {
        String downloadCompleteIntentName = DownloadManager.ACTION_DOWNLOAD_COMPLETE;
        IntentFilter downloadCompleteIntentFilter = new IntentFilter(downloadCompleteIntentName);

        final DownloadManager d = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://97.107.138.168:8080/NewApp-debug.apk"));
        request.setDestinationInExternalFilesDir(this, null, "newapk.apk");
        final long id = d.enqueue(request);

        String path = "file://" + getExternalFilesDir(null).getPath() + "/" + apk;
        File file = new File(getExternalFilesDir(null).getPath() + "/" + apk);
        if (file.exists()) {
            file.delete();
        }
        System.out.println(path);
        final Uri uri = Uri.parse(path);
        BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("Got apk");
                try {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, d.getMimeTypeForDownloadedFile(id));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                } catch (Exception e) { e.printStackTrace();}
            }
        };

        registerReceiver(downloadCompleteReceiver, downloadCompleteIntentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

        new AsyncTask<Void,Void,Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }
        }.execute(null, null, null);
        return super.onOptionsItemSelected(item);

    }


}
