package com.luke.clones.screen;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.luke.clones.android.AndroidAuthenticationTokenHandler;
import com.luke.clones.android.AndroidPickAccountHandler;
import com.luke.clones.config.Config;
import com.luke.clones.model.SpecialKeyHandler;
import com.luke.clones.model.button.RoomBar;
import com.luke.clones.network.backstage.ServerFrontendInfo;
import com.luke.clones.network.communication.ActionTypeUpdateLogin;
import com.luke.clones.network.communication.MoveLogin;
import com.luke.clones.network.communication.UpdateLogin;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.clones.sound.SoundManager;
import com.luke.clones.sound.SoundType;
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

public class ScreenOnlineLogin implements Screen, ListenerService{
	//server selected on the select screen
	private ServerFrontendInfo serverInfo;
	
	private StartScreen startScreen;
	private ClonesClient clonesClient;
	private String playerName;
	
	private int width, height;
	private Viewport viewport;
	private volatile Stage stage;
	private Skin skin, smallSkin;
	private Table mainTable;
	private ArrayList<RoomBar> roomBarList;
	
	private Image loadingImage;
	private Label errorLabel;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;
	
	private AndroidPickAccountHandler androidPickAccountHandler;
	private AndroidAuthenticationTokenHandler androidAuthenticationTokenHandler;
	
	public ScreenOnlineLogin(StartScreen startScreen){
		this.startScreen = startScreen;
	}
	
