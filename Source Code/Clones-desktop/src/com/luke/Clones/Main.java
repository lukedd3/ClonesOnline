package com.luke.Clones;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.opengl.Display;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.luke.clones.android.AndroidAuthenticationInvalidTokenHandler;
import com.luke.clones.android.AndroidAuthenticationTokenHandler;
import com.luke.clones.android.AndroidNativeHandler;
import com.luke.clones.android.AndroidPickAccountHandler;
import com.luke.clones.screen.StartScreen;

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

public class Main implements AndroidNativeHandler{
	private static Main application = new Main();
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Clones";
//		cfg.useGL20 = false;
		
		//lg:320 s2:480 s3:720
		//lg:480 s2:800 s3:1280
		
//		cfg.width = 320;
//		cfg.height = 480;
		
		cfg.width = 480;
		cfg.height = 800;
		
//		cfg.width = 480;
//		cfg.height = 854;
		
//		cfg.width = 576;
//		cfg.height = 1024;
		
//		cfg.width = 720;
//		cfg.height = 1280;
		
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
	         @Override
	         public void uncaughtException (Thread thread, final Throwable ex) {
		        ex.printStackTrace();
	        	Display.destroy();
	            System.exit(0);
	         }
         });
		
		new LwjglApplication(new StartScreen(application), cfg);
	}

	@Override
	public void openUrl(String url) {

	}

	@Override
	public void openMarketApp(String url) {

	}

	@Override
	public void showInterstitial() {
		
	}

	@Override
	public void showBannerAds() {
		
	}

	@Override
	public void hideBannerAds() {
		
	}

	@Override
	public void pickGoogleAccount(AndroidPickAccountHandler androidPickAccountHandler) {
		androidPickAccountHandler.onAccountPicked("pcTest"+RandomStringUtils.randomNumeric(8));
	}

	@Override
	public void getGoogleAccountToken(String accountName, AndroidAuthenticationTokenHandler androidAuthenticationTokenHandler) {
		androidAuthenticationTokenHandler.onSuccess("pcUKwZZuUpmMajlTWlgLhVwpskzybSaqlmBZcQKpYOIMNGzUka", accountName);
	}

	@Override
	public void clearToken(
			String token,
			AndroidAuthenticationInvalidTokenHandler androidAuthenticationInvalidTokenHandler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAppVersionName() {
		return "unknown (PC Test version)";
	}

	@Override
	public String getAndroidVersionString() {
		return "PC test";
	}

	@Override
	public String getDeviceVersionString() {
		return "PC test";
	}

	@Override
	public void hideSystemButtonBar() {
		
	}
}