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
	
	private void bluetoothConnected(Context context) {
		
		// create the service to keep the process alive
		context.startService(new Intent(context, WakeLockService.class));
		
		// set bluetooth audio to full volume
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager.getStreamMaxVolume(AUDIO_STREAM_BLUETOOTH);
		audioManager.setStreamVolume(AUDIO_STREAM_BLUETOOTH, maxVolume, AudioManager.FLAG_SHOW_UI);
	}
	
	private void bluetoothDisconnected(Context context) {
		
		// potentially reset audio level
		
		// stop the service which will remove the notification and release the wake lock
		context.stopService(new Intent(context, WakeLockService.class));
	}
}
