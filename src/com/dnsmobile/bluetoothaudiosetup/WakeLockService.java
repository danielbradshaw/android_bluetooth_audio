package com.dnsmobile.bluetoothaudiosetup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

public class WakeLockService extends Service 
{
	private static final String TAG = "WakeLockService";
	private static final int NOTIFICATION_ID = 1;
	
	private static PowerManager.WakeLock wakeLock = null;
	private Notification notification = null;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		constructWakeLock();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		wakeLock.acquire();
		createNotification();
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
		
		cancelNotification();
		super.onDestroy();
	}

	private void constructWakeLock() {
		if (wakeLock == null) {
			PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
		}
	}
	
	private void createNotification() {
		notification = new Notification.Builder(this)
        	.setContentTitle("Bluetooth Audio Settings")
        	.setContentText("Holding wake lock and adjusted audio")
        	.setSmallIcon(R.drawable.ic_launcher)
        	.setOngoing(true)
        	.build();
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(TAG, NOTIFICATION_ID, notification);
	}
	
	private void cancelNotification() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(TAG, NOTIFICATION_ID);
	}
}
