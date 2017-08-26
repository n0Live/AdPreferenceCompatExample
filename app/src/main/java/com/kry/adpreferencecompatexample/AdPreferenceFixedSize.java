package com.kry.adpreferencecompatexample;

import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import static com.kry.adpreferencecompatexample.AdUtils.getDefaultAdRequest;

public class AdPreferenceFixedSize extends Preference {

    private static final String TAG = "AdsPreferenceFixSize";

    private AdView mAdView;
    private AdListener mAdListener;

    public AdPreferenceFixedSize(Context context) {
        this(context, null);
    }

    public AdPreferenceFixedSize(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context,
                android.support.v7.preference.R.attr.preferenceStyle,
                android.R.attr.preferenceStyle));
    }

    public AdPreferenceFixedSize(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mAdListener = new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                mAdView.setVisibility(AdView.GONE);
                if (BuildConfig.DEBUG) Log.d(TAG, "Ads::AdFailedToLoad: " + i);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(AdView.VISIBLE);
                if (BuildConfig.DEBUG) Log.d(TAG, "Ads::AdLoaded");
            }
        };
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        mAdView = (AdView) holder.itemView.findViewById(R.id.adViewPref);

        //Don't show adView before loading complete
        mAdView.setVisibility(AdView.GONE);

        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
        // loading.
        mAdView.setAdListener(mAdListener);

        // Start loading the ad.
        mAdView.loadAd(getDefaultAdRequest(getContext()));

        holder.itemView.setFocusable(false);
        holder.itemView.setFocusableInTouchMode(false);
    }

}
