package asbridged.me.uk.gphoto.activities;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.Manifest;
        import android.app.Activity;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.widget.TextView;

        import asbridged.me.uk.gphoto.R;

        import java.util.Random;

        import asbridged.me.uk.gphoto.helper.LogHelper;


/**
 * The main activity launched when the app starts (in example was call "Now Playing Activity"
 * This just detects whether on TV or Mobile and launches the appropriate activity
 * In my case I am only testing/running on mobile, so really it just starts FullScreenPlayQueue Activity.
 * https://developer.android.com/training/tv/playback/now-playing.html
 *
 * This activity determines which activity to launch based on the current UI mode.
 */
public class GetPermissionsActivity extends Activity {

    private static final String TAG = LogHelper.makeLogTag(GetPermissionsActivity.class);

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    /**
     * The time that the splash screen will be on the screen in milliseconds.
     */
    private static int                 TIMEOUT_IN_MS       = 5000;

    /** The time when this {@link Activity} was created. */
    private long                startTimeMillis     = 0;

    /** A random number generator for the background colors. */
    private static final Random random              = new Random();

    /**
     * The TextView which is used to inform the user whether the permissions are
     * granted.
     */
    private TextView tvMessage = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.i(TAG, "onCreate");

        setContentView(R.layout.activity_getpermissions);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvMessage.setText("Waiting");

        /**
         * Save the start time of this Activity, which will be used to determine
         * when the splash screen should timeout.
         */
        startTimeMillis = System.currentTimeMillis();
        LogHelper.i(TAG, "check permissiions");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            startNextActivity();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    startNextActivity();
                } else {
                    // permission denied, boo!"
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    private void startNextActivity() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                tvMessage.setText("Granted...");
            }
        });
        long delayMillis = TIMEOUT_IN_MS - (System.currentTimeMillis() - startTimeMillis);
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(GetPermissionsActivity.this, getNextActivityClass()));
                finish();
            }
        }, delayMillis);
    }

    private Class getNextActivityClass() {
            return OptionDynamicListActivity.class;
    }



}
