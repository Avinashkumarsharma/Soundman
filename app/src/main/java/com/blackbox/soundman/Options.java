package com.blackbox.soundman;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class Options extends Activity {
	public static final int REQ_CODE = 2;
	private TimePicker start_time, end_time;
	private Spinner repeats_after, modes;
	private EditText label;
	Schedule schedule;
	Profile profile;
	boolean needUpdate = false;
	private static Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		context = getApplicationContext();
		setup();
		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			long id = getIntent().getExtras().getLong("id", -1);
			if(id != -1) {
				needUpdate = true;
				init(id);
			}
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id) {
		case R.id.save:
			save();
			finish();
			break;
		case R.id.cancle:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	public  static Context getContext() {
		return Options.context;
	}
	private void setup(){
		profile = null;
		schedule = new Schedule();
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int now = hour*60 + min;
		schedule.setStart_time(now);
		schedule.setEnd_time(now);
		modes = (Spinner) findViewById(R.id.modes);
		repeats_after = (Spinner) findViewById(R.id.repeat_after);
		start_time = (TimePicker) findViewById(R.id.start_time);
		end_time = (TimePicker) findViewById(R.id.end_time);
		end_time.setIs24HourView(true);
		start_time.setIs24HourView(true);
		start_time.setCurrentHour(hour);
		start_time.setCurrentMinute(min);
		end_time.setCurrentHour(hour);
		end_time.setCurrentMinute(min);
		label = (EditText) findViewById(R.id.profile_name);
		Resources res = getResources();
		String values_modes[] = res.getStringArray(R.array.profile_type);
		String values_repeat[] = res.getStringArray(R.array.repeat_interval);
		ArrayAdapter<String> adapter_mode = new ArrayAdapter<String>(Options.this, 
				android.R.layout.simple_spinner_dropdown_item, values_modes);
		ArrayAdapter<String> adapter_repeat = new ArrayAdapter<String>(Options.this, 
				android.R.layout.simple_spinner_dropdown_item, values_repeat);
		modes.setAdapter(adapter_mode);
		repeats_after.setAdapter(adapter_repeat);

		modes.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View v,
					int pos, long lng) {
				String s = adapter.getSelectedItem().toString();
				schedule.setProfileName(s);
				if(s.equalsIgnoreCase(Schedule.TYPE_CUSTOM)) {
					Intent intent = new Intent(Options.this, Custom.class);
					if(profile != null) {
					
						intent.putExtra("music", profile.getMusic());
						intent.putExtra("ring", profile.getRinger());
						intent.putExtra("alarm", profile.getAlarm());
						intent.putExtra("system", profile.getSystem());
						intent.putExtra("touch", profile.getTouch());
						intent.putExtra("vibrate", profile.getVibrate());
						intent.putExtra("voice", profile.getVoice());
					}
					startActivityForResult(intent, REQ_CODE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
				Toast.makeText(Options.this, "NOthing", Toast.LENGTH_SHORT).show();
			}
			
		});
		
		repeats_after.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View v,
					int pos, long lng) {
				String s = adapter.getSelectedItem().toString();
				schedule.setRepeatEvery(s);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		start_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker time, int hour, int min) {
				
				schedule.setStart_time(hour*60 + min);
			}
		});
		
		end_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker time, int hour, int min) {
				schedule.setEnd_time(hour*60 + min);
			}
		});
		
	}
	
	private void init(long id){
		SchedulesDataSource data = new SchedulesDataSource(Options.this);
		data.open();
		schedule = data.getScheduleById(id);
		int hour = ((int)schedule.getStart_time())/60;
		int min = ((int)schedule.getStart_time())%60;
		start_time.setCurrentHour(hour);
		start_time.setCurrentMinute(min);
		hour = ((int)schedule.getEnd_time())/60;
		min = ((int)schedule.getEnd_time())%60;
		end_time.setCurrentHour(hour);
		end_time.setCurrentMinute(min);
		modes.setSelection(getPosition(modes, schedule.getProfileName()));
		repeats_after.setSelection(getPosition(repeats_after, schedule.getRepeatEvery()));
		label.setText(schedule.getLabel());
		if(schedule.getProfile() != null)
		profile = new Profile(schedule.getProfile());

	}
	private int getPosition(Spinner s, String val) {
		for(int i = 0; i < s.getCount(); i++) {
			if(s.getItemAtPosition(i).toString().equalsIgnoreCase(val)) {
				return i;
			}
		}
		return 0;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == REQ_CODE) {
			if(resultCode == Custom.PROFILE_DATA && data != null) {
				int m = data.getIntExtra("music", 0);
				int r = data.getIntExtra("ring", 0);
				int a = data.getIntExtra("alarm", 0);
				int s = data.getIntExtra("system", 0);
				int t = data.getIntExtra("touch", 0);
				int vo = data.getIntExtra("voice", 0);
				int vi= data.getIntExtra("vibrate", 1);
				profile = new Profile(m, r, a, t, s, vo, vi);
			}
			if(data == null) {
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void save() {
		SchedulesDataSource data = new SchedulesDataSource(Options.this);
		data.open();
		schedule.setLabel(label.getText().toString());
		if(needUpdate) {
			schedule.setProfile(profile);
			data.update(schedule);
			schedule.cancel();
			schedule.activate();
		}
		else {
			schedule.setActive(1);
			long interval = Schedule.repeat_interval(schedule.getStart_time(), schedule.getRepeatEvery());
			schedule.setRepeat(interval);
			if(schedule.getProfileName().equalsIgnoreCase(Schedule.TYPE_CUSTOM)) {
				schedule.setProfile(profile);
			}
			else {
				
				schedule.setProfile(null);
			}
			long id = data.addShedule(schedule);
			schedule.setId(id);
			schedule.activate();
		}
		data.close();
	}
	
}
