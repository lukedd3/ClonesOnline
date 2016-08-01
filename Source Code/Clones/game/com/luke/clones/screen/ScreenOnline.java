package com.luke.clones.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.luke.clones.ads.InterstitialAdsManager;
import com.luke.clones.chart.TableChart;
import com.luke.clones.chart.TableChartCreator;
import com.luke.clones.config.Config;
import com.luke.clones.map.model.MapModel;
import com.luke.clones.map.model.MapModelUtils;
import com.luke.clones.messages.QuickMessage;
import com.luke.clones.messages.QuickMessageManager;
import com.luke.clones.messages.QuickMessagePresets;
import com.luke.clones.messages.QuickMessageShowable;
import com.luke.clones.messages.QuickMessageUtil;
import com.luke.clones.model.SpecialKeyHandler;
import com.luke.clones.model.button.CustomDialog;
import com.luke.clones.model.button.FlashingTextButton;
import com.luke.clones.model.type.PlayerMoveTimeLimitType;
import com.luke.clones.network.communication.AchievementGenreType;
import com.luke.clones.network.communication.AchievementType;
import com.luke.clones.network.communication.AchievementsRequest;
import com.luke.clones.network.communication.AchievementsResponse;
import com.luke.clones.network.communication.ActionTypeMoveRoom;
import com.luke.clones.network.communication.ActionTypeStats;
import com.luke.clones.network.communication.ActionTypeUpdateRoom;
import com.luke.clones.network.communication.GameStart;
import com.luke.clones.network.communication.MoveRoom;
import com.luke.clones.network.communication.PlayerMoveTimeLimit;
import com.luke.clones.network.communication.RoomCreateInfo;
import com.luke.clones.network.communication.RoomInfo;
import com.luke.clones.network.communication.RoomModifyInfo;
import com.luke.clones.network.communication.StatsRequest;
import com.luke.clones.network.communication.StatsResponse;
import com.luke.clones.network.communication.StatsType;
import com.luke.clones.network.communication.UpdateRoom;
import com.luke.clones.network.config.NetworkConfig;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.clones.sound.SoundManager;
import com.luke.clones.sound.SoundType;
import com.luke.clones.util.ClientStatsUtil;
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

public class ScreenOnline implements Screen, ListenerService, QuickMessageShowable{
	private StartScreen startScreen;
	private ClonesClient clonesClient;
	private String playerName;
	
	private int width, height;
	private Viewport mainViewport, layerViewport;
	private volatile Stage mainStage, layerStage;
	private volatile Skin skin, smallSkin;
	private volatile Table mainTable;
	private volatile ScrollPane mainPane;
	private volatile Table roomTable;
//	private ArrayList<RoomBar> roomBarList;
	
	RoomInfo[] roomInfoList;
	int sortColumn;
	
	//if true - switches to room interior after setting as screen
	//if false - switches to room list after setting as screen
	boolean switchToRoomInterior = false;
	
	boolean creatingRoom = false;
	
	CustomDialog errorDialogNameTooShort, errorDialogNameAlreadyTaken;
	
	//back button textures
	Texture[] up, down;
	
	InputMultiplexer inputMultiplexer;
	SpecialKeyHandler specialKeyHandler;
	
	private TableChartCreator tableChartCreator;
	
	private Texture flashingElementTexture;
	
	private InterstitialAdsManager interstitialAdsManager = InterstitialAdsManager.INSTANCE;
	
	public ScreenOnline(StartScreen startScreen){
		this.startScreen = startScreen;
	}
	
	public void initializeWithoutName(ClonesClient clonesClient, boolean switchToRoomInterior){
		this.clonesClient = clonesClient;
		clonesClient.setListener(this);
		this.switchToRoomInterior=switchToRoomInterior;
	}
	
	public void initialize(ClonesClient clonesClient, String playerName){
		this.clonesClient = clonesClient;
		clonesClient.setListener(this);
		this.playerName = playerName;
	}
	
	@Override
	public synchronized void show() {
		width = Config.width;
		height = Config.height;
//		mainStage = new Stage(width,height,false);
//		layerStage = new Stage(width,height,false);
		
		mainViewport = new FitViewport(width, height);
		mainStage = new Stage();
		mainStage.setViewport(mainViewport);
		
		layerViewport = new FitViewport(width, height);
		layerStage = new Stage();
		layerStage.setViewport(layerViewport);
		
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
				
			}
		};
		inputMultiplexer = new InputMultiplexer(mainStage, specialKeyHandler);
		
		Gdx.input.setInputProcessor(inputMultiplexer);

		
		skin = new Skin(Gdx.files.internal("data/skins/skin_medium.json"));
		smallSkin = new Skin(Gdx.files.internal("data/skins/skin_small.json"));
		
		flashingElementTexture = new Texture(Gdx.files.internal("data/models/select_button.png"));
		
		mainTable = new Table(skin);
		mainTable.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainTable.setSize(width-Config.widthOffset/2, height-(Config.menuBottomHeightOffset+Config.menuTopHeightOffset));
		mainTable.setBackground(skin.getDrawable("default-scroll"));
		mainTable.align(Align.top);
		
		mainPane = new ScrollPane(mainTable, skin);
		mainPane.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainPane.setSize(width-Config.widthOffset/2, height-(Config.menuBottomHeightOffset+Config.menuTopHeightOffset));
		
		mainStage.addActor(mainPane);
		
		up = new Texture[1];
		down = new Texture[1];

		up[0] = new Texture(Gdx.files.internal("data/buttons/turn.png"));
		up[0].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		down[0] = new Texture(Gdx.files.internal("data/buttons/turnc.png"));
		down[0].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		tableChartCreator = new TableChartCreator();
		tableChartCreator.setTextures(new Texture(Gdx.files.internal("data/images/table_chart_bars_part1.png")), 
				new Texture(Gdx.files.internal("data/images/table_chart_bars_part2.png")), 
				new Texture(Gdx.files.internal("data/images/table_chart_bars_part3.png")));
		int chartWidth = 1024;
		if(chartWidth > width-6){
			chartWidth = width-6;
		}
		int chartHeight = 32;
		tableChartCreator.setChartSize(chartWidth, chartHeight);
		
		buildRoomListUpperGUI();
		
		if(switchToRoomInterior){
			clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_INTERIOR, null, null));
			switchToRoomInterior=false;
		}
		else{
			clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_LIST, null, null));
		}
	}
	
	@Override
	public void hide() {
		mainStage.dispose();
	}
	
	@Override
	public synchronized void render(float delta) {
		Gdx.gl.glClearColor(54f/255f, 47f/255f, 45f/255f, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mainStage.act();
		mainStage.draw();
		
		layerStage.act();
		layerStage.draw();
	}
	
	private synchronized void buildRoomListUpperGUI(){
		startScreen.getAndroidNativeHandler().hideSystemButtonBar();
		
		inputMultiplexer.removeProcessor(specialKeyHandler);
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
				clonesClient.stop();
				 Gdx.app.postRunnable(new Runnable() {
			         public void run() {
			        	 startScreen.setToMainMenu();
			         }});
			}
		};
		inputMultiplexer.addProcessor(specialKeyHandler);
		
		creatingRoom=false;
		
		mainTable.clear();
		
		TextButton backButton = new TextButton("Back to menu", skin);
		mainTable.add(backButton).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
		mainTable.row();
		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				clonesClient.stop();
				 Gdx.app.postRunnable(new Runnable() {
			         public void run() {
			        	 startScreen.setToMainMenu();
			     }});
			}
		});
		
		TextButton statsButton = new TextButton("Statistics", skin);
		mainTable.add(statsButton).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
		mainTable.row();
		statsButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
