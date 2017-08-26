package com.kry.adpreferencecompatexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {
    private AdPreferencesFragment mAdPreferencesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdPreferencesFragment = new AdPreferencesFragment();

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.pref_layout, mAdPreferencesFragment, null)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
