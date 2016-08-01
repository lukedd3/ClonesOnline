package com.luke.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.luke.authentication.model.GoogleUserData;
import com.luke.authentication.model.TokenValidation;

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

public class AuthorizationUtil {
	
	private static String googleUserDataString="No_data";
	
	public static TokenValidation isTokenValid(String token) throws IOException{
		URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token="+token);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		int sc = con.getResponseCode();
		if (sc == 200) {
			InputStream is = con.getInputStream();
			googleUserDataString = readResponse(is);
			is.close();
			System.out.println("token ok");
			System.out.println("User data:"+googleUserDataString);
			GoogleUserData googleUserDataObject = null;
			try {
				googleUserDataObject = decodeGoogleUserDataJson(googleUserDataString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new TokenValidation(true, googleUserDataObject);
		} else if (sc == 401) {
			//TODO: to robiæ na frontendzie jak siê nie powiedzie autentykacja
//			GoogleAuthUtil.clearToken(activity, token);
			System.out.println("token invalid");
			return new TokenValidation(false, null);
		} else {
			System.out.println("token invalid (some other error occured)");
			return new TokenValidation(false, null);
		}
	}
	
	private static String readResponse(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] data = new byte[2048];
		int len = 0;
		while ((len = is.read(data, 0, data.length)) >= 0) {
			bos.write(data, 0, len);
		}
		return new String(bos.toByteArray(), "UTF-8");
	}
	
	private static GoogleUserData decodeGoogleUserDataJson(String googleUserDataJson) throws ParseException{
		JSONParser parser=new JSONParser();
		JSONObject jsonObject=null;
		jsonObject=(JSONObject) parser.parse(googleUserDataJson);
		GoogleUserData googleUserData = new GoogleUserData();
		googleUserData.setId((String) jsonObject.get("id"));
		googleUserData.setName((String) jsonObject.get("name"));
		googleUserData.setGivenName((String) jsonObject.get("given_name"));
		googleUserData.setFamilyName((String) jsonObject.get("family_name"));
		googleUserData.setLink((String) jsonObject.get("link"));
		googleUserData.setPicture((String) jsonObject.get("picture"));
		googleUserData.setGender((String) jsonObject.get("gender"));
		googleUserData.setLocale((String) jsonObject.get("locale"));
		return googleUserData;
	}
	
}
