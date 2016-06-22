package com.blackbox.soundman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

public class ScheduleReciever extends BroadcastReceiver {
	public static final int START = 0;
	public static final int END = 1;
	public static final int CANCLE = 3;
	private static AudioManager audioMan;
	@Override
	public void onReceive(Context context, Intent intent) {
		audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int action;
		long id;
		action = intent.getIntExtra("action", -1);
		id = intent.getLongExtra("id", -1);
		String mode = intent.getStringExtra("mode");
		set(context, action, id, mode);
		
	}
	private void set(Context context, int action, long id, String mode) {
		switch(action) {
		case START:
			if(mode.equalsIgnoreCase(Schedule.TYPE_NORMAL))
				normal();
			else if(mode.equalsIgnoreCase(Schedule.TYPE_SILENT))
				silent();
			else if(mode.equalsIgnoreCase(Schedule.TYPE_CUSTOM))
				custom(context, id);
			else if(mode.equalsIgnoreCase(Schedule.TYPE_VIBRATE))
				vibrate();
			else {
				Log.e("NOTHING:", "Kuch nai hua");
			}
			break;
		case END:
			normal();
			break;
		default:
			//Toast.makeText(context, "BOOT",Toast.LENGTH_LONG ).show();
			break;
		}	
	}
	
	private void vibrate() {
		audioMan.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		
	}
	private void normal() {
		
		audioMan.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}
	private void silent() {
		
		audioMan.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	}
	private void custom(Context context, long id) {
		
		SchedulesDataSource data = new SchedulesDataSource(context);
		data.open();
		Schedule schedule = data.getScheduleById(id);
		data.close();
		Profile profile = schedule.getProfile();
		float ringer, music, alarm, touch, system, voice;
		int rMax, mMax, aMax, tMax, sMax, vMax;
		ringer = profile.getRinger();
		music = profile.getMusic();
		alarm = profile.getAlarm();
		touch = profile.getTouch();
		system = profile.getSystem();
		voice = profile.getVoice();
		
		rMax = audioMan.getStreamMaxVolume(AudioManager.STREAM_RING);
		mMax = audioMan.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		aMax = audioMan.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		tMax = audioMan.getStreamMaxVolume(AudioManager.STREAM_DTMF);;
		sMax = audioMan.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		vMax = audioMan.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);	
		Log.e("Ringer", (int)((ringer/100)*rMax)+"");
		Log.e("Music", (int)((music/100)*mMax)+"");
		audioMan.setStreamVolume(AudioManager.STREAM_VOICE_CALL, (int)((voice/100)*vMax), AudioManager.FLAG_SHOW_UI);
		audioMan.setStreamVolume(AudioManager.STREAM_DTMF, (int)((touch/100)*tMax), AudioManager.FLAG_SHOW_UI);
		audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, (int)((music/100)*mMax), AudioManager.FLAG_SHOW_UI);
		audioMan.setStreamVolume(AudioManager.STREAM_ALARM, (int)((alarm/100)*aMax), AudioManager.FLAG_SHOW_UI);
		audioMan.setStreamVolume(AudioManager.STREAM_NOTIFICATION, (int)((ringer/100)*rMax), AudioManager.FLAG_SHOW_UI);
		audioMan.setStreamVolume(AudioManager.STREAM_RING, (int)((ringer/100)*rMax), AudioManager.FLAG_SHOW_UI);
		//audioMan.setStreamVolume(AudioManager.STREAM_SYSTEM, (int)((system/100)*sMax), AudioManager.FLAG_SHOW_UI);
		
		
	}
	
}