package com.blackbox.soundman;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 

class Db extends SQLiteOpenHelper{
	public static final String DB_NAME = "soundman";
	public static final String SCHEDULES = "schedule";
	public static final int DB_VERSION = 3;
	public static final String PROFILES = "profile";
	public static final String S_ID = "id";
	public static final String START_TIME = "start_time";
	public static final String END_TIME = "end_time";
	public static final String P_ID = "p_id";
	public static final String ACTIVE = "active";
	public static final String P_NAME = "profile_name";
	public static final String LABEL = "label";
	public static final String REPEAT_AFTER = "repeat_after";
	public static final String REPEAT_EVERY = "repeat_every";
	public static final String VIBRATE = "vibrate";
	public static final String MUSIC_STREAM = "music";
	public static final String RING_STREAM = "ring";
	public static final String ALARM_STREAM = "alarm";
	public static final String DTMF_STREAM = "touch";
	public static final String SYSTEM_STREAM = "system";
	public static final String VOICE_CALL_STREAM = "voice_call";
	public static final String CREATE_SCHEDULE = "create table if not exists "
			+ SCHEDULES+"("+S_ID + " integer not null unique primary key autoincrement, "
			+ START_TIME+" integer not null, "
			+ END_TIME+ " integer not null, "
			+ REPEAT_AFTER + " integer default 0, "
			+ ACTIVE +" integer not null, "
			+ P_NAME+ " varchar(60) not null , "
			+ REPEAT_EVERY+ " varchar(60) not null , "
			+ LABEL+ " varchar(60) ); ";
	public static final String CREATE_PROFILES = "create table if not exists "
			+ PROFILES+" ("+P_ID +" integer not null primary key autoincrement, "
			+ VIBRATE + " integer not null default 1, "
			+ MUSIC_STREAM + " integer, "
			+ RING_STREAM + " integer, "
			+ ALARM_STREAM + " integer, "
			+ DTMF_STREAM + " integer, "
			+ SYSTEM_STREAM + " integer, "
			+ VOICE_CALL_STREAM +" integer, "
			+ S_ID  +" int,  "
			+ "foreign key( " +S_ID+ " ) references "+SCHEDULES+"("+S_ID+") on delete cascade);";
	public static final String DROP_SCHEDULE = "DROP TABLE IF EXISTS " + SCHEDULES;
	public static final String DROP_PROFILES = "DROP TABLE IF EXISTS " + PROFILES;
	
	public Db(Context context) {
		super(context,DB_NAME, null, DB_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SCHEDULE);
		db.execSQL(CREATE_PROFILES);
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		db.execSQL(DROP_PROFILES);
		db.execSQL(DROP_SCHEDULE);
		onCreate(db);
	}
	
}

//The main DMO for Schedule
public class SchedulesDataSource {
	Db dbHelper;
	SQLiteDatabase database;
	
