package com.zzfordev.medialink;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

public class Global
{
    public static Application application;

    public static void postToMainThread(Runnable runnable)
    {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(runnable);
    }
}
