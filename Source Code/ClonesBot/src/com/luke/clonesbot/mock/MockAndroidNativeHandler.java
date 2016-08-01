package com.luke.clonesbot.mock;

import org.apache.commons.lang3.RandomStringUtils;

import com.luke.clones.android.AndroidAuthenticationInvalidTokenHandler;
import com.luke.clones.android.AndroidAuthenticationTokenHandler;
import com.luke.clones.android.AndroidNativeHandler;
import com.luke.clones.android.AndroidPickAccountHandler;

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

public class MockAndroidNativeHandler implements AndroidNativeHandler{

	@Override
	public void hideSystemButtonBar() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openUrl(String url) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openMarketApp(String marketId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showBannerAds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hideBannerAds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showInterstitial() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pickGoogleAccount(AndroidPickAccountHandler androidPickAccountHandler) {
		androidPickAccountHandler.onAccountPicked("Jan [Bot]");
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAndroidVersionString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeviceVersionString() {
		// TODO Auto-generated method stub
		return null;
	}


}
