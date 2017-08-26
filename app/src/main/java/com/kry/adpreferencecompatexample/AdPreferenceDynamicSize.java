package com.kry.adpreferencecompatexample;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.lang.ref.WeakReference;

import static com.kry.adpreferencecompatexample.AdUtils.convertPixelsToDp;
import static com.kry.adpreferencecompatexample.AdUtils.getDefaultAdRequest;

public class AdPreferenceDynamicSize extends Preference {

    private static final String TAG = "AdsPreferenceDynSize";
    private static final int MIN_AD_WIDTH = 280;
    private static final int AD_HEIGHT = 132;

    private final AdViewKeeper mAdViewKeeper;

    public AdPreferenceDynamicSize(Context context) {
        this(context, null);
    }

    public AdPreferenceDynamicSize(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context,
                android.support.v7.preference.R.attr.preferenceStyle,
                android.R.attr.preferenceStyle));
    }

    public AdPreferenceDynamicSize(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //Create an adView keeper
        mAdViewKeeper =
                new AdViewKeeper(getContext(), getContext().getString(R.string.ADMOB_AD_UNIT_ID_1));
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        final ViewGroup holderItemView = (ViewGroup) holder.itemView;

        if (holderItemView != null) {
            ViewTreeObserver viewTreeObserver = holderItemView.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    holderItemView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    holderItemView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }

                                //Get available width without padding
                                int width = holderItemView.getWidth() -
                                        holderItemView.getPaddingLeft() -
                                        holderItemView.getPaddingRight();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    width = width - holderItemView.getPaddingStart() -
                                            holderItemView.getPaddingEnd();
                                }

                                int dpViewWidth = convertPixelsToDp(width, getContext());
                                int dpViewHeight =
                                        convertPixelsToDp(holderItemView.getHeight(), getContext());
                                if (BuildConfig.DEBUG) Log.v(TAG,
                                        "Available space for ads: " + dpViewWidth + "x" +
                                                dpViewHeight + " dp");

                                if (dpViewWidth >= MIN_AD_WIDTH) {
                                    // Get AdBanner with specified size
                                    AdView adView = mAdViewKeeper.getAdView(dpViewWidth, AD_HEIGHT);

                                    if (adView != null) {
                                        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
                                        // loading.
                                        adView.setAdListener(new MyAdListener(adView));

                                        // Start loading the ad.
                                        adView.loadAd(getDefaultAdRequest(getContext()));

                                        // Set LayoutParams
                                        LinearLayout.LayoutParams lp =
                                                new LinearLayout.LayoutParams(
                                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                        lp.gravity = Gravity.CENTER;

                                        // Set layout orientation
                                        ((LinearLayout) holderItemView).setOrientation(
                                                LinearLayout.VERTICAL);

                                        // Disable left and right padding
                                        holderItemView.setPadding(0, holderItemView.getPaddingTop(),
                                                0, holderItemView.getPaddingBottom());

                                        //Remove adView from existing layout
                                        ViewGroup parent = (ViewGroup) adView.getParent();
                                        if (parent != null) parent.removeView(adView);

                                        //Add adView to layout
                                        holderItemView.addView(adView, lp);
                                    }
                                }
                            }
                        });
            }
        }

        holder.itemView.setFocusable(false);
        holder.itemView.setFocusableInTouchMode(false);
    }

    private static final class AdViewKeeper {
        private final WeakReference<Context> mContextWeakReference;
        private final String ADMOB_UNIT_ID;
        private AdView mAdView;

        private AdViewKeeper(@NonNull Context context, String admob_unit_id) {
            mContextWeakReference = new WeakReference<>(context);
            ADMOB_UNIT_ID = admob_unit_id;
        }

        AdView getAdView(int width, int height) {
            Context context = mContextWeakReference.get();
            if (context == null) return null;

            AdSize adSize = new AdSize(width, height);

            if (mAdView == null || !mAdView.getAdSize().equals(adSize)) {
                mAdView = new AdView(context);
                mAdView.setAdUnitId(ADMOB_UNIT_ID);
                mAdView.setAdSize(adSize);
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "Ads::Create AdView with size: " + width + "x" + height);
            }

            return mAdView;
        }

    }

    private class MyAdListener extends AdListener {
        private final View mAdView;

        private MyAdListener(@NonNull View adView) {
            super();
            mAdView = adView;
        }

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

    }

}