	@Override
	public synchronized void show() {
		width = Config.width;
		height = Config.height;
//		stage = new Stage(width,height,false);
		viewport = new FitViewport(width, height);
		stage = new Stage();
		stage.setViewport(viewport);
		
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
				//cancel button action
				clonesClient.stop();
				
				 Gdx.app.postRunnable(new Runnable() {
			         public void run() {
			        	 startScreen.setToMainMenu();
			         }});
			}
		};
		inputMultiplexer = new InputMultiplexer(stage, specialKeyHandler);
		
		Gdx.input.setInputProcessor(inputMultiplexer);

		
		skin = new Skin(Gdx.files.internal("data/skins/skin_medium.json"));
		smallSkin = new Skin(Gdx.files.internal("data/skins/skin_small.json"));
		
		mainTable = new Table(skin);
		mainTable.setPosition(0+Config.widthOffset/2, 0);
		mainTable.setSize(width-Config.widthOffset/2, height-Config.gameHeightOffset);
		mainTable.setBackground(skin.getDrawable("default-scroll"));
		mainTable.align(Align.top);
		
		stage.addActor(mainTable);
		
		Texture loadingTexture = new Texture(Gdx.files.internal("data/images/loading64.png"));
		loadingImage = new Image(loadingTexture);
		loadingImage.setColor(Config.textColor);
		loadingImage.setOrigin(loadingImage.getWidth()/2, loadingImage.getHeight()/2);
		loadingImage.addAction(Actions.forever(Actions.rotateBy(360f,1.7f)));
		
		mainTable.add(loadingImage).spaceBottom(18).spaceTop(18);
		
		mainTable.row();
		
		TextButton cancelButton = new TextButton("Cancel", skin);
		mainTable.add(cancelButton).minSize(width*3/4-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		cancelButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				 Gdx.app.postRunnable(new Runnable() {
			         public void run() {
			        	 startScreen.setToMainMenu();
			         }});
			}
		});
		
		errorLabel = new Label("", skin);
		errorLabel.setColor(Color.RED);
		errorLabel.setWidth(width);
		errorLabel.setWrap(true);
		errorLabel.setAlignment(Align.center);
		
		login();
		
	}
	
	@Override
	public synchronized void hide() {
		stage.dispose();
	}
	
	@Override
	public synchronized void render(float delta) {
		Gdx.gl.glClearColor(54f/255f, 47f/255f, 45f/255f, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
	}
	
	
	
	@Override
	public void resize(int width, int height) {
		if(viewport!=null) viewport.update(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void connected(Connection connection) {
		
	}

	@Override
	public synchronized void received(Connection connection, Object obj) {
		//if login ok -> goto screenOnline
		//if login not ok -> try again, give info what is the problem
		if(obj instanceof UpdateLogin){
			UpdateLogin updateLogin = (UpdateLogin) obj;
			ActionTypeUpdateLogin action = updateLogin.getActionType();
			if(action == ActionTypeUpdateLogin.SUCCESS){
				errorLabel.setText("");
				 Gdx.app.postRunnable(new Runnable() {
			         public void run() {
			        	 startScreen.setToOnline(clonesClient, playerName);
			         }});
				System.out.println("Udane logowanie !!!");
			}
			else if(action == ActionTypeUpdateLogin.INVALID_AUTH_TOKEN){
				authenticationFailureBehaviour();
			}
			else if(action == ActionTypeUpdateLogin.SERVER_FULL){
				serverFullBehaviour();
			}
			else if(action == ActionTypeUpdateLogin.UNEXPECTED_ERROR){
				unexpectedErrorBehaviour();
			}
		}
	}


	@Override
	public void disconnected(Connection connection) {
		
	}

	@Override
	public void idle(Connection connection) {
		
	}
	
	public void setServerInfo(ServerFrontendInfo serverInfo){
		this.serverInfo = serverInfo;
	}
	
	private void cancelBehaviour(){
		 clonesClient.stop();
		 Gdx.app.postRunnable(new Runnable() {
			 public void run() {
				 startScreen.setToMainMenu();
	     }});
	}
	
	private void authenticationFailureBehaviour(){
		errorLabel.setText("Authentication failed. Try again.");
		addTryAgainButton();
	}
	
	private void serverFullBehaviour(){
		errorLabel.setText("Server is currently full - maximum number of players was reached. Try again later.");
		addTryAgainButton();
	}
	
	private void unexpectedErrorBehaviour(){
		errorLabel.setText("Unexpected error. Try again.");
		addTryAgainButton();
	}
	
	private void authenticationServerIOExceptionBehaviour(){
		errorLabel.setText("Failed to connect to the authentication server. Check if you have live internet connection.");
		addTryAgainButton();
	}

	private void serverIOExceptionBehaviour(){
		errorLabel.setText("Failed to connect to the server. Check if you have live internet connection.");
		addTryAgainButton();
	}
	
	private void addTryAgainButton(){
		mainTable.clear();
		TextButton tryAgainButton = new TextButton("Try again", skin);
		mainTable.add(tryAgainButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		tryAgainButton.addListener(new LoginClickListener());
		
		TextButton cancelButton = new TextButton("Cancel", skin);
		mainTable.add(cancelButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		cancelButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				cancelBehaviour();
			}
		});
		
		mainTable.row();
		
		mainTable.add(errorLabel).width(width);
	}
	
	private class LoginClickListener extends ClickListener {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			SoundManager.INSTANCE.play(SoundType.CLICK);
			login();
		}
	}
	
	private void login(){
		boolean isConnectedToServer = connectToServer();
		
		androidPickAccountHandler=new AndroidPickAccountHandler() {
			@Override
			public void onCancelOrError() {
				System.out.println("onCancelOrError");
				cancelBehaviour();
			}
			@Override
			public void onAccountPicked(String accountName) {
				//idzie têdy
				System.out.println("onAccountPicked");
				startScreen.getAndroidNativeHandler().getGoogleAccountToken(accountName, androidAuthenticationTokenHandler);
			}
		};
		
		androidAuthenticationTokenHandler=new AndroidAuthenticationTokenHandler() {
			@Override
			public void onSuccess(String token, String username) {
				System.out.println("onSuccess");
//				System.out.println(token);
				playerName = NameUtil.getNameFromEmail(username);
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
				//idzie têdy
				System.out.println("onAuthException");
				authenticationFailureBehaviour();
			}
		};
		
		if(isConnectedToServer){
			startScreen.getAndroidNativeHandler().pickGoogleAccount(androidPickAccountHandler);
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
	
}
