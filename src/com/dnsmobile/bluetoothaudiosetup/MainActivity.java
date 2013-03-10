package com.dnsmobile.bluetoothaudiosetup;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class MainActivity extends Activity 
{
	public static final String SHARED_PREFS_NAME = "com.dnsmobile.bluetoothaudiosetup.SHARED_PREFS";
	public static final String SHARED_PREFS_KEY_ENABLED = "com.dnsmobile.bluetoothaudiosetup.KEY_ENABLED";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeEnabledSwitch();
	}
	
	private void initializeEnabledSwitch() {
		
		Switch manageBTSwitch = (Switch) findViewById(R.id.manage_bluetooth_switch);
		final SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		
		// set the initial state of the switch view
		manageBTSwitch.setChecked(sharedPrefs.getBoolean(SHARED_PREFS_KEY_ENABLED, false));
		
		manageBTSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.putBoolean(SHARED_PREFS_KEY_ENABLED, isChecked);
				editor.commit();
				
				// start or stop the service when the user flips the switch
				Intent intent = new Intent(MainActivity.this, WakeLockService.class);
				if (isChecked && isBluetoothConnected()) { 
					startService(intent); 
				}
				else if (!isChecked) {
					stopService(intent); 
				}
			}
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

    public boolean isBluetoothConnected()
    {
        boolean retval = true;
        try {
            retval = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(
            		android.bluetooth.BluetoothProfile.HEADSET) != android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
        } 
        catch (Exception exception) {
            // nothing to do
        }
        return retval;
    }
}
