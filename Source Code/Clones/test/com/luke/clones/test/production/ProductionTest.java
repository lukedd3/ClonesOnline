package com.luke.clones.test.production;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.Connection;
import com.luke.clones.android.AndroidAuthenticationTokenHandler;
import com.luke.clones.android.AndroidNativeHandler;
import com.luke.clones.android.AndroidPickAccountHandler;
import com.luke.clones.model.TimeMeasure;
import com.luke.clones.network.backstage.ServerFrontendInfo;
import com.luke.clones.network.communication.ActionTypeUpdateLogin;
import com.luke.clones.network.communication.MoveLogin;
import com.luke.clones.network.communication.UpdateLogin;
import com.luke.clones.network.config.NetworkConfig;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.clones.test.production.helper.ClonesClientTestWrapper;
import com.luke.clones.util.NameUtil;
import com.luke.network.controller.ClonesClient;

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

public abstract class ProductionTest implements ListenerService{
	private AndroidNativeHandler androidNativeHandler;
	private int numberOfClients = 1;
	private int serverNumberOnServerList = 0;
	
	private ServerFrontendInfo serverInfo = NetworkConfig.serverList[serverNumberOnServerList];
	private String playerName;
	
	private ClonesClient clonesClient;
	protected List<ClonesClientTestWrapper> clonesClientTestWrappers;
	
	private AndroidPickAccountHandler androidPickAccountHandler;
	private AndroidAuthenticationTokenHandler androidAuthenticationTokenHandler;
	
	private boolean connectTried = false;
	private boolean connected = false;

	protected ProductionTest(AndroidNativeHandler androidNativeHandler,
			int serverNumberOnServerList) {
		this.androidNativeHandler = androidNativeHandler;
		this.serverNumberOnServerList = serverNumberOnServerList;
	}

//	protected boolean connect(){
//		TimeMeasure time = new TimeMeasure();
//		connectTried = false;
//		login(time);
//		while(connectTried==false && time.getMeasuredTimeInMs()<=NetworkConfig.defaultClientTimeout){
//			
//		}
//		return connected;
//	}
	
	protected List<ClonesClientTestWrapper> connect(){
		clonesClientTestWrappers = new ArrayList<ClonesClientTestWrapper>();
		
		for(int i=0; i<numberOfClients; i++){
			TimeMeasure time = new TimeMeasure();
			connectTried = false;
			login(time);
			while(connectTried==false && time.getMeasuredTimeInMs()<=NetworkConfig.defaultConnectTimeout){
				
			}
			clonesClientTestWrappers.add(new ClonesClientTestWrapper(clonesClient, connected));
		}
		
		return clonesClientTestWrappers;
	}
	
	private void login(final TimeMeasure time){
		boolean isConnectedToServer = connectToServer();
		
		androidPickAccountHandler=new AndroidPickAccountHandler() {
			@Override
			public void onCancelOrError() {
				System.out.println("onCancelOrError");
				cancelBehaviour();
			}
			@Override
			public void onAccountPicked(String accountName) {
				System.out.println("onAccountPicked");
				androidNativeHandler.getGoogleAccountToken(accountName, androidAuthenticationTokenHandler);
			}
		};
		
		androidAuthenticationTokenHandler=new AndroidAuthenticationTokenHandler() {
			@Override
			public void onSuccess(String token, String username) {
				System.out.println("onSuccess");
				playerName = NameUtil.getNameFromEmail(username);
				time.start();
				clonesClient.send(new MoveLogin(token, playerName));
			}
			@Override
			public void onIOException(Exception e) {
				System.out.println("onIOException");
				authenticationServerIOExceptionBehaviour();
				e.printStackTrace();
			}
			@Override
			public void onAuthException(Exception e) {
				System.out.println("onAuthException");
				authenticationFailureBehaviour();
			}
		};
		
		if(isConnectedToServer){
			androidNativeHandler.pickGoogleAccount(androidPickAccountHandler);
		}
		else{
			connectTried = true;
			connected = false;
		}
		
	}
	
	private boolean connectToServer(){
		clonesClient = new ClonesClient();
		try {
			int port = serverInfo.getPort();
			String host = serverInfo.getHost();
			clonesClient.start(this, host, port);
			return true;
		} catch (IOException e) {
			System.out.println("Klient nie wystartowa³");
			serverIOExceptionBehaviour();
			e.printStackTrace();
			return false;
		}
	}
	
	protected void disconnect(){
		clonesClient.stop();
	}
	
	@Override
	public synchronized void received(Connection connection, Object obj) {
		if(obj instanceof UpdateLogin){
			UpdateLogin updateLogin = (UpdateLogin) obj;
			ActionTypeUpdateLogin action = updateLogin.getActionType();
			if(action == ActionTypeUpdateLogin.SUCCESS){
				connected = true;
				System.out.println("Udane logowanie !!!");
			}
			else if(action == ActionTypeUpdateLogin.INVALID_AUTH_TOKEN){
				authenticationFailureBehaviour();
				connected = false;
			}
			else if(action == ActionTypeUpdateLogin.SERVER_FULL){
				serverFullBehaviour();
				connected = false;
			}
			else if(action == ActionTypeUpdateLogin.UNEXPECTED_ERROR){
				unexpectedErrorBehaviour();
				connected = false;
			}
			connectTried = true;
		}
	}
	
	private void authenticationFailureBehaviour(){

	}
	
	private void serverFullBehaviour(){

	}
	
	private void unexpectedErrorBehaviour(){

	}
	
	private void authenticationServerIOExceptionBehaviour(){

	}
	
	private void serverIOExceptionBehaviour(){

	}
	
	private void cancelBehaviour(){
		 clonesClient.stop();
	}

	@Override
	public void connected(Connection connection) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void disconnected(Connection connection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void idle(Connection connection) {
		// TODO Auto-generated method stub
		
	}
	
	protected abstract void test();
	protected abstract void stopTest();

	protected void setNumberOfClients(int numberOfClients) {
		this.numberOfClients = numberOfClients;
	}
	
	
}
