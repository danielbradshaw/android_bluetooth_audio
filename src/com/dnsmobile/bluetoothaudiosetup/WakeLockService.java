package com.dnsmobile.bluetoothaudiosetup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.PowerManager;

public class WakeLockService extends Service 
{
	private static final String TAG = "WakeLockService";
	
	private static final String SHARED_PREFS_ORIGINAL_VOL = "com.dnsmobile.bluetoothaudiosetup.ORIGINAL_VOL";
	private static final int AUDIO_STREAM_BLUETOOTH = 6;
	private static final int NOTIFICATION_ID = 1;
	private static final int PENDING_INTENT_REQ_CODE = 1;
	
	private static PowerManager.WakeLock wakeLock = null;
	private Notification notification = null;
		
	@Override
	public void onCreate() {
		super.onCreate();
		constructWakeLock();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!wakeLock.isHeld()) {
			wakeLock.acquire();
			setBluetoothAudio();
			createNotification();
		}
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
			unsetBluetoothAudio();
			cancelNotification();
		}
		super.onDestroy();
	}

	private void constructWakeLock() {
		if (wakeLock == null) {
			PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
		}
	}
	
	private void createNotification() {
		
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this,
		        PENDING_INTENT_REQ_CODE, notificationIntent,
		        PendingIntent.FLAG_CANCEL_CURRENT);
		
		notification = new Notification.Builder(this)
			.setContentIntent(contentIntent)
        	.setContentTitle(getResources().getString(R.string.notif_title))
        	.setContentText(getResources().getString(R.string.notif_text))
        	.setSmallIcon(R.drawable.bluetooth_black)
        	.setOngoing(true)
        	.build();
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(TAG, NOTIFICATION_ID, notification);
	}
	
	private void cancelNotification() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(TAG, NOTIFICATION_ID);
	}
	
	private void setBluetoothAudio() {
		// set bluetooth audio to full volume, saving the original
		SharedPreferences sharedPrefs = getSharedPreferences(MainActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int originalVolume = audioManager.getStreamVolume(AUDIO_STREAM_BLUETOOTH);
		sharedPrefs.edit().putInt(SHARED_PREFS_ORIGINAL_VOL, originalVolume).commit();
		int maxVolume = audioManager.getStreamMaxVolume(AUDIO_STREAM_BLUETOOTH);
		audioManager.setStreamVolume(AUDIO_STREAM_BLUETOOTH, maxVolume, AudioManager.FLAG_SHOW_UI);
	}
	
	private void unsetBluetoothAudio() {
		// reset audio level to the original volume
		SharedPreferences sharedPrefs = getSharedPreferences(MainActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int originalVolume = sharedPrefs.getInt(SHARED_PREFS_ORIGINAL_VOL, 0);
		audioManager.setStreamVolume(AUDIO_STREAM_BLUETOOTH, originalVolume, AudioManager.FLAG_SHOW_UI);
	}
}
