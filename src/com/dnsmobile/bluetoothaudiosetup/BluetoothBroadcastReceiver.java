package com.dnsmobile.bluetoothaudiosetup;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.PowerManager;
import android.util.Log;

public class BluetoothBroadcastReceiver extends BroadcastReceiver 
{
	private static final String TAG = "BluetoothBroadcastReceiver";
	private static final int AUDIO_STREAM_BLUETOOTH = 6;
	private static final int NOTIFICATION_ID = 1;
	private static PowerManager.WakeLock wakeLock = null;
	private Notification notification = null;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.i(TAG, TAG + ":onReceive()");
		
		SharedPreferences sharedPrefs = context.getSharedPreferences(MainActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		if (!sharedPrefs.getBoolean(MainActivity.SHARED_PREFS_KEY_ENABLED, false)) {
			// feature turned off, bail out
			return;
		}
		
		if (intent.getAction().equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED))
		{
			// ensure the wakelock object has been instantiated
			constructWakeLock(context);
			
			switch (intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)) {
				case BluetoothAdapter.STATE_CONNECTED:
					Log.i(TAG, "Bluetooth has connected!");
					bluetoothConnected(context);
					break;
				case BluetoothAdapter.STATE_DISCONNECTED:
					Log.i(TAG, "Bluetooth disconnected!");
					bluetoothDisconnected(context);
					break;
				default:
					break;
			}
		}
	}
	
	private void constructWakeLock(Context context) {
		if (wakeLock == null) {
			PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
		}
	}

	private void bluetoothConnected(Context context) {
	
		// set wakelock and notification
		wakeLock.acquire();
		
		// create the notification to keep the process alive
		createNotification(context);
		
		// set bluetooth audio to full volume
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager.getStreamMaxVolume(AUDIO_STREAM_BLUETOOTH);
		audioManager.setStreamVolume(AUDIO_STREAM_BLUETOOTH, maxVolume, AudioManager.FLAG_SHOW_UI);
	}
	
	private void bluetoothDisconnected(Context context) {
		
		// potentially reset audio level
		
		// remove notification
		cancelNotification(context);
		
		// release wakelock
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
	}
	
	private void createNotification(Context context) {
		notification = new Notification.Builder(context)
        	.setContentTitle("Bluetooth Audio Settings")
        	.setContentText("Holding wake lock and adjusted audio")
        	.setSmallIcon(R.drawable.ic_launcher)
        	.setOngoing(true)
        	.build();
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(TAG, NOTIFICATION_ID, notification);
	}
	
	private void cancelNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(TAG, NOTIFICATION_ID);
	}
}
