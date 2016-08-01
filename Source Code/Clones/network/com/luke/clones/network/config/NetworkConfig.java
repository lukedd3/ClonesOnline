package com.luke.clones.network.config;

import com.luke.clones.network.backstage.ServerFrontendInfo;

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

public class NetworkConfig {
	//debug logging on(true)/off(false)
	private static final boolean log = false;
	
	//Clinet timeout in ms
	public static final int defaultKeepAliveTcp=4000; //0 means off
	public static final int defaultConnectTimeout=16000;
	public static final int defaultConnectionTimeout=32000;
	
	public static final int clientServerSelectionServerBackendInfoAdditionalTimeout = 1000;
	
	public static final int SERVER_WRITE_BUFFER_SIZE_IN_BYTES = 262144;
	public static final int SERVER_READ_BUFFER_SIZE_IN_BYTES = 32768;
	
	public static final int CLIENT_WRITE_BUFFER_SIZE_IN_BYTES = 131072;
	public static final int CLIENT_READ_BUFFER_SIZE_IN_BYTES = 16384;
	
	public static ServerFrontendInfo[] serverList = {
		new ServerFrontendInfo("Earth","localhost",54556),// !!! PUT YOUR CLONES SERVER ADDRESS AND PORT THERE !!!
		new ServerFrontendInfo("Earth","192.168.1.100",54556),// !!! PUT YOUR CLONES SERVER ADDRESS AND PORT THERE !!!
		// In fact in this situation localhost == 192.168.1.100, but direct adress like 192.168.1.100 is needed for android device
		// localhost is enough for the same pc that server is on. On pc you will see two the same servers in this config.
		// I made it this way by default because: If I would put 192.168.1.100 only somebody might think that
		// connection with server is not working, because he might have different adress in his network
		// If I would put localhost only it might work only on PC. Somebody might not notice that instead of localhost he should put
		// sth like 192.xxx.x.xxx to be able to connect from Android device
	};
	
	public static void log(String text){
		if(log==true) System.out.println("#"+text);
	}
}