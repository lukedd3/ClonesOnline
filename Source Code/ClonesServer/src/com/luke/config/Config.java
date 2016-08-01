package com.luke.config;

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
	private static final boolean log = true;
	
	//allows PC connections with fake token
	public static final boolean testModeOn = true;
	
	//port used for server-client communication
	public static final int port = 54556;
	
	public static final int maxNumberOfPlayers = 100;
	
	//Database data
	//!! PUT YOUR OWN DATABASE CONNECTION DATA THERE !!!
	public static final String databaseType = "postgresql";
	public static final String databaseAddress = "localhost";
	public static final int databasePort = 5432;
	public static final String databaseName = "ClonesDatabase";
	public static final String databaseUser = "postgres";
	public static final String databasePassword = "postgres";
	
	
	public static void log(String text){
		if(log==true) System.out.println("#"+text);
	}
}