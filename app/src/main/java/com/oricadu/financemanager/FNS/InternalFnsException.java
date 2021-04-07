package com.oricadu.financemanager.FNS;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class InternalFnsException extends Exception {
    public InternalFnsException() {
    }

    public InternalFnsException(String message) {
        super(message);
    }

    public InternalFnsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalFnsException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public InternalFnsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
