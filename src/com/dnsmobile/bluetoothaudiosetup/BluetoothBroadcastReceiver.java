package com.dnsmobile.bluetoothaudiosetup;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothBroadcastReceiver extends BroadcastReceiver 
{
	private static final String TAG = "BluetoothBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if (intent.getAction().equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED))
		{
			switch (intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)) {
				case BluetoothAdapter.STATE_CONNECTED:
					Log.i(TAG, "Bluetooth has connected!");
					break;
				case BluetoothAdapter.STATE_DISCONNECTED:
					Log.i(TAG, "Bluetooth disconnected!");
					break;
				default:
					break;
			}
		}
	}

}
