package com.panic.button.core.base.notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.panic.button.core.api.ApiExtensionKt;
import com.panic.button.core.api.UploadRequest;
import com.panic.button.core.base.BaseApplication;
import com.panic.button.core.model.Urls;

import java.io.File;

public class OnClearFromRecentService extends Service {

    private int currentTime = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentTime = intent.getIntExtra(NotificationService.EXTRA_TIME_INTERVAL, 0);
        ApiExtensionKt.log("ClearFromRecentService "+"Service Started "+intent.getIntExtra(NotificationService.EXTRA_TIME_INTERVAL, 0));
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        ApiExtensionKt.log("ClearFromRecentService Service Destroyed");
        uploadVoice();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        ApiExtensionKt.log("ClearFromRecentService "+"END "+currentTime);
        runBroadcast();
        stopSelf();
    }

    private void runBroadcast() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NotificationService.ACTION_RESTART_SERVICE);
        broadcastIntent.setClass(this, NotificationReceiver.class);
        broadcastIntent.putExtra(NotificationService.EXTRA_TIME_INTERVAL, currentTime);
        this.sendBroadcast(broadcastIntent);
    }

    private void uploadVoice() {
        if (BaseApplication.Companion.getRecorderVoice() != null) {
            BaseApplication.Companion.getRecorderVoice().stopRecord();

            File file = BaseApplication.Companion.getRecorderVoice().getFile();
            String roomID = "";

            if (BaseApplication.Companion.getSessionManager() != null) {
                roomID = BaseApplication.Companion.getSessionManager().getRoomsId();
            }

            new UploadRequest(Urls.Companion.getUPLOAD_PANIC_MANUAL()).uploadFilePanicButton(file, roomID, "audio-manual", null, null, null);
        }
    }
}
