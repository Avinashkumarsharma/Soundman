package com.blackbox.soundman;


import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiverSoundMan extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    	Toast.makeText(context, "Syncronizing Profiles", Toast.LENGTH_SHORT).show();
    	restart(context);
    }
    
    public void restart(Context context) {
    	SchedulesDataSource data = new SchedulesDataSource(context);
    	data.open();
    	ArrayList<Schedule> schedules = data.getAllSchedules();
    	data.close();
    	for(Schedule schedule: schedules) {
    		if(schedule.getActive()==1) {
    			schedule.activate();
    		}
    		else {
    			schedule.cancel();
    		}
    	}
    }
}
