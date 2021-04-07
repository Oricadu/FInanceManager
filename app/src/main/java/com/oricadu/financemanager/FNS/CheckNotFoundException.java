package com.oricadu.financemanager.FNS;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class CheckNotFoundException extends Exception {
    public CheckNotFoundException() {
    }

    public CheckNotFoundException(String message) {
        super(message);
    }

    public CheckNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckNotFoundException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CheckNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
