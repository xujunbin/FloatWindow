package com.ace.floatwindow.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by JunBin on 2015/8/31.
 */
public class ToastUtils {
    public static void showShortToast(final Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showShortToast(final Context context, final int resId) {
        showShortToast(context, context.getString(resId));
    }
}
