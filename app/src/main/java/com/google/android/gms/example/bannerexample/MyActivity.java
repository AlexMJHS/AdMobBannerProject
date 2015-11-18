/*
 * Copyright (C) 2013 Google, Inc.
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
package com.google.android.gms.example.bannerexample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem;
import android.widget.*;
import android.media.MediaPlayer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


/**
 * Main Activity. Inflates main activity xml and child fragments.
 */
public class MyActivity extends Activity implements Runnable
{

    private AdView myAdView;
    private Button startButton;
    private Button stopButton;
    private MediaPlayer soundPlayer;
    private SeekBar soundSeekBar;
    private Thread soundThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        myAdView = (AdView) findViewById(R.id.ad_view);
        startButton = (Button) findViewById(R.id.PlayButton);
        stopButton = (Button) findViewById(R.id.StopButton);
        soundSeekBar = (SeekBar) findViewById(R.id.SoundSeekBar);
        soundPlayer = MediaPlayer.create(this.getBaseContext(), R.raw.load);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        myAdView.loadAd(adRequest);

        setupListeners();

        soundThread = new Thread(this);
        soundThread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupListeners()
    {
        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                soundPlayer.start();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View currentView)
            {
                soundPlayer.stop();
                soundPlayer = MediaPlayer.create(getBaseContext(), R.raw.load);
            }
        });

        soundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(fromUser)
                {
                    soundPlayer.seekTo(progress);
                }
            }
        });
    }

    @Override
    public void run()
    {
        int currentPosition = 0;
        int soundTotal = soundPlayer.getDuration();
        soundSeekBar.setMax(soundTotal);

        while (soundPlayer != null && currentPosition < soundTotal)
        {
            try
            {
                Thread.sleep(300);
                currentPosition = soundPlayer.getCurrentPosition();
            }
            catch(InterruptedException soundException)
            {
                return;
            }
            catch(Exception otherException)
            {
                return;
            }
            soundSeekBar.setProgress(currentPosition);
        }

    }

    @Override
    public void onPause()
    {
        if (myAdView != null)
        {
            myAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (myAdView != null)
        {
            myAdView.resume();
        }
    }

    @Override
    public void onDestroy()
    {
        if (myAdView != null)
        {
            myAdView.destroy();
        }
        super.onDestroy();
    }

}
