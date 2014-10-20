
package com.vishal.flashlight;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

    private volatile Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().hide();

        // Request for Ads
        AdRequest.Builder adRequest = new AdRequest.Builder();

        boolean isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        if (isDebuggable) {

            String android_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String deviceId = md5(android_id).toUpperCase();
            adRequest.addTestDevice(deviceId);
            boolean isTestDevice = adRequest.build().isTestDevice(this);

            Log.v("VISHAL", "is Admob Test Device ? " + deviceId + " " + isTestDevice);
        }
        // Locate the Banner Ad in activity_main.xml
        AdView adView = (AdView) findViewById(R.id.adView);
        // Load ads into Banner Ads
        adView.loadAd(adRequest.build());

        boolean b = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        Log.d("VISHAL", "Flash Availabilty: " + b);

        camera = Camera.open();

        new Thread(new Runnable() {

            @Override
            public void run() {
                SeekBar seekBar = (SeekBar) findViewById(R.id.strobeSpeed);
                while (true) {
                    if (seekBar.getProgress() > 0) {
                        switchOn();
                        try {
                            Thread.sleep((seekBar.getProgress() / 2 * 10));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        switchOff();
                        try {
                            Thread.sleep((seekBar.getProgress() / 2 * 10));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public void onToggleClicked(View view) {
        ToggleButton lightSwitch = (ToggleButton) findViewById(R.id.togglebutton);

        try {
            if (lightSwitch.isChecked()) {
                switchOn();
            } else {
                switchOff();
            }
        } catch (Exception e) {
            Log.e("VISHAL", "Exception: ", e);
        }
    }

    private void switchOn() {
        Parameters p = camera.getParameters();
        camera.unlock();
        p.setFlashMode(Parameters.FLASH_MODE_TORCH);
        camera.lock();
        camera.setParameters(p);
        camera.startPreview();
    }

    private void switchOff() {
        Parameters p = camera.getParameters();
        camera.unlock();
        p.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.lock();
        camera.setParameters(p);
        camera.stopPreview();
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // Logger.logStackTrace(TAG, e);
        }
        return "";
    }
}
