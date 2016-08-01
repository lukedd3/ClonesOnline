package com.luke.clones.ads;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.luke.clones.android.AndroidNativeHandler;
import com.luke.clones.config.Config;
import com.luke.clones.messages.QuickMessage;
import com.luke.clones.messages.QuickMessageManager;

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

public class InterstitialAdsManager {

	public static final InterstitialAdsManager INSTANCE = new InterstitialAdsManager();
	
	private static int playedRoundsCount = 0;
	
	private InterstitialAdsManager(){
		
	}
	
	public void afterRoundFinish(){
		playedRoundsCount++;
	}
	
	public void afterLeaveRoom(AndroidNativeHandler androidNativeHandler, Stage stage, Skin skin){
		showInterstitialWhenConditionsAreMeet(androidNativeHandler,stage, skin);
		playedRoundsCount = 0;
	}
	
	private void showInterstitialWhenConditionsAreMeet(AndroidNativeHandler androidNativeHandler, Stage stage, Skin skin){
		if(playedRoundsCount>Config.numberOfPlayedRoundsAfterWhichInterstitialShouldBeShownOnLeaveRoom-1){
			androidNativeHandler.showInterstitial();
			System.out.println("Interstitial shown");
			//test
//			QuickMessage quickMessage = new QuickMessage("Interstitial test", skin);
//			quickMessage.setAutoHide(false);
//			QuickMessageManager.showMessageOnSpecifiedStage(quickMessage, stage);
		}
	}
	
}
