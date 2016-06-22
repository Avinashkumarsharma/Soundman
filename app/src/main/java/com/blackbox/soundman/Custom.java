package com.blackbox.soundman;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

public class Custom extends Activity {
	public static final int PROFILE_DATA = 2;
	SeekbarWithLabel music, ring, alarm, touch, voice, system;
	CheckBox vibrate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom);
		setup();
		AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adReq = new AdRequest.Builder().addTestDevice("918C38E312074B9E424960F332A8C928").build();
        adView.loadAd(adReq);
	}
	
	private void setup() {
		ring = (SeekbarWithLabel) findViewById(R.id.ring);
		music = (SeekbarWithLabel) findViewById(R.id.music);
		alarm = (SeekbarWithLabel) findViewById(R.id.alarm);
		touch = (SeekbarWithLabel) findViewById(R.id.touch);
		voice = (SeekbarWithLabel) findViewById(R.id.voice);
		system = (SeekbarWithLabel) findViewById(R.id.system);
		vibrate = (CheckBox) findViewById(R.id.vibrate_state);
		
		Bundle data = getIntent().getExtras();
		if(data != null) {
			ring.setProgress(data.getInt("ring",0));
			music.setProgress(data.getInt("music",0));
			alarm.setProgress(data.getInt("alarm",0));
			touch.setProgress(data.getInt("touch",0));
			system.setProgress(data.getInt("system",0));
			voice.setProgress(data.getInt("voice",0));
			boolean checked = (data.getInt("vibrate", 0) > 0) ? true : false;
			vibrate.setChecked(checked);
		}
		
	}
	private void prepareCustomProfile() {
		int m = music.getProgress();
		int r = ring.getProgress();
		int a = alarm.getProgress();
		int t = touch.getProgress();
		int s = system.getProgress();
		int vo = voice.getProgress();
		int v = vibrate.isChecked() ? 1:0;
		Intent intent = new Intent();
		intent.putExtra("music", m);
		intent.putExtra("ring", r);
		intent.putExtra("alarm", a);
		intent.putExtra("system", s);
		intent.putExtra("touch", t);
		intent.putExtra("vibrate", v);
		intent.putExtra("voice", vo);
		setResult(PROFILE_DATA, intent);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.custom, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onBackPressed() {
		prepareCustomProfile();
	}
	
}
