package com.luke.network.controller;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.luke.clones.network.backstage.ClientWrapper;
import com.luke.clones.network.config.NetworkConfig;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.clones.network.interfaces.RegisterClasses;

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

public class ClonesClient {
	private ClientWrapper client;
	
	public ClonesClient(){
		
	}
	
	public void start(ListenerService listener, String host, int port, int timeout, int keepAliveTcp) throws IOException{
		RegisterClasses register = new RegisterClassesImpl();
		
		client = new ClientWrapper(host, port, timeout, keepAliveTcp);
		client.setListener(listener);
		client.start(register);
	}
	
	public void start(ListenerService listener, String host, int port) throws IOException{
		this.start(listener, host, port, NetworkConfig.defaultConnectTimeout, NetworkConfig.defaultKeepAliveTcp);
	}
	
	public void setListener(ListenerService listener){
		client.setListener(listener);
	}
	
	public void stop(){
		client.stop();
	}
	
	public void send(Object object){
		client.send(object);
	}
	
}
