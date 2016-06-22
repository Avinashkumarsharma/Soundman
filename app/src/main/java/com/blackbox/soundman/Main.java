package com.blackbox.soundman;

import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Main extends ListActivity {
	ArrayAdapter<String> lAdapter;
	ListAdapter adapter;
	ListView listView;
	SchedulesDataSource source;
	public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AdView adView = (AdView) findViewById(R.id.adViewMain);
        AdRequest adReq = new AdRequest.Builder().addTestDevice("918C38E312074B9E424960F332A8C928").build();
        adView.loadAd(adReq);
        AudioManager audioMan = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioMan.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        context = getBaseContext();
        populate();
        listView = getListView();
        registerReceiver(new BootReceiverSoundMan(), new IntentFilter(Intent.ACTION_BOOT_COMPLETED));
        SwipeDismissListViewTouchListener touchlistener = new SwipeDismissListViewTouchListener(listView,
     		   new SwipeDismissListViewTouchListener.DismissCallbacks() {
 				@Override
 				public void onDismiss(ListView listView, int[] reverseSortedPositions) {
 					// TODO Auto-generated method stub
 					for (int position : reverseSortedPositions) {
                         SchedulesDataSource data = new SchedulesDataSource(getApplicationContext());
                         data.open();
                         data.delete(adapter.getItem(position));
                         data.close();
                         adapter.getItem(position).cancel();
                         adapter.erase(position);
                         
                     }
                    adapter.notifyDataSetChanged();
                    
 				}
 				
 				@Override
 				public boolean canDismiss(int position) {
 					// TODO Auto-generated method stub
 					return true;
 				}
 			});
        listView.setOnTouchListener(touchlistener);
        listView.setOnScrollListener(touchlistener.makeScrollListener());
    }
    private void populate() {
    	 source = new SchedulesDataSource(this);
         source.open();
         ArrayList<Schedule> all = source.getAllSchedules();
         source.close();
         adapter = new ListAdapter(Main.this, R.layout.custom_list, all);
         setListAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.add_new) {
        	startActivity(new Intent(Main.this, Options.class));
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
    	super.onResume();
    	populate();
    }
}
