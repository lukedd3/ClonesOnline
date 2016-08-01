package com.luke.clones.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.luke.clones.config.Config;
import com.luke.clones.model.SpecialKeyHandler;
import com.luke.clones.model.TimeMeasure;
import com.luke.clones.network.backstage.ServerFrontendInfo;
import com.luke.clones.network.communication.GetServerBackendInfo;
import com.luke.clones.network.communication.ServerBackendInfo;
import com.luke.clones.network.config.NetworkConfig;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.clones.sound.SoundManager;
import com.luke.clones.sound.SoundType;
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

public class ScreenOnlineServerSelection implements Screen, ListenerService{
	private StartScreen startScreen;
	private ClonesClient clonesClient;
	
	private int width, height;
	private Viewport viewport;
	private volatile Stage stage;
	private Skin skin, smallSkin;
	private Table mainTable;
	private ScrollPane mainPane;
	
	private Label errorLabel;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;
	
	private TimeMeasure timeMeasure;
	
	private Image loadingImage;
	
	public ScreenOnlineServerSelection(StartScreen startScreen){
		this.startScreen = startScreen;
	}
	
	@Override
	public void show() {
		initScreen();
	}
	
	private void initScreen(){
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
		
		errorLabel = new Label("", skin);
		errorLabel.setColor(Color.RED);
		errorLabel.setWidth(width-Config.widthOffset);
		errorLabel.setWrap(true);
		errorLabel.setAlignment(Align.center);
		
		mainTable = new Table(skin);
		mainPane = new ScrollPane(mainTable, skin);
		mainPane.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainPane.setSize(width-Config.widthOffset, height-(Config.menuTopHeightOffset+Config.menuBottomHeightOffset));
		
		mainTable.setPosition(0+Config.widthOffset/2, 0);
		mainTable.setSize(width-Config.widthOffset, height-Config.gameHeightOffset);
		mainTable.setBackground(skin.getDrawable("default-scroll"));
		mainTable.align(Align.top);
		
		stage.addActor(mainPane);
		
		final Label serverSelectionLabel = new Label("Select Server", skin);
		serverSelectionLabel.setAlignment(Align.center|Align.top);
		mainTable.add(serverSelectionLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		Texture loadingTexture = new Texture(Gdx.files.internal("data/images/loading64.png"));
		loadingImage = new Image(loadingTexture);
		loadingImage.setColor(Config.textColor);
		loadingImage.setOrigin(loadingImage.getWidth()/2, loadingImage.getHeight()/2);
		loadingImage.addAction(Actions.forever(Actions.rotateBy(360f,1.7f)));
		
		mainTable.add(loadingImage);
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				        	 
				timeMeasure = new TimeMeasure();
				timeMeasure.start();
				List<ClonesClient> clonesClientList = new ArrayList<ClonesClient>();
				final List<ServerFrontendInfo> availableServers = Collections.synchronizedList(new ArrayList<ServerFrontendInfo>());
				Map<ServerFrontendInfo, ServerBackendInfo> serverInfoBundleMap = new ConcurrentHashMap<ServerFrontendInfo, ServerBackendInfo>();
				for(final ServerFrontendInfo serverFrontendInfo: NetworkConfig.serverList){
					boolean connectionSuccessful;
					ClonesClient tempClonesClient = null;
//					try{
						tempClonesClient = connectToServer(serverFrontendInfo, new ServerBackendInfoListener(serverFrontendInfo, serverInfoBundleMap, availableServers));
						if(tempClonesClient!=null){
							connectionSuccessful=true;
						}
						else{
							connectionSuccessful=false;
						}
//					}
//					catch(IOException e){
//						e.printStackTrace();
//						connectionSuccessful = false;
//					}
					
					if(connectionSuccessful){
						availableServers.add(serverFrontendInfo);
						clonesClientList.add(tempClonesClient);
					}
				}
				
				for(ClonesClient tempClonesClient: clonesClientList){
					tempClonesClient.send(new GetServerBackendInfo());
				}

				Gdx.app.postRunnable(new Runnable() {
					public void run() {
						if (availableServers.size() == 0)
							drawEnd(availableServers);
					}
				});

	        }
		});
			
		t.start();
		
	}
	
	@Override
	public void received(Connection connection, Object obj) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void render(float delta) {
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
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	private ClonesClient connectToServer(ServerFrontendInfo serverFrontendInfo, ListenerService listener){
		try {
			clonesClient = new ClonesClient();
			int port = serverFrontendInfo.getPort();
			String host = serverFrontendInfo.getHost();
//			System.out.println("Client connection try started");
			clonesClient.start(listener, host, port);
//			System.out.println("Client connected");
			return clonesClient;
		} catch (IOException e) {
//			e.printStackTrace();
			System.out.println("Client can't connect");
			return null;
		}
	}
	
	private class ServerBackendInfoListener implements ListenerService {
		ServerFrontendInfo serverFrontendInfo;
		Map<ServerFrontendInfo, ServerBackendInfo> serverInfoBundleMap;
		List<ServerFrontendInfo> availableServers;
		
		public ServerBackendInfoListener(ServerFrontendInfo serverFrontendInfo, Map<ServerFrontendInfo,
				ServerBackendInfo> serverInfoBundleMap, List<ServerFrontendInfo> availableServers){
				this.serverFrontendInfo = serverFrontendInfo;
				this.serverInfoBundleMap = serverInfoBundleMap;
				this.availableServers = availableServers;
		}
		
		@Override
		public void received(Connection connection, Object obj) {
			if(obj instanceof ServerBackendInfo){
				ServerBackendInfo serverBackendInfo = (ServerBackendInfo) obj;
				serverInfoBundleMap.put(serverFrontendInfo, serverBackendInfo);
				timeMeasure.update();
				int timeout = NetworkConfig.defaultConnectTimeout*NetworkConfig.serverList.length
						+NetworkConfig.clientServerSelectionServerBackendInfoAdditionalTimeout;
				if(serverInfoBundleMap.size()==availableServers.size() || timeMeasure.getMeasuredTimeInMs()>timeout){
					if(timeMeasure.getMeasuredTimeInMs()>timeout){
						System.out.println("Got server backend info timeout while checking available servers"
					+timeMeasure.getMeasuredTimeInMs()+">"+timeout);
					}
					Gdx.app.postRunnable(new Runnable() {
						public void run() {
					drawServerList(availableServers, serverInfoBundleMap);
						}});
				}
			}
		}
		
		@Override
		public void idle(Connection connection) {	
		}
		@Override
		public void disconnected(Connection connection) {
		}
		@Override
		public void connected(Connection connection) {
			
		}
	}
	
	public void drawServerList(List<ServerFrontendInfo> availableServers, Map<ServerFrontendInfo, ServerBackendInfo> serverInfoBundleMap){
		int i=1;
		for(final ServerFrontendInfo serverFrontendInfo: availableServers){
			final ServerBackendInfo serverBackendInfo = serverInfoBundleMap.get(serverFrontendInfo);
			TextButton serverButton = new TextButton("Server "+i+" - "+serverFrontendInfo.getName()+" | "
					+serverBackendInfo.getCurrentNumberOfPlayers()+"/"
					+serverBackendInfo.getMaxNumberOfPlayers()+" users", skin);
			
			mainTable.add(serverButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
			mainTable.row();
			serverButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					SoundManager.INSTANCE.play(SoundType.CLICK);
					if(serverBackendInfo.getCurrentNumberOfPlayers()>=serverBackendInfo.getMaxNumberOfPlayers()){
						errorLabel.setText("Server is currently full - maximum number of players was reached. Try again later.");
					}
					else{
						startScreen.setToOnlineLogin(serverFrontendInfo);
					}
				}
			});
			i++;
		}
		
		drawEnd(availableServers);
	}
	
	public void drawEnd(List<ServerFrontendInfo> availableServers){
		loadingImage.remove();
		
		if(availableServers.size()==0){
			errorLabel.setText("Failed to find any server. Check if you have live internet connection and reflesh list.");
		}
		
		mainTable.add(errorLabel).width(width-Config.widthOffset-6);
		
		mainTable.row();
		
		TextButton refleshButton = new TextButton("Reflesh list", skin);
		mainTable.add(refleshButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		refleshButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				 Gdx.app.postRunnable(new Runnable() {
			         public void run() {
			        	 startScreen.setToOnlineServerSelection();
			         }});
			}
		});
		
		TextButton backButton = new TextButton("Back to menu", skin);
		mainTable.add(backButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				 Gdx.app.postRunnable(new Runnable() {
			         public void run() {
			        	 startScreen.setToMainMenu();
			         }});
			}
		});
	}

}
