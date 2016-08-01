package com.luke.clones.github.connector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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

//connector for sending bugs/issues as issue to Github account using Github API
public class GithubConnector {

	public GithubConnector() {

	}

	public void sendIssue(String title, String body) throws BadResponseCodeException, IOException {
			URL url = new URL(
					"https://api.github.com/repos/reponame/reposubname/issues");// !!! PUT YOUR OWN REPO URL THERE !!! 
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty(
					"Authorization",
					"Basic XXXXXXXXXXXXXXXXXXXXXXX"); // !! PUT YOUR OWN API KEY THERE !!!
			conn.setDoOutput(true);

			String jsonMessage = "{\"title\": \""+title+"\",\"body\": \""+body+"\"}";

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					conn.getOutputStream()));
			out.write(jsonMessage);
			out.close();
			
			if (conn.getResponseCode() != 201) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));
				String fullOutput="";
				String output;
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					fullOutput+=output;
				}
				throw new BadResponseCodeException("Failed : HTTP error code : "
						+ conn.getResponseCode() + "Output from Server: "+ fullOutput);
			}
			
			conn.disconnect();
	}
}
