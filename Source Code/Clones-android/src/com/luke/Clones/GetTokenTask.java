package com.luke.Clones;

import java.io.IOException;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.luke.clones.android.AndroidAuthenticationTokenHandler;

import android.app.Activity;
import android.os.AsyncTask;

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

public class GetTokenTask extends AsyncTask<Object, Object, Object>{
	private AndroidAuthenticationTokenHandler androidAuthenticationTokenHandler;
	private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
	
//	private String GOOGLE_USER_DATA="No_data";
	
	private Activity activity;
	private String username;
	private String scope;
	
	public GetTokenTask(Activity activity, String username, String scope, AndroidAuthenticationTokenHandler androidAuthenticationTokenHandler){
		this.activity = activity;
		this.username = username;
		this.scope = scope;
		this.androidAuthenticationTokenHandler = androidAuthenticationTokenHandler;
	}

	@Override
	protected Object doInBackground(Object... params) {
		try {
			System.out.println("in GetTokenTask class doInBackground method, username: "+username);
			System.out.println("in GetTokenTask class doInBackground method, scope: "+scope);
			String token = GoogleAuthUtil.getToken(activity, username, scope);
			System.out.println("in GetTokenTask class doInBackground method, token received: "+token);
			androidAuthenticationTokenHandler.onSuccess(token, username);
			
//			URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token="+token);
//			HttpURLConnection con = (HttpURLConnection) url.openConnection();
//			int sc = con.getResponseCode();
//			if (sc == 200) {
//				InputStream is = con.getInputStream();
//				GOOGLE_USER_DATA = readResponse(is);
//				is.close();
//				System.out.println("token ok");
//			} else if (sc == 401) {
//				GoogleAuthUtil.clearToken(activity, token);
//				System.out.println("token invalid");
//			} else {
//				System.out.println("token invalid (some other error occured)");
//			}
			
		} catch (UserRecoverableAuthException e) {
			activity.startActivityForResult(e.getIntent(), REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
			System.out.println("in GetTokenTask class doInBackground method UserRecoverableAuthException catched");
			e.printStackTrace();
		} catch (IOException e) {
			androidAuthenticationTokenHandler.onIOException(e);
			System.out.println("in GetTokenTask class doInBackground method IOException catched");
			e.printStackTrace();
		} catch (GoogleAuthException e) {
			androidAuthenticationTokenHandler.onAuthException(e);
			System.out.println("in GetTokenTask class doInBackground method GoogleAuthException catched");
			System.err.println("msg: "+ e.getMessage());
			System.err.println("lms: "+ e.getLocalizedMessage());
			System.err.println("toString: "+ e.toString());
			e.printStackTrace();
		}
		return null;
	}
	
//	private static String readResponse(InputStream is) throws IOException {
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		byte[] data = new byte[2048];
//		int len = 0;
//		while ((len = is.read(data, 0, data.length)) >= 0) {
//			bos.write(data, 0, len);
//		}
//		return new String(bos.toByteArray(), "UTF-8");
//	}

}
