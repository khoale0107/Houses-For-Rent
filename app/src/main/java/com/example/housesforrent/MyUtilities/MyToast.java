package com.example.housesforrent.MyUtilities;

import android.content.Context;
import android.widget.Toast;

public class MyToast {
    static Toast toastMessage;

    public static void toast(Context context, CharSequence msg) {
        if (toastMessage!= null) {
            toastMessage.cancel();
        }
        toastMessage= Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toastMessage.show();
    }
}
