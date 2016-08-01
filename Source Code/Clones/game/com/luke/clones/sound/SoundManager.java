package com.luke.clones.sound;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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

public class SoundManager {
	public static final SoundManager INSTANCE = new SoundManager();
	
	Map<SoundType, Sound> soundMap;
	
	private SoundManager(){
		soundMap = new HashMap<SoundType, Sound>();
		soundMap.put(SoundType.JUMP, Gdx.audio.newSound(Gdx.files.internal("data/sounds/jump.wav")));
		soundMap.put(SoundType.CLONE, Gdx.audio.newSound(Gdx.files.internal("data/sounds/clone.wav")));
		soundMap.put(SoundType.LOSE, Gdx.audio.newSound(Gdx.files.internal("data/sounds/lose.wav")));
		soundMap.put(SoundType.WIN, Gdx.audio.newSound(Gdx.files.internal("data/sounds/win.wav")));
		soundMap.put(SoundType.CLICK, Gdx.audio.newSound(Gdx.files.internal("data/sounds/click2.wav")));
	}
	
	public void play(SoundType soundToPlayType){
		if(isSoundOn()){
			playSoundWithFullVolume(soundToPlayType);
		}
	}
	
	public void play(SoundType soundToPlayType, float volume){
		if(isSoundOn()){
			playSoundWithSpecifiedVolume(soundToPlayType, volume);
		}
	}
	
	private boolean isSoundOn(){
		return SettingsManager.getSettingValue(SettingType.SOUND_ON);
	}
	
	private void playSoundWithFullVolume(SoundType soundToPlayType){
		Sound soundToPlay = soundMap.get(soundToPlayType);
		soundToPlay.play(soundToPlayType.getVolume());
	}
	
	private void playSoundWithSpecifiedVolume(SoundType soundToPlayType, float volume){
		Sound soundToPlay = soundMap.get(soundToPlayType);
		soundToPlay.play(volume);
	}
	
}