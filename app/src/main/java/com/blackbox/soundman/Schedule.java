package com.blackbox.soundman;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Schedule{
	private  int active;
	private long start_time,end_time, repeat, id;
	private String profilename, repeats_every, label;
	private Profile profile = null;
	public static Context _context;
	public static final String allcolumsSchedule[] = {
		Db.S_ID, Db.START_TIME, Db.END_TIME, Db.REPEAT_AFTER, Db.ACTIVE, Db.P_NAME, Db.LABEL, Db.REPEAT_EVERY};
	public static final String TYPE_CUSTOM = "Custom";
	public static final String TYPE_NORMAL = "Normal";
	public static final String TYPE_VIBRATE = "Vibrate";
	public static final String TYPE_SILENT = "Silent";
	public static final String REPEAT[] = {"No Repeat", "Every Day", "Every Week"};
	public static final String DAYS[] = {"","Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	public static final String allcolumsProfile[] = {Db.P_ID, Db.VIBRATE, Db.MUSIC_STREAM, Db.RING_STREAM, Db.ALARM_STREAM, 
			Db.DTMF_STREAM, Db.SYSTEM_STREAM, Db.VOICE_CALL_STREAM };
	public static final int MAX_SCHEDULES = 100;
	public Schedule(long start_time, long end_time, int active, long repeat,
			 String profilename, Profile profile) {
		this.active = active;
		this.start_time = start_time;
		this.end_time = end_time;
		this.repeat = repeat;
		this.profilename = profilename;
		this.profile = profile;
		this.label = "";
	}
	
	public Schedule(long id, long start_time, long end_time, int active, long repeat,
			 String profilename, Profile profile) {
		this.active = active;
		this.start_time = start_time;
		this.end_time = end_time;
		this.repeat = repeat;
		this.profilename = profilename;
		this.profile = profile;
		this.id = id;
		this.label = "";
	}
	
	public Schedule() {
		profile = null;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public String getRepeatEvery() {
		return repeats_every;
	}
	public void setRepeatEvery(String repeats_every) {
		this.repeats_every = repeats_every;
	}
	public long getRepeat() {
		return repeat;
	}

	public void setRepeat(long repeat) {
		this.repeat = repeat;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public long getStart_time() {
		return start_time;
	}

	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}

	public long getEnd_time() {
		return end_time;
	}

	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}

	public String getProfileName() {
		return profilename;
	}

	public void setProfileName(String profilename) {
		this.profilename = profilename;
	}
	public Profile getProfile() {
		return this.profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	public static long repeat_interval(long start_time, String interval){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(start_time);
		if(interval.equalsIgnoreCase(REPEAT[0])) { //NO REPEAT;
			return 0;
		}
		else if(interval.equalsIgnoreCase(REPEAT[1])) { //every day;
			return AlarmManager.INTERVAL_DAY;
		}
		else  //every week
			return AlarmManager.INTERVAL_DAY*7l;
	}
	
	public void activate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		long start, end;
		long interval;
		start = 0;end = 0; interval = 0;
		int present_hour = cal.get(Calendar.HOUR_OF_DAY);
		int present_min = cal.get(Calendar.MINUTE);
		int start_hour = (int) (start_time/60);
		int start_min = (int) (start_time%60);
		int end_hour = (int) (end_time/60);
		int end_min = (int) (end_time%60);
		if(present_hour*60 + present_min > start_time) {
			cal.add(Calendar.DATE, 1);
		}
		//Log.d("Today:", java.text.DateFormat.getDateTimeInstance().format(cal.getTime()));
		cal.set(Calendar.HOUR_OF_DAY, start_hour);
		cal.set(Calendar.MINUTE, start_min);
		//cal.set(Calendar.SECOND, 0);
		int index = search(REPEAT);
		if(index != -1) {
			start = cal.getTimeInMillis();
			if(present_hour*60 + present_min > end_time) {
				cal.add(Calendar.DATE, 1);
			}
			cal.set(Calendar.HOUR_OF_DAY, end_hour);
			cal.set(Calendar.MINUTE, end_min);
			cal.set(Calendar.SECOND, 0);
			end = cal.getTimeInMillis();
			
			interval = repeat_interval(start, profilename)*1000*60;
			//startScheduler for end and start;
		}
		else {
			index = search(DAYS);
			int offset;
			if(index != -1) {
				int today = cal.get(Calendar.DAY_OF_WEEK);
				if(today <= index) {
					offset = index - today;
				}
				else {
					offset = (7 - today) + index;
				}
				cal.set(Calendar.HOUR_OF_DAY, start_hour);
				cal.set(Calendar.MINUTE, start_min);
				cal.set(Calendar.SECOND, 0);
				start = cal.getTimeInMillis() + offset*24*60*60*1000;
				if(present_hour*60 + present_min > end_time) {
					cal.add(Calendar.DATE, 1);
				}
				cal.set(Calendar.HOUR, end_hour);
				cal.set(Calendar.MINUTE, end_min);
				cal.set(Calendar.SECOND, 0);
				
				end = cal.getTimeInMillis() + offset*24*60*60*1000;
				interval = repeat_interval(start, profilename);
			}else {
				return;
			}
		}
		
		Intent intent_start = new Intent(Main.context, ScheduleReciever.class);
		Intent intent_end = new Intent(Main.context, ScheduleReciever.class);
		intent_start.setAction("ACTION_START");
		intent_start.putExtra("mode", profilename);
		intent_start.putExtra("action", ScheduleReciever.START);
		intent_start.putExtra("id", id);
		intent_end.putExtra("mode", "ring");
		intent_end.putExtra("id", id);
		intent_end.putExtra("action", ScheduleReciever.END);
		intent_end.setAction("ACTION_END");
		
		PendingIntent pintent_start = PendingIntent.getBroadcast(Main.context, 
				(int)id, intent_start, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pintent_end = PendingIntent.getBroadcast(Main.context, 
				(int)id+MAX_SCHEDULES, intent_end, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager alarmMan = (AlarmManager) Main.context.getSystemService(Context.ALARM_SERVICE);
		
		if(interval == 0) {
			alarmMan.set(AlarmManager.RTC, start, pintent_start);
			alarmMan.set(AlarmManager.RTC, end, pintent_end);
		}
		else {
			alarmMan.setRepeating(AlarmManager.RTC, start, repeat, pintent_start);
			alarmMan.setRepeating(AlarmManager.RTC, end, repeat, pintent_end);	
		}
		
	}
	
	public void cancel() {
		
		Intent intent_start = new Intent(Main.context, ScheduleReciever.class);
		Intent intent_end = new Intent(Main.context, ScheduleReciever.class);
		intent_start.setAction("ACTION_START");
		intent_start.putExtra("mode", profilename);
		intent_start.putExtra("action", ScheduleReciever.START);
		intent_start.putExtra("id", id);
		intent_end.putExtra("mode", TYPE_NORMAL);
		intent_end.putExtra("id", id);
		intent_end.putExtra("action", ScheduleReciever.END);
		intent_end.setAction("ACTION_END");
		
		PendingIntent pintent_start = PendingIntent.getBroadcast(Main.context, 
				(int)id, intent_start, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pintent_end = PendingIntent.getBroadcast(Main.context, 
				(int)id+MAX_SCHEDULES, intent_end, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager alarmMan = (AlarmManager) Main.context.getSystemService(Context.ALARM_SERVICE);
		alarmMan.cancel(pintent_start);		
		alarmMan.cancel(pintent_end);
	}
	
	private int search(String values[]) {
		for(int i = 0; i <values.length; i++) {
			if(values[i].equalsIgnoreCase(repeats_every))
				return i;
		}
		return -1;
	}
	
}



 class Profile{
	private int music, ringer, alarm, touch, system, voice, vibrate;
	
	public Profile(int music, int ringer, int alarm, int touch,
			int system, int voice, int vibrate) {
		this.music = music;
		this.ringer = ringer;
		this.alarm = alarm;
		this.touch = touch;
		this.system = system;
		this.voice = voice;
		this.vibrate = vibrate;
	}
	public Profile() {
		
	}
public Profile(Profile profile) {
	this.music = profile.getMusic();
	this.ringer = profile.getRinger();
	this.alarm = profile.getAlarm();
	this.touch = profile.getTouch();
	this.system = profile.getSystem();
	this.voice = profile.getVoice();
	this.vibrate = profile.getVibrate();
}
	public int getVibrate() {
		return vibrate;
	}
	public void setVibrate(int vibrate) {
		this.vibrate = vibrate;
	}
	public int getMusic() {
		return music;
	}

	public void setMusic(int music) {
		this.music = music;
	}

	public int getRinger() {
		return ringer;
	}

	public void setRinger(int ringer) {
		this.ringer = ringer;
	}

	public int getAlarm() {
		return alarm;
	}

	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}

	public int getTouch() {
		return touch;
	}

	public void setTouch(int touch) {
		this.touch = touch;
	}

	public int getSystem() {
		return system;
	}

	public void setSystem(int system) {
		this.system = system;
	}

	public int getVoice() {
		return voice;
	}

	public void setVoice(int voice) {
		this.voice = voice;
	}  
}