//				try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				SoundManager.INSTANCE.play(SoundType.CLICK);
				clonesClient.send(new StatsRequest(ActionTypeStats.GET_GENERAL_STATS));
			}
		});
		
		TextButton achievementsButton = new TextButton("Achievements", skin);
		mainTable.add(achievementsButton).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
		mainTable.row();
		achievementsButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				clonesClient.send(new AchievementsRequest());
			}
		});
		
		TextButton createRoomButton = new TextButton("Create room", skin);
		mainTable.add(createRoomButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		createRoomButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				buildRoomCreate();
			}
		});
		
		Button sortTitleDummyButtonLeft = new Button(skin);
		sortTitleDummyButtonLeft.setDisabled(true);
		Button sortTitleDummyButtonRight = new Button(skin);
		sortTitleDummyButtonRight.setDisabled(true);
		TextButton sortTitleButton = new TextButton("Sort Method", skin);
		sortTitleButton.setDisabled(true);
		Table sortTitleBar = new Table(skin);
		sortTitleBar.add(sortTitleDummyButtonLeft).minSize(width/5-3, 33).spaceTop(3).top();
		sortTitleBar.add(sortTitleButton).minSize(width*3/5, 35).spaceTop(3);
		sortTitleBar.add(sortTitleDummyButtonRight).minSize(width/5-3, 33).spaceTop(3).top();
		mainTable.add(sortTitleBar).minSize(width-6, 35).spaceTop(3);
		mainTable.row();
		
		final List<String> selectBoxItems = new ArrayList<String>();
		selectBoxItems.add("Free Places");
		selectBoxItems.add("Room Name");
		selectBoxItems.add("Admin Name");
		
		final ListIterator<String> selectBoxItemsIterator = selectBoxItems.listIterator();
		
//		sortRoomInfoList(roomInfoList, selectBoxItemsIterator.nextIndex());
		sortColumn = selectBoxItemsIterator.nextIndex();
		final TextButton sortButton = new TextButton(selectBoxItemsIterator.next(), skin);
		sortButton.setDisabled(true);
		TextButton sortLeftButton = new TextButton("<", skin);
		sortLeftButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				if(selectBoxItemsIterator.hasPrevious()&&selectBoxItemsIterator.previousIndex()==selectBoxItems.size()-1) selectBoxItemsIterator.previous();
//				if(selectBoxItemsIterator.hasPrevious())sortRoomInfoList(roomInfoList, selectBoxItemsIterator.previousIndex());
				if(selectBoxItemsIterator.hasPrevious()){
					sortColumn = selectBoxItemsIterator.previousIndex();
					buildRoomList(roomInfoList, sortColumn);
				}
				if(selectBoxItemsIterator.hasPrevious()) sortButton.setText(selectBoxItemsIterator.previous());
			}
		});
		TextButton sortRightButton = new TextButton(">", skin);
		sortRightButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				if(selectBoxItemsIterator.hasNext()&&selectBoxItemsIterator.nextIndex()==0) selectBoxItemsIterator.next();
				if(selectBoxItemsIterator.hasNext())sortRoomInfoList(roomInfoList, selectBoxItemsIterator.nextIndex());
				if(selectBoxItemsIterator.hasNext()){
					sortColumn = selectBoxItemsIterator.nextIndex();
					buildRoomList(roomInfoList, sortColumn);
				}
				if(selectBoxItemsIterator.hasNext()) sortButton.setText(selectBoxItemsIterator.next());
			}
		});
		
		Table sortBar = new Table(skin);
		sortBar.add(sortLeftButton).minSize(width/5-6, 32).spaceBottom(3).spaceTop(3);
		sortBar.add(sortButton).minSize(width*3/5, 35).spaceBottom(3).spaceLeft(3).spaceRight(3);
		sortBar.add(sortRightButton).minSize(width/5-6, 32).spaceBottom(3).spaceTop(3);
		mainTable.add(sortBar).minSize(width-6, 35).spaceBottom(3);
		mainTable.row();
		
//		TextButton searchIconButton = new TextButton("S", skin);
//		
//		TextField searchField = new TextField("", skin);
//		
//		Table searchBar = new Table(skin);
//		searchBar.add(searchField).minSize(width*4/5-3, 50).spaceBottom(3).spaceTop(3);
//		searchBar.add(searchIconButton).minSize(width/5-3, 50).spaceBottom(3).spaceTop(3);
//		mainTable.add(searchBar).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
//		mainTable.row();
		
		roomTable = new Table(skin);
		mainTable.add(roomTable).minSize(width-6, 70);
		mainTable.row();
		
		buildRoomList(roomInfoList, sortColumn);
		
