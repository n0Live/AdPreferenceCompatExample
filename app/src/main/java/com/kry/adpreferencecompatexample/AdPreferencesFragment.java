package com.kry.adpreferencecompatexample;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import static com.kry.adpreferencecompatexample.AdUtils.getDefaultAdRequest;

public class AdPreferencesFragment extends android.support.v7.preference.PreferenceFragmentCompat {

    private static final String TAG = "AdsPreferencesFragment";
    private static final int MIN_AD_WIDTH = 280;
    private static final int AD_HEIGHT = 80;

    private AdView mAdView;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ViewGroup mAdPanel = (ViewGroup) view.findViewById(R.id.adPanel);

        if (mAdPanel != null) {
            ViewTreeObserver viewTreeObserver = mAdPanel.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    mAdPanel.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mAdPanel.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }

                                int dpViewWidth = AdUtils.convertPixelsToDp(mAdPanel.getWidth(),
                                        getContext());
                                int dpViewHeight = AdUtils.convertPixelsToDp(mAdPanel.getHeight(),
                                        getContext());
                                if (BuildConfig.DEBUG) Log.v(TAG,
                                        "Available space for ads: " + dpViewWidth + "x" +
                                                dpViewHeight + " dp");

                                if (dpViewWidth >= MIN_AD_WIDTH) {

                                    mAdView = new AdView(getContext());
                                    mAdView.setAdUnitId(
                                            getContext().getString(R.string.ADMOB_AD_UNIT_ID_2));
                                    mAdView.setAdSize(new AdSize(dpViewWidth, AD_HEIGHT));

                                    // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
                                    // loading.
                                    mAdView.setAdListener(new MyAdListener(mAdPanel));

                                    // Start loading the ad.
                                    mAdView.loadAd(getDefaultAdRequest(getContext()));

                                    //Add adView to layout
                                    mAdView.setLayoutParams(new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT));
                                    mAdPanel.addView(mAdView);
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private class MyAdListener extends AdListener {
        private final View mAdViewPanel;

        private MyAdListener(@NonNull View adViewPanel) {
            super();
            mAdViewPanel = adViewPanel;
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            mAdViewPanel.setVisibility(AdView.GONE);
            if (BuildConfig.DEBUG) Log.d(TAG, "Ads::AdFailedToLoad: " + i);
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            mAdViewPanel.setVisibility(AdView.VISIBLE);
            if (BuildConfig.DEBUG) Log.d(TAG, "Ads::AdLoaded");
        }

    }

}
