package com.dnsmobile.bluetoothaudiosetup;

import android.os.Bundle;
import android.app.Activity;
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
			}
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