	public SchedulesDataSource(Context context) {
		dbHelper = new Db(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public long addShedule(Schedule schedule) {
		
		ContentValues values = new ContentValues();
		values.put(Db.START_TIME, schedule.getStart_time());
		values.put(Db.END_TIME, schedule.getEnd_time());
		values.put(Db.P_NAME, schedule.getProfileName());
		values.put(Db.ACTIVE, schedule.getActive());
		values.put(Db.REPEAT_AFTER, schedule.getRepeat());
		values.put(Db.LABEL, schedule.getLabel());
		values.put(Db.REPEAT_EVERY, schedule.getRepeatEvery());
		
		if(schedule.getProfile() != null) {
			ContentValues pvalues = new ContentValues();
			Profile profile = schedule.getProfile();
			pvalues.put(Db.VIBRATE, profile.getVibrate());
			pvalues.put(Db.MUSIC_STREAM, profile.getMusic());
			pvalues.put(Db.RING_STREAM, profile.getRinger());
			pvalues.put(Db.DTMF_STREAM, profile.getTouch());
			pvalues.put(Db.ALARM_STREAM, profile.getAlarm());
			pvalues.put(Db.SYSTEM_STREAM, profile.getSystem());
			pvalues.put(Db.VOICE_CALL_STREAM, profile.getVoice());
			long id =  database.insert(Db.SCHEDULES, null, values);
			pvalues.put(Db.S_ID, id);
			//Inserting now to Table Profiles.
			database.insert(Db.PROFILES, null, pvalues);
			return id;
		}
		return database.insert(Db.SCHEDULES, null, values);
		//Inserting to Table Schedule.
	}
	public void update(Schedule schedule) {
		long id = schedule.getId();
		//Log.e("ID:", id+"");
		//String sql = "Select schedule.id FROM schedule left outer join profile on schedule.id = profile.id Where schedule.id = "+id;
		//Cursor cur = database.rawQuery(sql,null);
		//cur.moveToFirst();
		ContentValues values = new ContentValues();
		values.put(Db.START_TIME, schedule.getStart_time());
		values.put(Db.END_TIME, schedule.getEnd_time());
		values.put(Db.P_NAME, schedule.getProfileName());
		values.put(Db.ACTIVE, schedule.getActive());
		values.put(Db.REPEAT_AFTER, schedule.getRepeat());
		values.put(Db.LABEL, schedule.getLabel());
		values.put(Db.REPEAT_EVERY, schedule.getRepeatEvery());
		if(schedule.getProfile() != null) {
			ContentValues pvalues = new ContentValues();
			Profile profile = schedule.getProfile();
			pvalues.put(Db.VIBRATE, profile.getVibrate());
			pvalues.put(Db.MUSIC_STREAM, profile.getMusic());
			pvalues.put(Db.RING_STREAM, profile.getRinger());
			pvalues.put(Db.DTMF_STREAM, profile.getTouch());
			pvalues.put(Db.ALARM_STREAM, profile.getAlarm());
			pvalues.put(Db.SYSTEM_STREAM, profile.getSystem());
			pvalues.put(Db.VOICE_CALL_STREAM, profile.getVoice());
			//Log.e("Updating: ", "Profile");
			database.update(Db.PROFILES, pvalues,"id = " +id, null);
		}
		database.update(Db.SCHEDULES, values, "id = " +id, null);
		//Log.e("Updating: ", "schedule");
	}
	public void delete(Schedule schedule) {
		long id = schedule.getId();
		database.delete(Db.SCHEDULES, "id = " + id, null);
	}
	private long getLong(Cursor cur, String colname) {
		return cur.getLong(cur.getColumnIndex(colname));
	}
	
	
	private int getInt(Cursor cur, String colname) {
		return cur.getInt(cur.getColumnIndex(colname));
	}
	
	
	private String getString(Cursor cur, String colname) {
		return cur.getString(cur.getColumnIndex(colname));
	}
	
	
	public ArrayList<Schedule> getAllSchedules(){
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		String SQL = "SELECT "+Db.SCHEDULES+"." 
		+ Db.S_ID + ", " 
		+ Db.START_TIME+ ", " 
		+ Db.END_TIME + ", "  
		+ Db.REPEAT_AFTER + ", " 
		+ Db.REPEAT_EVERY + ", "
		+ Db.ACTIVE + ", " 
		+ Db.P_NAME + ", " 
		+ Db.LABEL + ", "
		+ Db.MUSIC_STREAM+ ", " 
		+ Db.RING_STREAM + ", " 
		+ Db.ALARM_STREAM + ", " 
		+ Db.DTMF_STREAM+ ", " 
		+ Db.VIBRATE + ", " 
		+ Db.SYSTEM_STREAM + ", " 
		+ Db.VOICE_CALL_STREAM +
		" FROM "+Db.SCHEDULES+" LEFT OUTER JOIN "+ Db.PROFILES + " ON schedule.id = profile.id ORDER BY "+Db.START_TIME;
		//Log.e("SQL:", SQL);
		Cursor cur = database.rawQuery(SQL, null);
		//String s[] = cur.getColumnNames();
		/*for(String val:s) {
			Log.e("COL: ", val);
		}
		Log.e("Total rows: ", cur.getCount()+"");*/
		cur.moveToFirst();
		while(!cur.isAfterLast()) {
			Schedule schedule;
			long id = getLong(cur, Db.S_ID);
			long start_time = cur.getLong(cur.getColumnIndex(Db.START_TIME));
			long end_time = cur.getLong(cur.getColumnIndex(Db.END_TIME));
			long repeat = cur.getLong(cur.getColumnIndex(Db.REPEAT_AFTER));
			int active = getInt(cur, Db.ACTIVE);
			String profileName = getString(cur, Db.P_NAME);
			String label = getString(cur, Db.LABEL);
			String repeat_every = getString(cur, Db.REPEAT_EVERY);
			if(profileName.equalsIgnoreCase(Schedule.TYPE_CUSTOM)) {
				int music = getInt(cur, Db.MUSIC_STREAM);
				int ringer = getInt(cur, Db.RING_STREAM);
				int alarm = getInt(cur, Db.ALARM_STREAM);
				int touch = getInt(cur, Db.DTMF_STREAM);
				int vibrate = getInt(cur, Db.VIBRATE);
				int system = getInt(cur, Db.SYSTEM_STREAM);
				int voice = getInt(cur, Db.VOICE_CALL_STREAM);
				Profile profile = new Profile(music, ringer, alarm, touch, system, voice, vibrate);
				schedule = new Schedule(id, start_time, end_time, active, repeat, profileName, profile);
				schedule.setLabel(label);
				schedule.setRepeatEvery(repeat_every);
				schedules.add(schedule);
			}
			else {
				schedule = new Schedule(id, start_time, end_time, active, repeat, profileName, null);
				schedule.setLabel(label);
				schedule.setRepeatEvery(repeat_every);
				schedules.add(schedule);
				
			}
			cur.moveToNext();
		}
		cur.close();
		return schedules;
	}
	
	public Schedule getScheduleById(long s_id) {
		Schedule schedule;
		String col[] = {
				 Db.SCHEDULES+"."+Db.S_ID  
				,Db.START_TIME  
				,Db.END_TIME 
				,Db.REPEAT_AFTER 
				,Db.REPEAT_EVERY
				,Db.ACTIVE 
				,Db.P_NAME  
				,Db.LABEL
				,Db.MUSIC_STREAM
				,Db.RING_STREAM
				,Db.ALARM_STREAM 
				,Db.DTMF_STREAM
				,Db.VIBRATE
				,Db.SYSTEM_STREAM 
				,Db.VOICE_CALL_STREAM
				};
		String columns = "";
		for(String val:col)
			columns += val+",";
			columns = columns.substring(0, columns.length()-1);
		String sql = "Select "+ columns + " FROM schedule LEFT OUTER JOIN profile ON schedule.id = profile.id WHERE schedule.id = " + s_id;
		//Log.e("GETBYID", sql);
		Cursor cur = database.rawQuery(sql, null);
		cur.moveToFirst();
		Log.e("ROWS:", cur.getCount()+"");
		long id = getLong(cur, Db.S_ID);
		long start_time = cur.getLong(cur.getColumnIndex(Db.START_TIME));
		long end_time = cur.getLong(cur.getColumnIndex(Db.END_TIME));
		long repeat = cur.getLong(cur.getColumnIndex(Db.REPEAT_AFTER));
		int active = getInt(cur, Db.ACTIVE);
		String profileName = getString(cur, Db.P_NAME);
		String label = getString(cur, Db.LABEL);
		String repeat_every = getString(cur, Db.REPEAT_EVERY);
		if(profileName.equalsIgnoreCase(Schedule.TYPE_CUSTOM)) {
			int music = getInt(cur, Db.MUSIC_STREAM);
			int ringer = getInt(cur, Db.RING_STREAM);
			int alarm = getInt(cur, Db.ALARM_STREAM);
			int touch = getInt(cur, Db.DTMF_STREAM);
			int vibrate = getInt(cur, Db.VIBRATE);
			int system = getInt(cur, Db.SYSTEM_STREAM);
			int voice = getInt(cur, Db.VOICE_CALL_STREAM);
			Profile profile = new Profile(music, ringer, alarm, touch, system, voice, vibrate);
			schedule = new Schedule(id, start_time, end_time, active, repeat, profileName, profile);
			schedule.setLabel(label);
			schedule.setRepeatEvery(repeat_every);
		}
		else {
			schedule = new Schedule(id, start_time, end_time, active, repeat, profileName, null);
			schedule.setLabel(label);
			schedule.setRepeatEvery(repeat_every);
			
		}
		cur.close();
		return schedule;
		}
	
} 