//		uiDrawLock=false;
	}
	
	private synchronized void buildRoomList(RoomInfo[] roomInfoList, int sortColumn){
		
		//don't add room list if there is no roomInfoList
		if(roomInfoList==null){
			roomTable.clear();
			return;
		}
		
		sortRoomInfoList(roomInfoList, sortColumn);
		
		roomTable.clear();
		
		for(final RoomInfo roomInfo: roomInfoList){
//			String playerString = "";
//			for(String playerName: roomInfo.getPlayerNames()){
//				playerString += playerName + ", ";
//			}
			
			Button roomButton = new Button(skin);
			roomButton.pad(0);
			
			Label roomNameLabel = new Label(roomInfo.getRoomName(), skin);
			roomNameLabel.setAlignment(Align.center);
			
			Label numberOfPlayersLabel = new Label(roomInfo.getNumberOfPlayers()+"/"+roomInfo.getPlayerLimit(), skin);
			numberOfPlayersLabel.setAlignment(Align.center);
			
//			Label playerNamesLabel = new Label(playerString, skin);
//			playerNamesLabel.setAlignment(Align.center);
			
			Label mapNameLabel = new Label("Map: "+roomInfo.getMapName(), skin);
			numberOfPlayersLabel.setAlignment(Align.center);
			
			roomButton.add(roomNameLabel).minSize(width*(3f/4f)-3, 35).spaceLeft(0).spaceRight(0);
			roomButton.add(numberOfPlayersLabel).minSize(width/4-3, 35).spaceLeft(0).spaceRight(0);
			roomButton.row();
//			roomButton.add(playerNamesLabel).minSize(width-6, 35).colspan(2);
			roomButton.add(mapNameLabel).minHeight(35).colspan(2);
			
//			TextButton roomButton = new TextButton(roomInfo.getRoomName()+" | "+playerString, skin);
			roomTable.add(roomButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
			roomTable.row();
			roomButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					SoundManager.INSTANCE.play(SoundType.CLICK);
					clonesClient.send(new MoveRoom(ActionTypeMoveRoom.ENTER, null, roomInfo.getRoomName()));
				}
			});
		}
	}
	
	private synchronized void sortRoomInfoList(RoomInfo[] roomInfoList, final int sortColumn){
		if(roomInfoList==null) return;
		Arrays.sort(roomInfoList, new Comparator<RoomInfo>() {
			@Override
			public int compare(RoomInfo arg0, RoomInfo arg1) {
				switch(sortColumn){
				case 0:
					int freePlaces0 = arg0.getPlayerLimit()-arg0.getNumberOfPlayers();
					int freePlaces1 = arg1.getPlayerLimit()-arg1.getNumberOfPlayers();
					if(freePlaces0<freePlaces1) return 1;
					else if(freePlaces0>freePlaces1) return -1;
					else return 0;
				case 1:
					return arg0.getRoomName().compareTo(arg1.getRoomName());
				case 2:
					return arg0.getAdminName().compareTo(arg1.getAdminName());
				default:
					return 0;
				}
				
			}
		});
	}
	
	private synchronized void buildStatistics(long level, long percentToNextLevel,
			Map<StatsType, Integer> statsPercentMap, Map<StatsType, Long> statsMap){
		startScreen.getAndroidNativeHandler().hideSystemButtonBar();
		
		inputMultiplexer.removeProcessor(specialKeyHandler);
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
				clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_LIST, null, null));
				buildRoomListUpperGUI();
			}
		};
		inputMultiplexer.addProcessor(specialKeyHandler);
		
		mainTable.clear();
		
		TextButton cancelButton = new TextButton("Back to list", skin);
		cancelButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_LIST, null, null));
				buildRoomListUpperGUI();
			}
		});
		mainTable.add(cancelButton).minSize(width*3/4-6, 70).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		Label playerLevelLabel = new Label("Level "+level, skin);
		playerLevelLabel.setAlignment(Align.center);
		mainTable.add(playerLevelLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		TableChart percentToNextLevelChart = tableChartCreator.createWithPercentScale((int)(100-percentToNextLevel), (int)percentToNextLevel, 0);
		mainTable.add(percentToNextLevelChart);
		mainTable.row();
		
		Label percentToNextLevelLabel = new Label(percentToNextLevel+"% to next level",skin);
		percentToNextLevelLabel.setAlignment(Align.center);
		mainTable.add(percentToNextLevelLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		Label played2PgamesLabel = new Label("Played 2 player games", skin);
		played2PgamesLabel.setAlignment(Align.center);
		mainTable.add(played2PgamesLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		TableChart played2PGamesChart = tableChartCreator.createWithPercentScale(statsPercentMap.get(StatsType.WON_2P_GAMES),
				statsPercentMap.get(StatsType.LOST_2P_GAMES), 0);
		mainTable.add(played2PGamesChart);
		mainTable.row();
		
		Label played2PGamesPercentLabel = new Label(statsPercentMap.get(StatsType.WON_2P_GAMES)+"% won | "
				+statsPercentMap.get(StatsType.LOST_2P_GAMES)+"% lost", skin);
		played2PGamesPercentLabel.setAlignment(Align.center);
		mainTable.add(played2PGamesPercentLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
//		Label played2PGamesNumberLabel = new Label(statsMap.get(StatsType.WON_2P_GAMES)+" won | "
//				+statsMap.get(StatsType.LOST_2P_GAMES)+" lost",skin);
//		played2PGamesNumberLabel.setAlignment(Align.center);
//		mainTable.add(played2PGamesNumberLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
//		mainTable.row();
		
		Label played4PgamesLabel = new Label("Played 4 player games", skin);
		played4PgamesLabel.setAlignment(Align.center);
		mainTable.add(played4PgamesLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		TableChart played4PGamesChart = tableChartCreator.createWithPercentScale(statsPercentMap.get(StatsType.WON_4P_GAMES),
				statsPercentMap.get(StatsType.LOST_4P_GAMES), 0);
		mainTable.add(played4PGamesChart);
		mainTable.row();
		
		Label played4PGamesPercentLabel = new Label(statsPercentMap.get(StatsType.WON_4P_GAMES)+"% won | "
				+statsPercentMap.get(StatsType.LOST_4P_GAMES)+"% lost", skin);
		played4PGamesPercentLabel.setAlignment(Align.center);
		mainTable.add(played4PGamesPercentLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		Label wonInRowLabel = new Label("Won in row", skin);
		wonInRowLabel.setAlignment(Align.center);
		mainTable.add(wonInRowLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		TableChart wonInRowChart = tableChartCreator.createWithPercentScale(statsPercentMap.get(StatsType.LAST_WON_IN_ROW),
				statsPercentMap.get(StatsType.BIGGEST_WON_IN_ROW), 0);
		mainTable.add(wonInRowChart);
		mainTable.row();
		
		Label wonInRowPercentLabel = new Label("Last "+statsMap.get(StatsType.LAST_WON_IN_ROW)+" | Biggest "
				+statsMap.get(StatsType.BIGGEST_WON_IN_ROW), skin);
		wonInRowPercentLabel.setAlignment(Align.center);
		mainTable.add(wonInRowPercentLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		Label totalPlayTimeTitleLabel = new Label("Total play time", skin);
		totalPlayTimeTitleLabel.setAlignment(Align.center);
		mainTable.add(totalPlayTimeTitleLabel).minSize(width-6, 50).spaceBottom(3).spaceTop(23);
		mainTable.row();
	      
	    long totalPlayTimeMs = statsMap.get(StatsType.TOTAL_PLAY_TIME)*1000;
	    
	    String totalPlayTimeFormatted = TimeUnit.MILLISECONDS.toHours(totalPlayTimeMs)
	    		+ " hour(s) "
	    		+ (TimeUnit.MILLISECONDS.toMinutes(totalPlayTimeMs) -  TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalPlayTimeMs)))
	    		+ " minute(s) "
	    		+ (TimeUnit.MILLISECONDS.toSeconds(totalPlayTimeMs) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalPlayTimeMs)))
	    		+ " second(s) "; 
		
		Label totalPlayTimeValueLabel = new Label(totalPlayTimeFormatted, skin);
		totalPlayTimeValueLabel.setAlignment(Align.center);
		mainTable.add(totalPlayTimeValueLabel);
		
//		for(Entry<StatsType, Long> stat: statsMap.entrySet()){
//			TextButton played2pGamesLabel = new TextButton(stat.getKey().getDescription(), skin);
//			played2pGamesLabel.setDisabled(true);
//			mainTable.add(played2pGamesLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
//			
//			mainTable.row();
//			
//			final TextField played2pGamesValueTextField = new TextField(stat.getValue().toString(), skin);
//			played2pGamesValueTextField.setDisabled(true);
//			mainTable.add(played2pGamesValueTextField).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
//			
//			mainTable.row();
//		}
		

	}
	
	private synchronized void buildAchievements(final List<AchievementType> achievementTypes){
		startScreen.getAndroidNativeHandler().hideSystemButtonBar();
		System.out.println("achievementTypes: "+achievementTypes);
   	 
		inputMultiplexer.removeProcessor(specialKeyHandler);
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
				clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_LIST, null, null));
				buildRoomListUpperGUI();
			}
		};
		inputMultiplexer.addProcessor(specialKeyHandler);
	 
   	 Collections.sort(achievementTypes, new Comparator<AchievementType>() {
    		
    		@Override
    		public int compare(AchievementType a0, AchievementType a1) {
    			if(achievementGenreTypeToWeight(a0.getAchievementGenreType())>achievementGenreTypeToWeight(a1.getAchievementGenreType())){
    				return 1;
    			}
    			else if(achievementGenreTypeToWeight(a0.getAchievementGenreType())<achievementGenreTypeToWeight(a1.getAchievementGenreType())){
    				return -1;
    			}
    			return 0;
    		}
    		 
    		 private int achievementGenreTypeToWeight(AchievementGenreType achievementGenreType){
    			 if(achievementGenreType==AchievementGenreType.PLAY_TIME){
    				 return 0;
    			 }
    			 else if(achievementGenreType==AchievementGenreType.GAMES_PLAYED){
    				 return 1;
    			 }
    			 else if(achievementGenreType==AchievementGenreType.GAMES_WON){
    				 return 2;
    			 }
    			 else if(achievementGenreType==AchievementGenreType.GAMES_WON_IN_ROW){
    				 return 3;
    			 }
    			 else{
    				 return 4;
    			 }
    		 }
 		});
		
		Gdx.app.postRunnable(new Runnable() {
	         public void run() {
			    int imageSize = 88;
			        	 
				mainTable.clear();
				mainTable.align(Align.top);
				
				TextButton cancelButton = new TextButton("Back to list", skin);
				cancelButton.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						SoundManager.INSTANCE.play(SoundType.CLICK);
						clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_LIST, null, null));
						buildRoomListUpperGUI();
					}
				});
				mainTable.add(cancelButton).minSize(width*3/4-6, 70).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				if(achievementTypes.size()>0){
					for(AchievementType achievementType: achievementTypes){
						Table twoColumnTable = new Table();
						float twoColumnTableWidth = mainTable.getWidth()-6;
						twoColumnTable.setWidth(twoColumnTableWidth);
						Table leftColumn = new Table();
						leftColumn.setWidth(imageSize);
						Table rightColumn = new Table();
						rightColumn.setWidth(twoColumnTable.getWidth()-imageSize);
						
						leftColumn.add(prepareImage(achievementType)).size(imageSize, imageSize).left().top();
				
						Label titleLabel = new Label(achievementType.getTitle(), skin);
						titleLabel.setWrap(true);
						titleLabel.setAlignment(Align.center);
						rightColumn.add(titleLabel).minWidth(twoColumnTable.getWidth()-imageSize);
						
						rightColumn.row();
						
						Label descriptionLabel = new Label("["+achievementType.getDescription()+"]", smallSkin);
						descriptionLabel.setWrap(true);
						descriptionLabel.setAlignment(Align.center);
						rightColumn.add(descriptionLabel).minWidth(twoColumnTable.getWidth()-imageSize);
						
						twoColumnTable.add(leftColumn);
						twoColumnTable.add(rightColumn);
						mainTable.add(twoColumnTable).minWidth(twoColumnTableWidth);
						mainTable.row();
					}
				}
				else{
					String texturePath = "data/images/blank_achievements.png";
					Texture imageTexture = new Texture(Gdx.files.internal(texturePath));
					imageTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
					Image image = new Image(imageTexture);
					mainTable.add(image);
				}
		
	         }});
	}
	
	private Image prepareImage(AchievementType achievementType){
		String texturePath = "data/messages/achievement_won_games.png";
		if(achievementType.getAchievementGenreType()==AchievementGenreType.GAMES_WON){
			texturePath = "data/messages/achievement_won_games.png";
		}
		else if(achievementType.getAchievementGenreType()==AchievementGenreType.GAMES_PLAYED){
			texturePath = "data/messages/achievement_played_games.png";
		}
		else if(achievementType.getAchievementGenreType()==AchievementGenreType.PLAY_TIME){
			texturePath = "data/messages/achievement_time.png";
		}
		else if(achievementType.getAchievementGenreType()==AchievementGenreType.GAMES_WON_IN_ROW){
			texturePath = "data/messages/achievement_won_in_row.png";
		}
		
		Texture imageTexture = new Texture(Gdx.files.internal(texturePath));
		imageTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Image image = new Image(imageTexture);
		image.setColor(Config.textColor);
		
		return image;
	}
	
	private synchronized void buildRoomCreate(){
		startScreen.getAndroidNativeHandler().hideSystemButtonBar();
//		uiDrawLock=true;
		
		inputMultiplexer.removeProcessor(specialKeyHandler);
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
				creatingRoom=false;
				clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_LIST, null, null));
				buildRoomListUpperGUI();
			}
		};
		inputMultiplexer.addProcessor(specialKeyHandler);
		
		creatingRoom = true;
		
		//clearing main table
		mainTable.clear();
		
				//Room Name Label
				TextButton roomNameLabel = new TextButton("Room Name", skin);
				roomNameLabel.setDisabled(true);
				mainTable.add(roomNameLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				//Room Name TextField
				final TextField roomNameTextField = new TextField(playerName+"'s room", skin);
				roomNameTextField.setMaxLength(25);
				mainTable.add(roomNameTextField).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
		
				//Players Number Label
				TextButton playersNumLabel = new TextButton("Players Number", skin);
				playersNumLabel.setDisabled(true);
				mainTable.add(playersNumLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				//Players Number SelectBox
//				final SelectBox playersNumSelectBox = new SelectBox(new Integer[]{2,4}, skin);
//				playersNumSelectBox.setSelection(0);
				final SelectBox<Integer> playersNumSelectBox = new SelectBox<Integer>(skin);
				playersNumSelectBox.setItems(new Integer[]{2,4});
				playersNumSelectBox.setSelectedIndex(0);
				mainTable.add(playersNumSelectBox).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				TextButton playerMoveTimeLimitLabel = new TextButton("Player move time limit", skin);
				playerMoveTimeLimitLabel.setDisabled(true);
				mainTable.add(playerMoveTimeLimitLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				final PlayerMoveTimeLimitType[] playerMoveTimeLimitSelectBoxItems = new PlayerMoveTimeLimitType[]{
						PlayerMoveTimeLimitType.T5S,
						PlayerMoveTimeLimitType.T10S,
						PlayerMoveTimeLimitType.T15S,
						PlayerMoveTimeLimitType.T30S,
						PlayerMoveTimeLimitType.T45S,
						PlayerMoveTimeLimitType.T60S,
						PlayerMoveTimeLimitType.NO_LIMIT};
				
//				final SelectBox playerMoveTimeLimitSelectBox = new SelectBox(playerMoveTimeLimitSelectBoxItems, skin);
//				playerMoveTimeLimitSelectBox.setSelection(2);
				final SelectBox<PlayerMoveTimeLimitType> playerMoveTimeLimitSelectBox = new SelectBox<PlayerMoveTimeLimitType>(skin);
				playerMoveTimeLimitSelectBox.setItems(playerMoveTimeLimitSelectBoxItems);
				playerMoveTimeLimitSelectBox.setSelectedIndex(2);
				mainTable.add(playerMoveTimeLimitSelectBox).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				MapModel[] twoPlayerMaps = MapModelUtils.selectMapsWithSpecifiedNumberOfPlayers(2);
				MapModel[] fourPlayerMaps = MapModelUtils.selectMapsWithSpecifiedNumberOfPlayers(4);
				final String[] twoPlayerMapNames = MapModelUtils.resolveMapsToNames(twoPlayerMaps);
				final String[] fourPlayerMapNames = MapModelUtils.resolveMapsToNames(fourPlayerMaps);

				//Map Size Label
				TextButton mapLabel = new TextButton("Map", skin);
				mapLabel.setDisabled(true);
				mainTable.add(mapLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				//Map Size SelectBox
//				final SelectBox mapSelectBox = new SelectBox(twoPlayerMapNames, skin);
//				mapSelectBox.setSelection(0);
				final SelectBox<String> mapSelectBox = new SelectBox<String>(skin);
				mapSelectBox.setItems(twoPlayerMapNames);
				mapSelectBox.setSelectedIndex(RandomUtils.nextInt(0, mapSelectBox.getItems().size));
				mainTable.add(mapSelectBox).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
				
				playersNumSelectBox.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
//						int playersNumber = Integer.valueOf(playersNumSelectBox.getSelection());
						int playersNumber = Integer.valueOf(playersNumSelectBox.getSelected());
						if(playersNumber==2){
							mapSelectBox.setItems(twoPlayerMapNames);
						}
						else{
							mapSelectBox.setItems(fourPlayerMapNames);
						}
					}
				});
				
				mainTable.row();
				
				//Start Game Button
				TextButton createRoomButton = new TextButton("Create Room", skin);
				createRoomButton.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						SoundManager.INSTANCE.play(SoundType.CLICK);
						//create new room create info
						String roomName = roomNameTextField.getText();
//						int playerNumber = Integer.valueOf(playersNumSelectBox.getSelection());
//						String mapName = mapSelectBox.getSelection();
//						PlayerMoveTimeLimit playerMoveTimeLimit = playerMoveTimeLimitSelectBoxItems[playerMoveTimeLimitSelectBox.getSelectionIndex()]
//								.getPlayerMoveTimeLimit();
						int playerNumber = Integer.valueOf(playersNumSelectBox.getSelected());
						String mapName = mapSelectBox.getSelected();
						PlayerMoveTimeLimit playerMoveTimeLimit = playerMoveTimeLimitSelectBoxItems[playerMoveTimeLimitSelectBox.getSelectedIndex()]
								.getPlayerMoveTimeLimit();
						RoomCreateInfo roomCreateInfo = new RoomCreateInfo(
								roomName,
								playerNumber,
								playerMoveTimeLimit,
								mapName);
						
						clonesClient.send(new MoveRoom(ActionTypeMoveRoom.CREATE, roomCreateInfo, roomName));
					}
				});
				mainTable.add(createRoomButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				TextButton cancelButton = new TextButton("Cancel", skin);
				cancelButton.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						SoundManager.INSTANCE.play(SoundType.CLICK);
						creatingRoom=false;
						clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_LIST, null, null));
						buildRoomListUpperGUI();
					}
				});
				mainTable.add(cancelButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
//				uiDrawLock=false;
	}
	
	private synchronized void buildRoomModify(final RoomInfo roomInfo){
//		uiDrawLock=true;
		startScreen.getAndroidNativeHandler().hideSystemButtonBar();
		
		inputMultiplexer.removeProcessor(specialKeyHandler);
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
				clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_INTERIOR, null, null));
			}
		};
		inputMultiplexer.addProcessor(specialKeyHandler);
		
		creatingRoom = true;
		
		//clearing main table
		mainTable.clear();
		
				//Room Name Label
				TextButton roomNameLabel = new TextButton("Room Name", skin);
				roomNameLabel.setDisabled(true);
				mainTable.add(roomNameLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				//Room Name TextField
				final TextField roomNameTextField = new TextField(roomInfo.getRoomName(), skin);
				roomNameTextField.setDisabled(true);
				mainTable.add(roomNameTextField).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
		
				//Players Number Label
				TextButton playersNumLabel = new TextButton("Players Number", skin);
				playersNumLabel.setDisabled(true);
				mainTable.add(playersNumLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				Integer[] possibleNumbersOfPlayers = new Integer[]{2,4};
				//Players Number SelectBox
//				final SelectBox playersNumSelectBox = new SelectBox(possibleNumbersOfPlayers, skin);
//				Integer playerLimit = roomInfo.getPlayerLimit();
//				playersNumSelectBox.setSelection(
//						computeSelectBoxIndexDependingOnCurrentRoomParameter(possibleNumbersOfPlayers, playerLimit)
//						);
				final SelectBox<Integer> playersNumSelectBox = new SelectBox<Integer>(skin);
				playersNumSelectBox.setItems(possibleNumbersOfPlayers);
				Integer playerLimit = roomInfo.getPlayerLimit();
				playersNumSelectBox.setSelectedIndex(
						computeSelectBoxIndexDependingOnCurrentRoomParameter(possibleNumbersOfPlayers, playerLimit)
						);
				mainTable.add(playersNumSelectBox).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				TextButton playerMoveTimeLimitLabel = new TextButton("Player move time limit", skin);
				playerMoveTimeLimitLabel.setDisabled(true);
				mainTable.add(playerMoveTimeLimitLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				final PlayerMoveTimeLimitType[] playerMoveTimeLimitSelectBoxItems = new PlayerMoveTimeLimitType[]{
						PlayerMoveTimeLimitType.T5S,
						PlayerMoveTimeLimitType.T10S,
						PlayerMoveTimeLimitType.T15S,
						PlayerMoveTimeLimitType.T30S,
						PlayerMoveTimeLimitType.T45S,
						PlayerMoveTimeLimitType.T60S,
						PlayerMoveTimeLimitType.NO_LIMIT};
				
//				final SelectBox playerMoveTimeLimitSelectBox = new SelectBox(playerMoveTimeLimitSelectBoxItems, skin);
//				PlayerMoveTimeLimitType currentPlayerMoveTimeLimitType = PlayerMoveTimeLimitType.fromPlayerMoveTimeLimit(
//						roomInfo.getPlayerMoveTimeLimit());
//				playerMoveTimeLimitSelectBox.setSelection(
//						computeSelectBoxIndexDependingOnCurrentRoomParameter(playerMoveTimeLimitSelectBoxItems, currentPlayerMoveTimeLimitType)
//						);
				final SelectBox<PlayerMoveTimeLimitType> playerMoveTimeLimitSelectBox = new SelectBox<PlayerMoveTimeLimitType>(skin);
				playerMoveTimeLimitSelectBox.setItems(playerMoveTimeLimitSelectBoxItems);
				PlayerMoveTimeLimitType currentPlayerMoveTimeLimitType = PlayerMoveTimeLimitType.fromPlayerMoveTimeLimit(
						roomInfo.getPlayerMoveTimeLimit());
				playerMoveTimeLimitSelectBox.setSelectedIndex(
						computeSelectBoxIndexDependingOnCurrentRoomParameter(playerMoveTimeLimitSelectBoxItems, currentPlayerMoveTimeLimitType)
						);
				mainTable.add(playerMoveTimeLimitSelectBox).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				MapModel[] twoPlayerMaps = MapModelUtils.selectMapsWithSpecifiedNumberOfPlayers(2);
				MapModel[] fourPlayerMaps = MapModelUtils.selectMapsWithSpecifiedNumberOfPlayers(4);
				final String[] twoPlayerMapNames = MapModelUtils.resolveMapsToNames(twoPlayerMaps);
				final String[] fourPlayerMapNames = MapModelUtils.resolveMapsToNames(fourPlayerMaps);

				//Map Size Label
				TextButton mapLabel = new TextButton("Map", skin);
				mapLabel.setDisabled(true);
				mainTable.add(mapLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				//Map Size SelectBox
//				final SelectBox mapSelectBox = new SelectBox(twoPlayerMapNames, skin);
//				int playersNumber = Integer.valueOf(playersNumSelectBox.getSelection());
//				if(playersNumber==2){
//					mapSelectBox.setItems(twoPlayerMapNames);
//					mapSelectBox.setSelection(computeSelectBoxIndexDependingOnCurrentRoomParameter(twoPlayerMapNames, roomInfo.getMapName()));
//				}
//				else{
//					mapSelectBox.setItems(fourPlayerMapNames);
//					mapSelectBox.setSelection(computeSelectBoxIndexDependingOnCurrentRoomParameter(fourPlayerMapNames, roomInfo.getMapName()));
//				}
				final SelectBox<String> mapSelectBox = new SelectBox<String>(skin);
				mapSelectBox.setItems(twoPlayerMapNames);
				int playersNumber = Integer.valueOf(playersNumSelectBox.getSelected());
				if(playersNumber==2){
					mapSelectBox.setItems(twoPlayerMapNames);
					mapSelectBox.setSelectedIndex(computeSelectBoxIndexDependingOnCurrentRoomParameter(twoPlayerMapNames, roomInfo.getMapName()));
				}
				else{
					mapSelectBox.setItems(fourPlayerMapNames);
					mapSelectBox.setSelectedIndex(computeSelectBoxIndexDependingOnCurrentRoomParameter(fourPlayerMapNames, roomInfo.getMapName()));
				}
				mainTable.add(mapSelectBox).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
				
				playersNumSelectBox.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
//						int playersNumber = Integer.valueOf(playersNumSelectBox.getSelection());
//						if(playersNumber==2){
//							mapSelectBox.setItems(twoPlayerMapNames);
//							mapSelectBox.setSelection(computeSelectBoxIndexDependingOnCurrentRoomParameter(twoPlayerMapNames, roomInfo.getMapName()));
//						}
//						else{
//							mapSelectBox.setItems(fourPlayerMapNames);
//							mapSelectBox.setSelection(computeSelectBoxIndexDependingOnCurrentRoomParameter(fourPlayerMapNames, roomInfo.getMapName()));
//						}
						int playersNumber = Integer.valueOf(playersNumSelectBox.getSelected());
						if(playersNumber==2){
							mapSelectBox.setItems(twoPlayerMapNames);
							mapSelectBox.setSelectedIndex(computeSelectBoxIndexDependingOnCurrentRoomParameter(twoPlayerMapNames, roomInfo.getMapName()));
						}
						else{
							mapSelectBox.setItems(fourPlayerMapNames);
							mapSelectBox.setSelectedIndex(computeSelectBoxIndexDependingOnCurrentRoomParameter(fourPlayerMapNames, roomInfo.getMapName()));
						}
					}
				});
				
				mainTable.row();
				
				//Start Game Button
				TextButton modifyRoomButton = new TextButton("Modify Room", skin);
				modifyRoomButton.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						SoundManager.INSTANCE.play(SoundType.CLICK);
						//create new room create info
//						int playerNumber = Integer.valueOf(playersNumSelectBox.getSelection());
//						String mapName = mapSelectBox.getSelection();
//						PlayerMoveTimeLimit playerMoveTimeLimit = playerMoveTimeLimitSelectBoxItems[playerMoveTimeLimitSelectBox.getSelectionIndex()]
//								.getPlayerMoveTimeLimit();
						int playerNumber = Integer.valueOf(playersNumSelectBox.getSelected());
						String mapName = mapSelectBox.getSelected();
						PlayerMoveTimeLimit playerMoveTimeLimit = playerMoveTimeLimitSelectBoxItems[playerMoveTimeLimitSelectBox.getSelectedIndex()]
								.getPlayerMoveTimeLimit();
						RoomModifyInfo roomUpdatePreferencesInfo = new RoomModifyInfo(
								roomInfo.getRoomName(),
								playerNumber,
								playerMoveTimeLimit,
								mapName);
						MoveRoom moveRoom = new MoveRoom(ActionTypeMoveRoom.MODIFY, null, roomInfo.getRoomName());
						moveRoom.setRoomUpdateInfo(roomUpdatePreferencesInfo);
						clonesClient.send(moveRoom);
					}
				});
				mainTable.add(modifyRoomButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
				mainTable.row();
				
				TextButton cancelButton = new TextButton("Cancel", skin);
				cancelButton.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						SoundManager.INSTANCE.play(SoundType.CLICK);
						clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_INTERIOR, null, null));
					}
				});
				mainTable.add(cancelButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
				
//				uiDrawLock=false;
	}
	
	private <T> int computeSelectBoxIndexDependingOnCurrentRoomParameter(T[] selectBoxItems, T currentParameterValue){
		for (int i=0; i<selectBoxItems.length; i++){
			if(selectBoxItems[i].equals(currentParameterValue)){
				return i;
			}
		}
		return 0;
	}
	
	private synchronized void buildRoomInterior(RoomInfo roomInterior){
		startScreen.getAndroidNativeHandler().hideSystemButtonBar();
		
		inputMultiplexer.removeProcessor(specialKeyHandler);
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
				clonesClient.send(new MoveRoom(ActionTypeMoveRoom.LEAVE, null, null));
				buildRoomListUpperGUI();
			}
		};
		inputMultiplexer.addProcessor(specialKeyHandler);

		creatingRoom=false;
		
		int numberOfPlayers = roomInterior.getPlayerNames().length;
		int playerLimit = roomInterior.getPlayerLimit();
		String adminName = roomInterior.getAdminName();

		//clearing main table
		mainTable.clear();

		//Room Name Label
		TextButton roomTitleLabel = new TextButton("Room Name:", skin);
		roomTitleLabel.setDisabled(true);
		mainTable.add(roomTitleLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		//Room Name Label
		TextField roomNameLabel = new TextField(roomInterior.getRoomName(), skin);
		roomNameLabel.setDisabled(true);
		mainTable.add(roomNameLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		//Number of players Label
		TextButton playerNumberLabel = new TextButton("Number of players: "+numberOfPlayers+"/"+playerLimit, skin);
		playerNumberLabel.setDisabled(true);
		mainTable.add(playerNumberLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		TextButton mapNameLabel = new TextButton("Map: "+roomInterior.getMapName(), skin);
		mapNameLabel.setDisabled(true);
		mainTable.add(mapNameLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		//Players Label
		TextButton playerTitleLabel = new TextButton("Players:", skin);
		playerTitleLabel.setDisabled(true);
		mainTable.add(playerTitleLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		
		for(String playerName: roomInterior.getPlayerNames()){
			
			//Room Name Label
			TextField playerLabel = new TextField(playerName, skin);
			playerLabel.setDisabled(true);
			if(playerName.equals(adminName)){
				playerLabel.setText(playerName+" - admin");
			}
			//playerLabel.setDisabled(true);
			mainTable.add(playerLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
			
			mainTable.row();
		
		}
		
		//Start Game Button
		if(playerName.equals(adminName)){
			FlashingTextButton startGameButton = new FlashingTextButton("Start Game", skin);
			startGameButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					SoundManager.INSTANCE.play(SoundType.CLICK);
					clonesClient.send(new MoveRoom(ActionTypeMoveRoom.START_GAME, null, null));
				}
			});
			
			List<Float> flashAlphas = new ArrayList<Float>();
			
			for(float alpha = 0.15f; alpha<1.0f; alpha+=0.05f){
				flashAlphas.add(alpha);
			}
			for(float alpha = 1.0f; alpha>0.15f; alpha-=0.05f){
				flashAlphas.add(alpha);
			}
			
			startGameButton.setAlphas(flashAlphas);
			
			if(playerName.equals(adminName)){
				if(numberOfPlayers!=playerLimit){
					startGameButton.setDisabled(true);
					startGameButton.setText("Waiting for more players...");
				}
				else{
					startGameButton.startFlashing();
				}
			}
			
			mainTable.add(startGameButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		}
		else{
			TextButton gameStatusLabel = new TextButton("Waiting for more players...", skin);
			gameStatusLabel.setDisabled(true);
			if(numberOfPlayers==playerLimit){
				gameStatusLabel.setText("Ready to go...");
			}
			mainTable.add(gameStatusLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		}
		
		mainTable.row();
		
		if(roomInterior.getAdminName().equals(playerName)){
			TextButton modifyRoomButton = new TextButton("Modify Room", skin);
			modifyRoomButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					SoundManager.INSTANCE.play(SoundType.CLICK);
					clonesClient.send(new MoveRoom(ActionTypeMoveRoom.MODIFY_REQUEST, null, null));
				}
			});
			mainTable.add(modifyRoomButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
			
			mainTable.row();
		}
		
		TextButton leaveRoomButton = new TextButton("Leave Room", skin);
		leaveRoomButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				clonesClient.send(new MoveRoom(ActionTypeMoveRoom.LEAVE, null, null));
				buildRoomListUpperGUI();
			}
		});
		mainTable.add(leaveRoomButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
	}
	
	@Override
	public void resize(int width, int height) {
		if(mainViewport!=null) mainViewport.update(width, height, true);
		if(layerViewport!=null) layerViewport.update(width, height, true);
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
		if(obj instanceof UpdateRoom){
			NetworkConfig.log("received update room");
			final UpdateRoom updateRoom = (UpdateRoom) obj;
			ActionTypeUpdateRoom actionType = updateRoom.getActionType();
			if(actionType==ActionTypeUpdateRoom.ROOM_LIST && creatingRoom==false){
				roomInfoList = updateRoom.getRoomInfoList();
				
				buildRoomList(roomInfoList, sortColumn);
				
				NetworkConfig.log("Room Info List:");
				if(roomInfoList!=null){
					for(RoomInfo roomInfo: roomInfoList){
						String playerString = "";
						for(String playerName: roomInfo.getPlayerNames()){
							playerString += playerName + ", ";
						}
						NetworkConfig.log(roomInfo.getRoomId()+" | "+roomInfo.getRoomName()+" | "+playerString);
					}
				}
			}
			else if(actionType==ActionTypeUpdateRoom.FORBIDDEN_NAME){
				NetworkConfig.log("Room name forbidden");
				TextButton tb = new TextButton("ok", skin);
				errorDialogNameTooShort = new CustomDialog(smallSkin);
				Label errorLabel = new Label("This room name is too short", skin);
				errorLabel.setColor(Color.RED);
				errorDialogNameTooShort.add(errorLabel);
				errorDialogNameTooShort.row();
				tb.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						SoundManager.INSTANCE.play(SoundType.CLICK);
						errorDialogNameTooShort.remove();
					}
				});
				errorDialogNameTooShort.add(tb);
				mainStage.addActor(errorDialogNameTooShort);
			}
			else if(actionType==ActionTypeUpdateRoom.NAME_ALREADY_TAKEN){
				NetworkConfig.log("Room name already taken");
				TextButton tb = new TextButton("ok", skin);
				errorDialogNameAlreadyTaken = new CustomDialog(smallSkin);
				Label errorLabel = new Label("This room name is already taken", skin);
				errorLabel.setColor(Color.RED);
				errorDialogNameAlreadyTaken.add(errorLabel);
				errorDialogNameAlreadyTaken.row();
				tb.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						SoundManager.INSTANCE.play(SoundType.CLICK);
						errorDialogNameAlreadyTaken.remove();
					}
				});
				errorDialogNameAlreadyTaken.add(tb);
				mainStage.addActor(errorDialogNameAlreadyTaken);
			}
			else if(actionType==ActionTypeUpdateRoom.CREATE_SUCCESS){
				buildRoomInterior(updateRoom.getRoomInterior());
				NetworkConfig.log("Room create success");
				if(errorDialogNameTooShort!=null) errorDialogNameTooShort.remove();
				if(errorDialogNameAlreadyTaken!=null) errorDialogNameAlreadyTaken.remove();
			}
			else if(actionType==ActionTypeUpdateRoom.ENTER_SUCCESS){
				buildRoomInterior(updateRoom.getRoomInterior());
				NetworkConfig.log("Room enter success");
			}
			else if(actionType==ActionTypeUpdateRoom.ROOM_INTERIOR){
				buildRoomInterior(updateRoom.getRoomInterior());
				NetworkConfig.log("Room interior updated");
			}
			if(actionType==ActionTypeUpdateRoom.BACK_TO_ROOM_LIST){
				buildRoomListUpperGUI();
				
				Gdx.app.postRunnable(new Runnable() {
					public void run() {
						List<QuickMessage> quickMessagesAboutAchievements = QuickMessageUtil.prepareQuickMessagesWithNewAchievements(updateRoom.getNewAchievements(), skin, smallSkin, startScreen);
						for(QuickMessage quickMessage : quickMessagesAboutAchievements){
							QuickMessageManager.showMessageOnSpecifiedStage(quickMessage, mainStage);
						}
					}
				});
				
				interstitialAdsManager.afterLeaveRoom(startScreen.getAndroidNativeHandler(), mainStage, skin);
				 
				clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_LIST, null, null));
			}
			if(actionType==ActionTypeUpdateRoom.BACK_TO_ROOM_INTERIOR){
				clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_INTERIOR, null, null));
			}
			if(actionType==ActionTypeUpdateRoom.MODIFY_REQUEST_RESPONSE){
				buildRoomModify(updateRoom.getRoomInterior());
			}
		}
		else if(obj instanceof GameStart){
			final GameStart gameStart = (GameStart) obj;
			 Gdx.app.postRunnable(new Runnable() {
		         public void run() {
		        	 startScreen.setToOnlineGame(clonesClient, gameStart, playerName);
		         }});
			 startScreen.getAndroidNativeHandler().hideBannerAds();
		}
		else if(obj instanceof StatsResponse){
			StatsResponse statsResponse = (StatsResponse) obj;
			ActionTypeStats actionTypeStats = statsResponse.getActionTypeStats();
			
			if(actionTypeStats==ActionTypeStats.GET_GENERAL_STATS){
				buildStatistics(statsResponse.getLevel(), statsResponse.getPercentToNextLevel(),
						ClientStatsUtil.statsValuesToPercent(statsResponse.getStatsMap()),statsResponse.getStatsMap());
			}
		}
		else if(obj instanceof AchievementsResponse){
			AchievementsResponse achievementsResponse = (AchievementsResponse) obj;
			buildAchievements(achievementsResponse.getCurrentAchievements());
		}
	}
	
	@Override
	public void disconnected(Connection connection) {
		Gdx.app.postRunnable(new Runnable() {
			public void run() {
				startScreen.setToMainMenuWithMessage(QuickMessagePresets.CONNECTION_TO_SERVER_LOST.getQuickMessage());
			}
		});
	}

	@Override
	public void idle(Connection connection) {
		
	}

	@Override
	public Stage getStageForQuickMessage() {
		return mainStage;
	}

}
