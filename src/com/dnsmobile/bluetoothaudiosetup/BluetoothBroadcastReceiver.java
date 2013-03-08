package com.dnsmobile.bluetoothaudiosetup;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

public class BluetoothBroadcastReceiver extends BroadcastReceiver 
{
	private static final String TAG = "BluetoothBroadcastReceiver";
	public static final String SHARED_PREFS_ORIGINAL_VOL = "com.dnsmobile.bluetoothaudiosetup.ORIGINAL_VOL";
	private static final int AUDIO_STREAM_BLUETOOTH = 6;
	
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
			switch (intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)) {
				case BluetoothAdapter.STATE_CONNECTED:
					Log.i(TAG, "Bluetooth has connected!");
					bluetoothConnected(context, sharedPrefs);
					break;
				case BluetoothAdapter.STATE_DISCONNECTED:
					Log.i(TAG, "Bluetooth disconnected!");
					bluetoothDisconnected(context, sharedPrefs);
					break;
				default:
					break;
			}
		}
	}
	
	private void bluetoothConnected(Context context, SharedPreferences sharedPrefs) {
		
		// create the service to keep the process alive
		context.startService(new Intent(context, WakeLockService.class));
		
		// set bluetooth audio to full volume, saving the original
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int originalVolume = audioManager.getStreamVolume(AUDIO_STREAM_BLUETOOTH);
		sharedPrefs.edit().putInt(SHARED_PREFS_ORIGINAL_VOL, originalVolume).commit();
		int maxVolume = audioManager.getStreamMaxVolume(AUDIO_STREAM_BLUETOOTH);
		audioManager.setStreamVolume(AUDIO_STREAM_BLUETOOTH, maxVolume, AudioManager.FLAG_SHOW_UI);
	}
	
	private void bluetoothDisconnected(Context context, SharedPreferences sharedPrefs) {
		
		// reset audio level to the original volume
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int originalVolume = sharedPrefs.getInt(SHARED_PREFS_ORIGINAL_VOL, 0);
		audioManager.setStreamVolume(AUDIO_STREAM_BLUETOOTH, originalVolume, AudioManager.FLAG_SHOW_UI);
		
		// stop the service which will remove the notification and release the wake lock
		context.stopService(new Intent(context, WakeLockService.class));
	}
}
