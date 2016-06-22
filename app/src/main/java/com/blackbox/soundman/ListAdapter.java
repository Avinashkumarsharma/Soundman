package com.blackbox.soundman;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ListAdapter extends  ArrayAdapter<Schedule>{
	
	int layout;
	LayoutInflater inflater;
	ArrayList<Schedule> schedules;
	Context context;
	public ListAdapter(Context context, int resource,  ArrayList<Schedule>schedules) {
		super(context, resource);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = resource;
		this.schedules = schedules;
		for(Schedule sc: schedules) {
			if(sc.getActive() == 1) {
				sc.cancel();
				sc.activate();
			}
		}
	}
	public static class ViewHolder {
		public ImageView edit_schedule, state, icon;
		public TextView start_time, starts, ends, end_time, cur_profile, repeat_every, label;
		public long id;
		public int pos;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row = convertView;
		if(row == null) {
			row = inflater.inflate(layout, parent, false);
			//configure ViewHolder 
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.start_time = (TextView) row.findViewById(R.id.start_time);
			viewHolder.starts = (TextView) row.findViewById(R.id.starts);
			viewHolder.ends = (TextView) row.findViewById(R.id.ends);
			viewHolder.end_time = (TextView) row.findViewById(R.id.end_time);
			viewHolder.cur_profile = (TextView) row.findViewById(R.id.profile);
			viewHolder.edit_schedule = (ImageView) row.findViewById(R.id.edit_schedule);
			viewHolder.state = (ImageView) row.findViewById(R.id.state);
			viewHolder.icon = (ImageView) row.findViewById(R.id.profile_icon);
			viewHolder.label = (TextView)row.findViewById(R.id.label);
			viewHolder.repeat_every = (TextView)row.findViewById(R.id.repeat_every);
			//Setting Listeners now 
			viewHolder.edit_schedule.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View edit) {
					ViewGroup parent = (ViewGroup) edit.getParent();
					ViewHolder holder = (ViewHolder) parent.getTag();
					long id = schedules.get(holder.pos).getId();
					Intent intent = new Intent(getContext(), Options.class);
					intent.putExtra("id", id);
					Toast.makeText(getContext(), "Editing now..", Toast.LENGTH_SHORT).show();
					getContext().startActivity(intent);
				}
			});
			viewHolder.state.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View state) {
					ViewGroup parent = (ViewGroup) state.getParent();
					ViewHolder holder = (ViewHolder) parent.getTag();
					long id = schedules.get(holder.pos).getId();
					SchedulesDataSource data = new SchedulesDataSource(getContext());
					data.open();
					Schedule sc = data.getScheduleById(id);
					int active = sc.getActive() > 0 ? 0 : 1;
					sc.setActive(active);
					schedules.get(holder.pos).setActive(active);
					data.update(sc);
					data.close();
					schedules.get(holder.pos).setActive(active);
					if(active > 0) {
						Toast.makeText(getContext(), "Active now", Toast.LENGTH_SHORT).show();
						schedules.get(holder.pos).activate();
					}
					else {
						Toast.makeText(getContext(), "Inactive now", Toast.LENGTH_SHORT).show();
						schedules.get(holder.pos).cancel();
					}
					notifyDataSetChanged();
				}
			});
			
			row.setTag(viewHolder);
			
		}
		//fill data now 
		Schedule sc = schedules.get(position);
		ViewHolder holder = (ViewHolder) row.getTag();
		holder.start_time.setText(FormatTime(sc.getStart_time()));
		holder.end_time.setText(FormatTime(sc.getEnd_time()));
		holder.cur_profile.setText(sc.getProfileName());
		holder.repeat_every.setText(sc.getRepeatEvery());
		String l = sc.getLabel().toUpperCase(Locale.ENGLISH);
		if(l.length() > 15)
			l = l.substring(0, 15)+"...";
		holder.label.setText(l);
		holder.id = sc.getId();
		holder.pos = position;
		if(sc.getActive() > 0) {
			holder.state.setBackgroundResource(R.drawable.active);
		}
		else {
			holder.state.setBackgroundResource(R.drawable.inactive);
		}
		final String p = sc.getProfileName();
		int img = R.drawable.ring;
		if(p.equalsIgnoreCase(Schedule.TYPE_CUSTOM)) {
			img = R.drawable.custom;
		}
		else if(p.equalsIgnoreCase(Schedule.TYPE_NORMAL)) {
			img = R.drawable.ring;
		}
		else if(p.equalsIgnoreCase(Schedule.TYPE_SILENT)) {
			img = R.drawable.silient;
		}
		else if(p.equalsIgnoreCase(Schedule.TYPE_VIBRATE)) {
			img = R.drawable.vibrate;
		}
		holder.icon.setBackgroundResource(img);
		return row;
	}
	private String FormatTime(long min) {
		long hour = min/60;
		long minutes = min%60;
		if(hour >=0 && hour<=9 && minutes >=0 && minutes <=9) {
			return "0"+hour+" : 0"+minutes;
		}
		if(minutes >=0 && minutes <=9) {
			return hour+" : 0"+minutes;
		}
		if(hour >=0 && hour <=9) {
			return "0"+hour+" : "+minutes;
		}
		
		return hour+" : "+minutes;
	}
	@Override
	public int getCount() {
		return schedules.size();
	}
	@Override
	public Schedule getItem(int position) {
		return schedules.get(position);
	}
	
	public void erase(int pos) {
		schedules.remove(pos);
	}
}
