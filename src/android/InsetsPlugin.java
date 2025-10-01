package com.maazkhaleeq.cordova.insets;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.view.WindowManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.content.res.Resources;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

public class InsetsPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if ("getInsets".equals(action)) {
            getWindowInsets(callbackContext);
            return true;
        }
        return false;
    }

    private void getWindowInsets(final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    View decorView = cordova.getActivity().getWindow().getDecorView();
                    WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(decorView);

                    if (insets == null) {
                        // Fallback to display metrics if insets are not available
                        getFallbackInsets(callbackContext);
                        return;
                    }

                    JSONObject result = new JSONObject();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // For Android 11+ (API 30+), use the most precise method
                        Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                        Insets navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                        
                        result.put("top", pxToDp(statusBars.top));
                        result.put("bottom", pxToDp(navigationBars.bottom));
                        result.put("left", pxToDp(navigationBars.left));
                        result.put("right", pxToDp(navigationBars.right));
                    } else {
                        // For older versions, use systemBars but exclude IME
                        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                        result.put("top", pxToDp(systemBars.top));
                        result.put("bottom", pxToDp(systemBars.bottom));
                        result.put("left", pxToDp(systemBars.left));
                        result.put("right", pxToDp(systemBars.right));
                    }

                    callbackContext.success(result);

                } catch (Exception e) {
                    callbackContext.error("Error getting insets: " + e.getMessage());
                }
            }
        });
    }

    private void getFallbackInsets(CallbackContext callbackContext) {
        try {
            // Fallback method using system resources
            int statusBarHeight = getStatusBarHeight();
            int navigationBarHeight = getNavigationBarHeight();
            
            JSONObject result = new JSONObject();
            result.put("top", pxToDp(statusBarHeight));
            result.put("bottom", pxToDp(navigationBarHeight));
            result.put("left", 0);
            result.put("right", 0);
            
            callbackContext.success(result);
        } catch (Exception e) {
            callbackContext.error("Fallback also failed: " + e.getMessage());
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = cordova.getActivity().getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = cordova.getActivity().getResources().getDimensionPixelSize(resourceId);
        }
        return result > 0 ? result : dpToPx(24); // Fallback to 24dp if not found
    }

    private int getNavigationBarHeight() {
        int result = 0;
        int resourceId = cordova.getActivity().getResources()
                .getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = cordova.getActivity().getResources().getDimensionPixelSize(resourceId);
        }
        return result > 0 ? result : dpToPx(48); // Fallback to 48dp if not found
    }

    private int pxToDp(int px) {
        DisplayMetrics metrics = cordova.getActivity().getResources().getDisplayMetrics();
        return Math.round(px / metrics.density);
    }

    private int dpToPx(int dp) {
        DisplayMetrics metrics = cordova.getActivity().getResources().getDisplayMetrics();
        return Math.round(dp * metrics.density);
    }
}
