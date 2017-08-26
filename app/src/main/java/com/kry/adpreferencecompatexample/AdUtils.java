package com.kry.adpreferencecompatexample;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.DisplayMetrics;

import com.google.android.gms.ads.AdRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum AdUtils {
    ;

    public static int convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = Math.round(px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static AdRequest getDefaultAdRequest(Context context) {
        // Code to force all devices to show test ads
        String ANDROID_ID =
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(md5(ANDROID_ID).toUpperCase())
                .build();

        return adRequest;
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            String h;
            for (byte aMessageDigest : messageDigest) {
                h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
