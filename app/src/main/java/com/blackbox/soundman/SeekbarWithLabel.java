package com.blackbox.soundman;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class SeekbarWithLabel extends LinearLayout{
	
	private SeekBar seekbar;
	private TextView label;
	private int sound, stream;
	private SoundPool soundPool;
	private boolean loaded, playsound = true;
	public SeekbarWithLabel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.slider_with_text, this);
		initialize(context, attrs);
	}

	public SeekbarWithLabel(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.slider_with_text, this);
		initialize(context, attrs);
	}

	public SeekbarWithLabel(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.slider_with_text, this);
	}
	
	private void initialize(Context context, AttributeSet attrs ){
		TypedArray val = context.obtainStyledAttributes(attrs, R.styleable.seekbarWithText, 0,0);
		String title = val.getString(R.styleable.seekbarWithText_titleText);
		sound = val.getResourceId(R.styleable.seekbarWithText_sound, R.raw.ping);
		stream = val.getInt(R.styleable.seekbarWithText_stream, AudioManager.STREAM_RING);
		playsound = val.getBoolean(R.styleable.seekbarWithText_playsound, true);
		label = (TextView) findViewById(R.id.seekbar_label);
		label.setText(title);
		val.recycle();
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		loaded = false;
		soundPool = new SoundPool(10, stream, 0);
		soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loaded = true;
			}
		});
		sound = soundPool.load(getContext(), sound, 1);
		
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekbar) {
				// TODO Auto-generated method stub
				if(playsound)
				play();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void setStream(int stream) {
		this.stream = stream;
	}
	public int getStream() {
		return this.stream;
	}
	
	public void play() {
		if(loaded) {
			AudioManager audioMan = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
			float max = audioMan.getStreamMaxVolume(stream);
			float cur = audioMan.getStreamVolume(stream);
			float volume = (float)getProgress()/seekbar.getMax();
			audioMan.setStreamVolume(stream, (int)max, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			//Log.e("CUR|MAX|VOLUME", cur+"|"+max+"|"+volume);
			//Log.e("Volume|Progeress|SMAX|MAX", volume+"|"+getProgress()+"|"+seekbar.getMax()+"|"+max);
			soundPool.play(sound, volume, volume, 1, 0, 1f);
			audioMan.setStreamVolume(stream, (int)cur, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			
		}
	}
	public void setPlaySound(boolean enable){
		this.playsound = enable;
	}
	
	public boolean getPlaySound() {
		return this.playsound;
	}
	public SeekBar getSeekBar(){
		return seekbar;
	}
	public TextView getTextView(){
		return label;
	}
	public int getProgress() {
		return seekbar.getProgress();
	}
	public void setProgress(int progress) {
		this.seekbar.setProgress(progress);
	}
	
}
