package com.luke.clones.music;

import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.luke.clones.settings.SettingType;
import com.luke.clones.settings.SettingsManager;

/* The MIT License (MIT)

Copyright (c) 2016 £ukasz Dziak

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE. */

public class MusicManager {
	private static final Integer START_TRACK = 0;
	private static final float VOLUME = 0.07f;
//	private static final long DELAY_MS = 500;
	
	private static final long VOLUME_CHANGE_ACCURACY_MS = 10; //The smaller the bigger performance load
	
	private static final long GRADUALLY_INCREASE_VOLUME_TIME = 1500L;
	private static final float GRADUALLY_INCREASE_VOLUME_TIME_SINGLE_ITERATION_VOLUME_CHANGE = (VOLUME/GRADUALLY_INCREASE_VOLUME_TIME) * VOLUME_CHANGE_ACCURACY_MS;
	
	private static final long GRADUALLY_MUTE_TIME = 1000L;
	private static final float GRADUALLY_MUTE_TIME_SINGLE_ITERATION_VOLUME_CHANGE = (VOLUME/GRADUALLY_MUTE_TIME) * VOLUME_CHANGE_ACCURACY_MS;

	public static final MusicManager INSTANCE = new MusicManager();

	private LinkedHashMap<Integer, Music> musicMap;

	private Timer timer;
	
	private Timer graduallyDecreaseVolumeTimer;

	private Integer currentTrack = START_TRACK;
	
	private boolean gradualVolumeIncreaseBeforePlay = false;

	protected MusicManager() {
		timer = new Timer();
		graduallyDecreaseVolumeTimer = new Timer();

		musicMap = new LinkedHashMap<Integer, Music>();
		musicMap.put(0, Gdx.audio.newMusic(Gdx.files
				.internal("data/music/epic_orchestra.ogg")));

		for (Music music : musicMap.values()) {
			music.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(Music music) {
					playNextTrack();
				}
			});
		}
	}

	private void playNextTrack() {
		currentTrack++;
		if (currentTrack >= musicMap.size()) {
			currentTrack = 0;
		}
		play();
	}

	public void play() {
		if (SettingsManager.getSettingValue(SettingType.MUSIC_ON) == true) {
			try {
				timer.cancel();
				graduallyDecreaseVolumeTimer.cancel();
			} catch (IllegalStateException e) {

			}
			if(gradualVolumeIncreaseBeforePlay==false){
				playImmediately();
			}
			else{
				graduallyIncreaseVolumeAndPlay();
			}
		}
	}

	 private void playImmediately(){
		 musicMap.get(currentTrack).setVolume(VOLUME);
		 musicMap.get(currentTrack).play();
	 }
	
	private void graduallyIncreaseVolumeAndPlay(){
		gradualVolumeIncreaseBeforePlay = false;
		musicMap.get(currentTrack).setVolume(0);
		musicMap.get(currentTrack).play();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if(musicMap.get(currentTrack).getVolume()<VOLUME){
					float currentVolume = musicMap.get(currentTrack).getVolume();
					musicMap.get(currentTrack).setVolume(currentVolume+GRADUALLY_INCREASE_VOLUME_TIME_SINGLE_ITERATION_VOLUME_CHANGE);
				}
				else{
					timer.cancel();
				}
			}
		};

		timer = new Timer();
		timer.schedule(timerTask, 0, VOLUME_CHANGE_ACCURACY_MS);
	}

	public void pauseImmediately() {
		try {
			timer.cancel();
			graduallyDecreaseVolumeTimer.cancel();
		} catch (IllegalStateException e) {

		}
		musicMap.get(currentTrack).pause();
	}
	
	private void graduallyDecreaseVolumeAndPause(){
		try {
			timer.cancel();
			graduallyDecreaseVolumeTimer.cancel();
		} catch (IllegalStateException e) {

		}
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if(musicMap.get(currentTrack).getVolume()>0){
					float currentVolume = musicMap.get(currentTrack).getVolume();
					musicMap.get(currentTrack).setVolume(currentVolume-GRADUALLY_MUTE_TIME_SINGLE_ITERATION_VOLUME_CHANGE);
				}
				else{
					musicMap.get(currentTrack).pause();
					graduallyDecreaseVolumeTimer.cancel();
				}
			}
		};

		graduallyDecreaseVolumeTimer = new Timer();
		graduallyDecreaseVolumeTimer.schedule(timerTask, 0, VOLUME_CHANGE_ACCURACY_MS);
	}

	public void pauseAndScheduleGradualVolumeIncreaseBeforePlay() {
		try {
			timer.cancel();
		} catch (IllegalStateException e) {

		}
		graduallyDecreaseVolumeAndPause();
		gradualVolumeIncreaseBeforePlay=true;
	}
	
	public void dispose() {
		try {
			timer.cancel();
		} catch (IllegalStateException e) {

		}
		for (Music music : musicMap.values()) {
			music.dispose();
		}
		musicMap.clear();
	}
}
