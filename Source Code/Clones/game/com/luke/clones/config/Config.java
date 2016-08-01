package com.luke.clones.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

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

public class Config {
	//debug logging on(true)/off(false)
	public final static boolean debug = false;
	public final static boolean showMapCreator = false;
	public final static boolean showTestTools = false;
	
	//screen size
//	public final static int width = Gdx.graphics.getWidth();
//	public final static int height = Gdx.graphics.getHeight();
//	public final static int width = 1080;
//	public final static int height = 1920;
//	public final static int width = 480;
//	public final static int height = 854;
//	public final static int width = 720;
//	public final static int height = 1280;
	public final static int width = ConfigUtils.getScreenWidth();
	public final static int height = ConfigUtils.getScreenHeight();
	//TODO: dla du¿ych rozdzielczosci
	//mo¿na daæ warunek jeœli Gdx.graphics.getHeight()>1280 i ratio 1.7 wtedy ustaw height na 1280...
	//podobnie dla ratio 1.6 tylko wtedy dac inna rozdzielczosc
	//Uwaga: view board aktualnie rz¹dzi siê chyba trochê swoimi prawami
	
	public final static int widthOffset = 0; 
	public final static int gameHeightOffset = 100; //place for menu bar in game
	public final static int timeBarHeight = 3;
	public final static int menuTopHeightOffset = 25;
	public final static int menuBottomHeightOffset = 100; //place for ads
	
	public final static Color textColor = new Color(199f/255f, 178f/255f, 153f/255f, 1);
	
	protected final static int nextRoundAutoStartTimeFor2PlayerGameInSeconds = 15;
	protected final static int nextRoundAutoStartTimeFor4PlayerGameInSeconds = 30;
	public final static int autoendOneMoveTimeMs = 250;
	
	public final static int numberOfPlayedRoundsAfterWhichInterstitialShouldBeShownOnLeaveRoom = 2;
	
	public final static String appPackageForGooglePlayButton = "com.luke.Clones";
	
	public static void log(String text){
		if(debug==true) System.out.println(text);
	}
}
