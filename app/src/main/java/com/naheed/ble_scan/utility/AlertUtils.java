package com.naheed.ble_scan.utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Naheed Shamim on 02-01-2018.
 */

public class AlertUtils {

    private static Toast mToast;

    private static void showToast(Context context, String msg, final int toastLength)
    {
        if(mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(context,msg,toastLength);
        mToast.show();

    }
    public static void  showShortToast(Context context, String msg)
    {
        showToast(context,msg,Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, String msg)
    {
        showToast(context,msg,Toast.LENGTH_LONG);
    }
}
