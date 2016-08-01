package com.luke.Clones;

import java.io.IOException;

import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.facebook.AppEventsLogger;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.common.AccountPicker;
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

@SuppressLint("NewApi")
public class MainActivity extends AndroidApplication implements
		AndroidNativeHandler {
	private static final int currentApiVersion = android.os.Build.VERSION.SDK_INT;
	private static final String BANNER_AD_UNIT_ID = "ca-app-pub-xxxxxx"; //!!! PUT THERE YOUR BANNER_AD_UNIT_ID !!!
	private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-xxxxxx";//!!! PUT THERE YOUR INTERSTITIAL_AD_UNIT_ID !!!
	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	static final int REQUEST_CODE_PICK_ACCOUNT_AND_GIVE_PERMISSION = 1001;

	private MainActivity mainActivity = this;
	protected AdView bannerAdView;
	protected InterstitialAd interstitialAd;
	protected View gameView;

	private final int SHOW_BANNER_ADS = 0;
	private final int HIDE_BANNER_ADS = 1;
	private final int SHOW_INTERSTITIAL = 2;

	private AndroidPickAccountHandler androidPickAccountHandler;

	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_BANNER_ADS: {
				bannerAdView.resume();
				bannerAdView.setVisibility(View.VISIBLE);
				break;
			}
			case HIDE_BANNER_ADS: {
				bannerAdView.pause();
				bannerAdView.setVisibility(View.GONE);
				break;
			}
			case SHOW_INTERSTITIAL: {
				if (interstitialAd.isLoaded()) {
					interstitialAd.show();
				}
				break;
			}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		// cfg.useGL20 = false;

		// because of ads we don't use initialize()
		// initialize(new StartScreen(), cfg);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		RelativeLayout layout = new RelativeLayout(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);

		View gameView = createGameView(cfg);
		layout.addView(gameView);

		interstitialAd = createInterstitialAd();
		requestNewInterstitialAd(interstitialAd);
		// and show it somewhere

		AdView admobView = createBannerAdView();
		layout.addView(admobView);
		setContentView(layout);
		startBannerAdAdvertising(admobView);

	}

	private AdView createBannerAdView() {
		bannerAdView = new AdView(this);
		bannerAdView.setAdSize(AdSize.BANNER);
		bannerAdView.setAdUnitId(BANNER_AD_UNIT_ID);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		bannerAdView.setLayoutParams(params);
		return bannerAdView;
	}

	private InterstitialAd createInterstitialAd() {
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT_ID);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				requestNewInterstitialAd(interstitialAd);
			}
		});
		return interstitialAd;
	}

	private View createGameView(AndroidApplicationConfiguration cfg) {
		gameView = initializeForView(new StartScreen(this), cfg);
		return gameView;
	}

	private void startBannerAdAdvertising(AdView adView) {
		AdRequest adRequest = new AdRequest.Builder().build();
//		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
//				"22B8DD4DA7E5CB4DE1F70305B644D9FB").build();
		adView.loadAd(adRequest);
	}

	private void requestNewInterstitialAd(InterstitialAd interstitialAd) {
		AdRequest adRequest = new AdRequest.Builder().build();
//		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
//				"22B8DD4DA7E5CB4DE1F70305B644D9FB").build();
		interstitialAd.loadAd(adRequest);
	}

	@Override
	public void onResume() {
		super.onResume();
		hideSystemButtonBar();
		if (bannerAdView != null)
			bannerAdView.resume();
		
		AppEventsLogger.activateApp(this);
	}

	@Override
	public void hideSystemButtonBar() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (currentApiVersion >= 19) {
					// TODO:Switch to target API 19 and use View constants
					int buttonBarOptions = 256 | 512 | 1024 | 2 | 4 | 4096;
					getWindow().getDecorView().setSystemUiVisibility(
							buttonBarOptions);
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		if (bannerAdView != null)
			bannerAdView.pause();
		
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	public void onDestroy() {
		if (bannerAdView != null)
			bannerAdView.destroy();
		super.onDestroy();
	}

	@Override
	public void openUrl(String url) {
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			url = "http://" + url;
		Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(myIntent);
	}

	@Override
	public void openMarketApp(String marketId) {
		try {
			String url = "market://details?id=" + marketId;
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(myIntent);
		} catch (android.content.ActivityNotFoundException anfe) {
			String url = "https://play.google.com/store/apps/details?id="
					+ marketId;
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(myIntent);
		}
	}

	@Override
	public void showBannerAds() {
		handler.sendEmptyMessage(SHOW_BANNER_ADS);
	}

	@Override
	public void hideBannerAds() {
		handler.sendEmptyMessage(HIDE_BANNER_ADS);
	}

	@Override
	public void showInterstitial() {
		handler.sendEmptyMessage(SHOW_INTERSTITIAL);
	}

	@Override
	public void pickGoogleAccount(
			AndroidPickAccountHandler androidPickAccountHandler) {
		this.androidPickAccountHandler = androidPickAccountHandler;
		String[] accountTypes = new String[] { "com.google" };
		Intent intent = AccountPicker.newChooseAccountIntent(null, null,
				accountTypes, true, null, null, null, null);
		try{
			startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
		}
		catch(ActivityNotFoundException e){
			mainActivity.runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(mainActivity)
							.setTitle("Login failed")
							.setMessage(
									"You can login to server only using google account (account with email address ending with @gmail.com). Sorry.")
							.setNeutralButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									})
							.setIcon(android.R.drawable.ic_dialog_alert).show();
				}
			});
			System.out.println("-- Account pick failed --");
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("onActivityResult requestCode: " + requestCode);
		System.out.println("onActivityResult resultCode: " + resultCode);
		System.out.println("onActivityResult data (Intent class): " + data);
		if (requestCode == REQUEST_CODE_PICK_ACCOUNT
				|| requestCode == REQUEST_CODE_PICK_ACCOUNT_AND_GIVE_PERMISSION) {
			if (resultCode == RESULT_OK) {
				androidPickAccountHandler.onAccountPicked(data
						.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
			} else if (resultCode == RESULT_CANCELED) {
				androidPickAccountHandler.onCancelOrError();
			} else {
				androidPickAccountHandler.onCancelOrError();
			}
		}
	}

	@Override
	public void getGoogleAccountToken(String accountName,
			AndroidAuthenticationTokenHandler androidAuthenticationTokenHandler) {
		//idzie têdy
//		String scope = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
		String scope = "oauth2:profile";
		System.out.println("in getGoogleAccountToken executing GetTokenTask");
		new GetTokenTask(MainActivity.this, accountName,
				scope,
				androidAuthenticationTokenHandler).execute();
	}

	@Override
	public void clearToken(
			String invalidToken,
			AndroidAuthenticationInvalidTokenHandler androidAuthenticationInvalidTokenHandler) {
		try {
			GoogleAuthUtil.clearToken(MainActivity.this, invalidToken);
			androidAuthenticationInvalidTokenHandler.onSuccess();
		} catch (GooglePlayServicesAvailabilityException e) {
			androidAuthenticationInvalidTokenHandler.onAuthException(e);
			e.printStackTrace();
		} catch (GoogleAuthException e) {
			androidAuthenticationInvalidTokenHandler.onAuthException(e);
			e.printStackTrace();
		} catch (IOException e) {
			androidAuthenticationInvalidTokenHandler.onIOException(e);
			e.printStackTrace();
		}
	}

	@Override
	public String getAppVersionName() {
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "unknown";
		}
	}

	@Override
	public String getAndroidVersionString() {
		return "API " + android.os.Build.VERSION.SDK_INT + " Android "
				+ android.os.Build.VERSION.RELEASE + " "
				+ android.os.Build.VERSION.CODENAME;
	}

	@Override
	public String getDeviceVersionString() {
		return android.os.Build.MANUFACTURER + " model "
				+ android.os.Build.MODEL;
	}

}