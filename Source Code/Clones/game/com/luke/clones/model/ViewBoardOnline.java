package com.luke.clones.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.luke.clones.ads.InterstitialAdsManager;
import com.luke.clones.chart.TableChart;
import com.luke.clones.chart.TableProgressBarCreator;
import com.luke.clones.config.Config;
import com.luke.clones.config.ConfigUtils;
import com.luke.clones.messages.QuickMessage;
import com.luke.clones.messages.QuickMessagePresets;
import com.luke.clones.messages.QuickMessageUtil;
import com.luke.clones.model.SingleStat.TurnStatsArgumentException;
import com.luke.clones.model.actor.FieldActor2;
import com.luke.clones.model.actor.HighlightActor;
import com.luke.clones.model.actor.HighlightActorAnimatedColorChange;
import com.luke.clones.model.button.ButtonBar;
import com.luke.clones.model.button.CustomDialog;
import com.luke.clones.model.button.ImgButton;
import com.luke.clones.model.button.StatusBar;
import com.luke.clones.model.button.StatusBarOnline;
import com.luke.clones.model.type.FieldType;
import com.luke.clones.model.type.PlayerPosition;
import com.luke.clones.model.type.PlayerType;
import com.luke.clones.model.type.Position;
import com.luke.clones.model.type.Turn;
import com.luke.clones.network.communication.ActionTypeBoard;
import com.luke.clones.network.communication.ActionTypeGame;
import com.luke.clones.network.communication.ActionTypeMove;
import com.luke.clones.network.communication.ActionTypeMoveRoom;
import com.luke.clones.network.communication.ActionTypeUpdateRoom;
import com.luke.clones.network.communication.GameOver;
import com.luke.clones.network.communication.GameStart;
import com.luke.clones.network.communication.MessageToShowType;
import com.luke.clones.network.communication.MoveBoard;
import com.luke.clones.network.communication.MoveRoom;
import com.luke.clones.network.communication.RoomInfo;
import com.luke.clones.network.communication.Token;
import com.luke.clones.network.communication.UpdateBoard;
import com.luke.clones.network.communication.UpdateGame;
import com.luke.clones.network.communication.UpdateRoom;
import com.luke.clones.network.config.NetworkConfig;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.clones.screen.StartScreen;
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

public class ViewBoardOnline implements ListenerService{
	private int screenWidth, screenHeight;
	private LogicBoard board;
	private Viewport mainViewport, layerViewport, messageViewport;
	private volatile Stage mainStage, layerStage, messageStage;
	private int fieldSize;
	private FieldListener fieldListener;
	private int unusedSpace;
	private Turn turn;
	private PlayerPosition clickedPlayerPosition;
	private StatusBar statusBar;
	private HashMap<Position,FieldActor2> fieldMap;
	private Texture emptyTexture, solidTexture, ballTexture;
	
	private ClonesClient clonesClient;
	private PlayerType playerColor;
	
	private StartScreen startScreen;
	
	private RoomInfo roomInfo;
	private String playerName;
	
	private Skin skin, smallSkin;
	private Texture selectTexture;
	private Texture markBallTexture;
	
	private CustomDialog socialDialog, optionsDialog;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;
	
	private Table endTable;
	
	private SoundManager soundManager = SoundManager.INSTANCE;
	private TableProgressBarCreator tableProgressBarCreator;
	
	private TableChart timeBar;
	private TimeMeasure playerMoveTime;
	private Actor playerMoveTimeActor;
	
	private volatile TimeMeasure nextRoundAutoStartTimeMeasure;
	private Timer nextRoundAutoStartTimer;
	private volatile Label nextRoundAutoStartTimeLabel;
	
	private InterstitialAdsManager interstitialAdsManager = InterstitialAdsManager.INSTANCE;
	
	public ViewBoardOnline(ClonesClient clonesClient, Map map, PlayerType playerColor, Token token, RoomInfo roomInfo, String playerName, StartScreen startScreen, PlayerType[] playerTypeOrder) {
		construct(clonesClient, map, playerColor, token, roomInfo, playerName, startScreen, playerTypeOrder, false);
	}
	
	public synchronized void construct(ClonesClient clonesClient, Map map, PlayerType playerColor, Token token, RoomInfo roomInfo, String playerName, StartScreen startScreen, PlayerType[] playerTypeOrder, boolean restart) {
		this.clonesClient = clonesClient;
		this.playerColor = playerColor;
		this.startScreen = startScreen;
		this.roomInfo = roomInfo;
		this.playerName = playerName;
		clonesClient.setListener(this);
		init(map.getWidth(),map.getHeight(),map.getStartSolids(),map.getStartPlayers(), token.getPlayerType(), playerTypeOrder, restart);
	}
	
	private synchronized void init(int boardWidth, int boardHeight, Position[] startSolids, PlayerPosition[] startPlayers, PlayerType startingPlayer, PlayerType[] playerTypeOrder, boolean restart){
		Config.log("in init");
		skin = new Skin(Gdx.files.internal("data/skins/skin_medium.json"));
		smallSkin = new Skin(Gdx.files.internal("data/skins/skin_small.json"));
		
		screenWidth = Config.width - Config.widthOffset;
		screenHeight = Config.height - Config.gameHeightOffset - Config.timeBarHeight;
		
		board = new LogicBoard(boardWidth,boardHeight);
		
		if(startSolids != null){
			for(int i=0; i<startSolids.length; i++){
				board.setFieldTypeSolid(startSolids[i].getX(), startSolids[i].getY());
			}
		}
		
		for(int i=0; i<startPlayers.length; i++){
			board.setFieldPlayer(startPlayers[i].getX(), startPlayers[i].getY(), startPlayers[i].getPlayerType());
		}
		
		Config.log("-> bef turn create playerTypeOrder:"+playerTypeOrder);
		Config.log("-> bef turn create startingPlayer:"+startingPlayer);
		if(startingPlayer!=null) turn = new Turn(playerTypeOrder, startingPlayer);

		if(restart==false){
//			mainStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
//			layerStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
			
			mainViewport = new FitViewport(Config.width, Config.height);
			mainStage = new Stage();
			mainStage.setViewport(mainViewport);
			
			layerViewport = new FitViewport(Config.width, Config.height);
			layerStage = new Stage();
			layerStage.setViewport(layerViewport);
			
			messageViewport = new FitViewport(Config.width, Config.height);
			messageStage = new Stage();
			messageStage.setViewport(messageViewport);
			
			if(mainViewport!=null) mainViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
			if(layerViewport!=null) layerViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
			if(messageViewport!=null) messageViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		}
		
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
//				mainStage.addActor(optionsDialog);
				messageStage.addActor(optionsDialog);
			}
		};
		inputMultiplexer = new InputMultiplexer(messageStage, mainStage, specialKeyHandler);
		
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		fieldListener = new FieldListener();
		
		int xFieldSize = screenWidth/boardHeight;
		int yFieldSize = screenHeight/boardWidth;
		
		if(xFieldSize < yFieldSize) fieldSize = xFieldSize;
		else fieldSize = yFieldSize;
		unusedSpace = (int)mainStage.getWidth() - (fieldSize * boardHeight);
		
		socialDialog = new CustomDialog(smallSkin);
		optionsDialog = new CustomDialog(smallSkin);
		
		TextButton closeSocialDialogButton = new TextButton("Close", skin);
		closeSocialDialogButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				socialDialog.remove();
			}
		});
		
		String[] playerNameList = roomInfo.getPlayerNames();
		Label[] playerLabelList = new Label[playerNameList.length];

		for (int i=0; i<playerLabelList.length;i++){
			String tempName = playerNameList[i];
			//TODO arrayIndexOutOfBounds possible
			tempName += " - "+playerTypeOrder[i];
			if(playerNameList[i].equals(playerName)) tempName+= " (You)";
			playerLabelList[i] = new Label(tempName, skin);
		}
		
		for(Label tempLabel : playerLabelList){
			socialDialog.add(tempLabel);
			socialDialog.row();
		}
		
		socialDialog.add(closeSocialDialogButton).minSize(Config.width*1/4-6,40).spaceTop(8).spaceBottom(8);
		socialDialog.row();
		
		if(playerName.equals(roomInfo.getAdminName())){
			TextButton stopGameButton = new TextButton("Stop game", skin);
			stopGameButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					SoundManager.INSTANCE.play(SoundType.CLICK);
					clonesClient.send(new MoveRoom(ActionTypeMoveRoom.STOP_GAME, null, null));
				}
			});
			optionsDialog.add(stopGameButton).minSize(Config.width*1/3-6,35).spaceTop(8).spaceBottom(8);
			optionsDialog.row();
		}
		TextButton leaveRoomButton = new TextButton("Leave room", skin);
		leaveRoomButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				clonesClient.send(new MoveRoom(ActionTypeMoveRoom.LEAVE, null, null));
			}
		});
		optionsDialog.add(leaveRoomButton).minSize(Config.width*1/3-6,35).spaceTop(8).spaceBottom(8);
		optionsDialog.row();
		TextButton closeOptionsDialogButton = new TextButton("Cancel", skin);
		closeOptionsDialogButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				optionsDialog.remove();
			}
		});
		optionsDialog.add(closeOptionsDialogButton).minSize(Config.width*1/3-6,35).spaceTop(8).spaceBottom(8);
		optionsDialog.row();
		
		statusBar = new StatusBarOnline(mainStage, turn, board, skin, smallSkin, playerColor);
		statusBar.setPosition(0, (int)mainStage.getHeight()-Config.gameHeightOffset);
		statusBar.setSize((int)mainStage.getWidth(), Config.gameHeightOffset/2);
		
		Texture[] up = new Texture[3];
		Texture[] down = new Texture[3];

		up[0] = new Texture(Gdx.files.internal("data/buttons/turn.png"));
		up[0].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		down[0] = new Texture(Gdx.files.internal("data/buttons/turnc.png"));
		down[0].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		up[1] = new Texture(Gdx.files.internal("data/buttons/social.png"));
		up[1].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		down[1] = new Texture(Gdx.files.internal("data/buttons/socialc.png"));
		down[1].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		up[2] = new Texture(Gdx.files.internal("data/buttons/options.png"));
		up[2].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		down[2] = new Texture(Gdx.files.internal("data/buttons/optionsc.png"));
		down[2].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		ButtonBar buttonBar = new ButtonBar(mainStage, up, down){
			@Override
			public void click(InputEvent event, float x, float y) {
				ImgButton clicked = (ImgButton) event.getListenerActor();
				if(clicked.equals(imgButton[0])){
					SoundManager.INSTANCE.play(SoundType.CLICK);
					clickedPlayerPosition=null;
					layerStage.clear();
					MoveBoard move = new MoveBoard(ActionTypeMove.SKIP_TURN,null,null);
					clonesClient.send(move);

				}
				if(clicked.equals(imgButton[1])){
//					mainStage.addActor(socialDialog);
					SoundManager.INSTANCE.play(SoundType.CLICK);
					messageStage.addActor(socialDialog);
				}
				if(clicked.equals(imgButton[2])){
//					mainStage.addActor(optionsDialog);
					SoundManager.INSTANCE.play(SoundType.CLICK);
					messageStage.addActor(optionsDialog);
				}
			}
		};
		
		buttonBar.setPosition((int)mainStage.getWidth()-(48+2)*3, (int)mainStage.getHeight()-Math.round(Config.gameHeightOffset/2.0f));
		buttonBar.setButtonSize(48, 46);
		buttonBar.setSpacing(2,2);
		
		fieldMap = new HashMap<Position,FieldActor2>();
		
		tableProgressBarCreator = new TableProgressBarCreator();
		tableProgressBarCreator.setTextures(new Texture(Gdx.files.internal("data/images/table_chart_bars_brown_part1.png")), 
				new Texture(Gdx.files.internal("data/images/table_chart_bars_brown_part2.png")), 
				new Texture(Gdx.files.internal("data/images/table_chart_bars_brown_part3.png")));
		tableProgressBarCreator.setPosition(0, (int)mainStage.getHeight()-Config.gameHeightOffset-Config.timeBarHeight);
		tableProgressBarCreator.setChartSize(Math.round(mainStage.getWidth()), Config.timeBarHeight);
		
		nextRoundAutoStartTimeMeasure = new TimeMeasure();
//		nextRoundAutoStartTimeLabel = new Label("Next round starts in "+NEXT_ROUND_AUTO_START_TIME_IN_SECONDS+"s", smallSkin);
		nextRoundAutoStartTimeLabel = new Label("", smallSkin);
		
		startMeasureAndSignalizePlayerMoveTime();
		
		buildStage();
	}
	
	private void startMeasureAndSignalizePlayerMoveTime(){
		if(!isPlayerMoveTimeLimitUnlimited()){
			timeBar = tableProgressBarCreator.createWithPercentScale(100, 0, 0);
			mainStage.addActor(timeBar);
			
			playerMoveTime = new TimeMeasure();
			
			prepareMoveTimeActor();
			
			mainStage.addActor(playerMoveTimeActor);
		}
	}
	
	private void prepareMoveTimeActor(){
		playerMoveTimeActor = new Actor(){
//			@Override
//			public void draw(SpriteBatch batch, float parentAlpha) {
//				float playerMoveTimeNumber = playerMoveTime.getMeasuredTimeInMs()/1000f;
//				float playerMoveTimeRatio = playerMoveTimeNumber/roomInfo.getPlayerMoveTimeLimit().getLimitInSeconds();
//				int playerMoveTimePercent = Math.round(playerMoveTimeRatio*1000);
//				timeBar = tableProgressBarCreator.createWithPerMilScale(1000-playerMoveTimePercent, playerMoveTimePercent, 0);
//			}
			@Override
			public void draw(Batch batch, float parentAlpha) {
				float playerMoveTimeNumber = playerMoveTime.getMeasuredTimeInMs()/1000f;
				float playerMoveTimeRatio = playerMoveTimeNumber/roomInfo.getPlayerMoveTimeLimit().getLimitInSeconds();
				int playerMoveTimePercent = Math.round(playerMoveTimeRatio*1000);
				timeBar = tableProgressBarCreator.createWithPerMilScale(1000-playerMoveTimePercent, playerMoveTimePercent, 0);
			}
		};
	}
	
	private void stopMeasureAndSignalizePlayerMoveTime(){
		if(!isPlayerMoveTimeLimitUnlimited()){
			playerMoveTimeActor.remove();
		}
	}
	
	private boolean isPlayerMoveTimeLimitUnlimited(){
		return roomInfo.getPlayerMoveTimeLimit().isUnlimited();
	}

	private synchronized void buildStage(){
		emptyTexture = new Texture(Gdx.files.internal("data/models/blank.png"));
		solidTexture = new Texture(Gdx.files.internal("data/models/solid.png"));
		ballTexture = new Texture(Gdx.files.internal("data/models/ball.png"));
		selectTexture = new Texture(Gdx.files.internal("data/models/select2.png"));
		markBallTexture = new Texture(Gdx.files.internal("data/models/select_ball.png"));
		
		for(int i=0; i<board.getWidth();i++){
			for(int j=0; j<board.getHeight();j++){
				FieldType fieldType = board.getFieldType(i, j);
				if(fieldType == FieldType.EMPTY){
					FieldActor2 emptyField = new FieldActor2(emptyTexture, solidTexture, ballTexture, emptyTexture, null, new Position(i,j),fieldType);
					emptyField.setPosition((j*fieldSize)+unusedSpace/2, i*fieldSize);
					emptyField.setSize(fieldSize, fieldSize);
					emptyField.addListener(fieldListener);
					mainStage.addActor(emptyField);
					fieldMap.put(emptyField.getPosition(), emptyField);
					
				}
				else if(fieldType == FieldType.SOLID){
					FieldActor2 solidField = new FieldActor2(emptyTexture, solidTexture, ballTexture, emptyTexture, null, new Position(i,j),fieldType);
					solidField.setPosition((j*fieldSize)+unusedSpace/2, i*fieldSize);
					solidField.setSize(fieldSize, fieldSize);
					solidField.addListener(fieldListener);
					mainStage.addActor(solidField);
					fieldMap.put(solidField.getPosition(), solidField);
				}
				else if(fieldType == FieldType.TAKEN){
					PlayerType playerType;
					playerType = board.getFieldPlayer(i, j);
					Color color;
					if(playerType == PlayerType.BLUE) color = Color.BLUE;
					else if(playerType == PlayerType.GREEN) color = Color.GREEN;
					else if(playerType == PlayerType.RED) color = Color.RED;
					else if(playerType == PlayerType.ORANGE) color = Color.ORANGE;
					else color = Color.WHITE;
					FieldActor2 ballField = new FieldActor2(emptyTexture, solidTexture, ballTexture, emptyTexture, color, new Position(i,j),fieldType);
					ballField.setPosition((j*fieldSize)+unusedSpace/2, i*fieldSize);
					ballField.setSize(fieldSize, fieldSize);
					ballField.addListener(fieldListener);
					mainStage.addActor(ballField);
					fieldMap.put(ballField.getPosition(), ballField);
				}
			}
		}
		
		flashCurrentPlayerBalls();
		
		startCountingPlayerMoveTime();
		
		if(Config.debug) System.out.println(" Red: "+board.getBallCount(PlayerType.RED)+" Blue: "+board.getBallCount(PlayerType.BLUE)+" Green: "+board.getBallCount(PlayerType.GREEN)+" Orange: "+board.getBallCount(PlayerType.ORANGE));
	}
	
	private void startCountingPlayerMoveTime(){
		if(!isPlayerMoveTimeLimitUnlimited()){
			playerMoveTime.start();
		}
	}
	
	private void updateStage(){		
		statusBar.reflesh();
		for(int i=0; i<board.getWidth();i++){
			for(int j=0; j<board.getHeight();j++){
				FieldType fieldType = board.getFieldType(i, j);
				Position pos = new Position(i,j);
				FieldActor2 field = fieldMap.get(pos);
					if(fieldType == FieldType.TAKEN){
						PlayerType playerType = board.getFieldPlayer(i, j);
						Color color;
						if(playerType == PlayerType.BLUE) color = Color.BLUE;
						else if(playerType == PlayerType.GREEN) color = Color.GREEN;
						else if(playerType == PlayerType.RED) color = Color.RED;
						else if(playerType == PlayerType.ORANGE) color = Color.ORANGE;
						else color = Color.WHITE;
						if(field.getColor() != color) field.setColor(color);
						if(field.getFieldType() != fieldType) field.setFieldType(fieldType);
					}
					else{
						if(field.getFieldType() != fieldType) {
							field.setFieldType(fieldType);
							field.setColor(null);
						}
					}
			}
		}
		
		flashCurrentPlayerBalls();
		
		startCountingPlayerMoveTime();
		
		if(Config.debug) System.out.println(" Red: "+board.getBallCount(PlayerType.RED)+" Blue: "+board.getBallCount(PlayerType.BLUE)+" Green: "+board.getBallCount(PlayerType.GREEN)+" Orange: "+board.getBallCount(PlayerType.ORANGE));
	}
	
	public synchronized Stage getMainStage(){
		return mainStage;
	}
	
	public synchronized Stage getLayerStage(){
		return layerStage;
	}
	
	public Stage getMessageStage() {
		return messageStage;
	}

	private class FieldListener extends ClickListener{

		@Override
		public synchronized void clicked(InputEvent event, float x, float y) {
			super.clicked(event, x, y);
			FieldActor2 field = (FieldActor2) event.getListenerActor();
			Position pos = field.getPosition();
			PlayerType fieldPlayer = board.getFieldPlayer(pos.getX(), pos.getY());
			if(board.getFieldType(pos.getX(), pos.getY()) == FieldType.TAKEN && fieldPlayer == turn.getCurrentPlayer()){
				clickedPlayerPosition = new PlayerPosition(pos.getX(), pos.getY(), fieldPlayer);
				
				highlightPossibleMoveFields(clickedPlayerPosition);
			}
			else if(board.getFieldType(pos.getX(), pos.getY()) == FieldType.EMPTY){
				if(clickedPlayerPosition!=null){
					if(board.isMovePossible(clickedPlayerPosition.getX(), clickedPlayerPosition.getY(), pos.getX(), pos.getY())){
						Config.log("My player Type: "+playerColor);
						clonesClient.send(new MoveBoard(ActionTypeMove.MOVE,clickedPlayerPosition,pos));
						clickedPlayerPosition=null;
						
						layerStage.clear();
					}
				}
			}
		}

	}
	
	private void highlightPossibleMoveFields(PlayerPosition chosenPlayerPosition){
		if(chosenPlayerPosition.getPlayerType() == playerColor){
			List<Position> possibleCloneFieldsList = new ArrayList<Position>();
			List<Position> possibleJumpFieldsList = new ArrayList<Position>();
			
			for(int i= chosenPlayerPosition.getX()-2; i<=chosenPlayerPosition.getX()+2;i++){
				for(int j= chosenPlayerPosition.getY()-2;j<=chosenPlayerPosition.getY()+2;j++){
					if(board.isClonePossible(chosenPlayerPosition.getX(), chosenPlayerPosition.getY(), i, j)){
						possibleCloneFieldsList.add(new Position(i, j));
					}
					if(board.isJumpPossible(chosenPlayerPosition.getX(), chosenPlayerPosition.getY(), i, j)){
						possibleJumpFieldsList.add(new Position(i, j));
					}
				}
				
				layerStage.clear();
				
				PlayerType playerType = chosenPlayerPosition.getPlayerType();
				Color playerColor = convertPlayerTypeToPlayerColor(playerType);
				
				Color markBallColor = new Color(playerColor.r, playerColor.g, playerColor.b, 0.4f);
				HighlightActor markBallLight = new HighlightActor(markBallTexture, markBallColor, new Position((chosenPlayerPosition.getY()*fieldSize)+unusedSpace/2, chosenPlayerPosition.getX()*fieldSize));
				markBallLight.setPosition((chosenPlayerPosition.getY()*fieldSize)+unusedSpace/2, chosenPlayerPosition.getX()*fieldSize);
				markBallLight.setSize(fieldSize, fieldSize);
				layerStage.addActor(markBallLight);
				
				for(Position tempPosition: possibleCloneFieldsList){
					Color highlightColor = new Color(playerColor.r, playerColor.g, playerColor.b, 0.4f);
					HighlightActor light = new HighlightActor(selectTexture, highlightColor, new Position((tempPosition.getY()*fieldSize)+unusedSpace/2, tempPosition.getX()*fieldSize));
					light.setPosition((tempPosition.getY()*fieldSize)+unusedSpace/2, tempPosition.getX()*fieldSize);
					light.setSize(fieldSize, fieldSize);
					layerStage.addActor(light);
				}
				for(Position tempPosition: possibleJumpFieldsList){
					Color highlightColor = new Color(playerColor.r, playerColor.g, playerColor.b, 0.15f);
					HighlightActor light = new HighlightActor(selectTexture, highlightColor, new Position((tempPosition.getY()*fieldSize)+unusedSpace/2, tempPosition.getX()*fieldSize));
					light.setPosition((tempPosition.getY()*fieldSize)+unusedSpace/2, tempPosition.getX()*fieldSize);
					light.setSize(fieldSize, fieldSize);
					layerStage.addActor(light);
				}
			}
			
		}
	}
	
	private void flashCurrentPlayerBalls(){
		layerStage.clear();
		PlayerType currentPlayer = turn.getCurrentPlayer();
		if(currentPlayer != playerColor){
			return;
		}
		
		List<Position> ballsToFlash = getCurrentPlayerBallsPositions();
		
//		PlayerType playerType = turn.getCurrentPlayer();
		Color flashColor = Color.WHITE;
		
		List<Color> flashColors = new ArrayList<Color>();
		
		for(float alpha = 0.0f; alpha<1.0f; alpha+=0.05f){
			Color colorToAdd = new Color(flashColor.r, flashColor.g, flashColor.b, alpha);
			flashColors.add(colorToAdd);
		}
		for(float alpha = 1.0f; alpha>0.0f; alpha-=0.05f){
			Color colorToAdd = new Color(flashColor.r, flashColor.g, flashColor.b, alpha);
			flashColors.add(colorToAdd);
		}
		
		for(Position tempPosition: ballsToFlash){
			HighlightActorAnimatedColorChange light = new HighlightActorAnimatedColorChange(markBallTexture, flashColors, new Position((tempPosition.getY()*fieldSize)+unusedSpace/2, tempPosition.getX()*fieldSize));
			light.setPosition((tempPosition.getY()*fieldSize)+unusedSpace/2, tempPosition.getX()*fieldSize);
			light.setSize(fieldSize, fieldSize);
			layerStage.addActor(light);
		}
		
	}
	
	private List<Position> getCurrentPlayerBallsPositions(){
		List<Position> balls = new ArrayList<Position>();

		for(int i=0; i<board.getWidth(); i++){
			for(int j=0; j<board.getHeight(); j++){
				boolean fieldWithBallWithPlayerColor = board.getFieldType(i, j)==FieldType.TAKEN && 
						board.getFieldPlayer(i, j)==playerColor;
				if(fieldWithBallWithPlayerColor){
					balls.add(new Position(i,j));
				}
			}
		}
		
		return balls;
	}
	
	private Color convertPlayerTypeToPlayerColor(PlayerType playerType){
		Color playerColor;
		if(playerType == PlayerType.BLUE) playerColor = Color.BLUE;
		else if(playerType == PlayerType.GREEN) playerColor = Color.GREEN;
		else if(playerType == PlayerType.RED) playerColor = Color.RED;
		else if(playerType == PlayerType.ORANGE) playerColor = Color.ORANGE;
		else playerColor = Color.WHITE;
		return playerColor;
	}

	@Override
	public void connected(Connection connection) {
		
	}

	@Override
	public synchronized void received(Connection connection, final Object obj) {
		if(obj instanceof UpdateRoom){
			NetworkConfig.log("received update room");
			final UpdateRoom updateRoom = (UpdateRoom) obj;
			ActionTypeUpdateRoom actionType = updateRoom.getActionType();
			if(actionType==ActionTypeUpdateRoom.BACK_TO_ROOM_LIST){
				if(updateRoom.getNewAchievements()!=null && updateRoom.getNewAchievements().length>0){
					 Gdx.app.postRunnable(new Runnable() {
				         public void run() {
				        	 List<QuickMessage> quickMessagesAboutAchievements = QuickMessageUtil.prepareQuickMessagesWithNewAchievements(updateRoom.getNewAchievements(),skin, smallSkin, startScreen);
				        	 startScreen.setToOnlineWithoutNameWithMultipleMessages(clonesClient, false, quickMessagesAboutAchievements);
				         }
				     });
				}
				else{
				 Gdx.app.postRunnable(new Runnable() {
			         public void run() {
			        	 startScreen.setToOnlineWithoutName(clonesClient, false);
			         }});
				}
				startScreen.getAndroidNativeHandler().showBannerAds();
				interstitialAdsManager.afterLeaveRoom(startScreen.getAndroidNativeHandler(), mainStage, skin);
			}
			if(actionType==ActionTypeUpdateRoom.BACK_TO_ROOM_INTERIOR){
				if(updateRoom.getMessageToShow()==MessageToShowType.ONE_OF_PLAYERS_LEFT_ROOM){
				 Gdx.app.postRunnable(new Runnable() {
			         public void run() {
			        	 startScreen.setToOnlineWithoutNameWithMessage(clonesClient, true, QuickMessagePresets.ONE_OF_PLAYERS_LEFT_ROOM.getQuickMessage());
			         }});
				}
				else if(updateRoom.getMessageToShow()==MessageToShowType.ADMIN_STOPPED){
					 Gdx.app.postRunnable(new Runnable() {
				         public void run() {
				        	 startScreen.setToOnlineWithoutNameWithMessage(clonesClient, true, QuickMessagePresets.ADMIN_STOPPED.getQuickMessage());
				         }});
				}
				else{
					 Gdx.app.postRunnable(new Runnable() {
				         public void run() {
				        	 startScreen.setToOnlineWithoutName(clonesClient, true);
				         }});
				}
				startScreen.getAndroidNativeHandler().showBannerAds();
			}
		}
		if(obj instanceof UpdateBoard){
			UpdateBoard update = (UpdateBoard) obj;
			PlayerPosition playerPos = update.getPlayerPosition();
			Position movePos = update.getPosition();
			
			if(update.getActionType()==ActionTypeBoard.CLONE){
				board.clone(playerPos.getX(), playerPos.getY(), movePos.getX(), movePos.getY());
				soundManager.play(SoundType.CLONE);
			}
			else if(update.getActionType()==ActionTypeBoard.JUMP){
				board.jump(playerPos.getX(), playerPos.getY(), movePos.getX(), movePos.getY());
				soundManager.play(SoundType.JUMP);
			}
			else if(update.getActionType()==ActionTypeBoard.NEXT_TURN){
				//just do nothing
			}
			
			turn.nextTurn();
			updateStage();
		}
		//to delete
		if(obj instanceof UpdateGame){
			UpdateGame update = (UpdateGame) obj;
			if(update.getActionType()==ActionTypeGame.NEXT_TURN){
				turn.nextTurn();
				updateStage();
			}
		}
		
		if(obj instanceof GameOver){
			Gdx.app.postRunnable(new Runnable() {
		         public void run() {
		    System.out.println("game over received");
		        	 
		    interstitialAdsManager.afterRoundFinish();

		    stopMeasureAndSignalizePlayerMoveTime();
		    layerStage.clear();
		        	 
			GameOver over = (GameOver) obj;
			
			PlayerScore[] playerScore = over.getPlayerScore();
			TurnStats turnStats = over.getTurnStats();
			boolean firstRound = over.isFirstRound();
					
					Skin tSkin = smallSkin;
					endTable = new Table(tSkin);
					endTable.setBackground(tSkin.getDrawable("message-round-large-bright"));
					endTable.setSize(screenWidth, 240);
					if(playerScore.length>3){
						endTable.setHeight(endTable.getHeight()+80);
					}
					endTable.setPosition(screenWidth/2-endTable.getWidth()/2, screenHeight/2-endTable.getHeight()/2);
					
					Label placeLabel = new Label("", skin);
//					if(playerScore.length>0 && playerScore[0].getPlayerType()==playerColor){
//						placeLabel.setText("You won!");
//					}
//					else if(playerScore.length>1 && playerScore[1].getPlayerType()==playerColor){
//						placeLabel.setText("You were 2nd");
//					}
//					else if(playerScore.length>2 && playerScore[2].getPlayerType()==playerColor){
//						placeLabel.setText("You were 3rd");
//					}
//					else if(playerScore.length>3 && playerScore[3].getPlayerType()==playerColor){
//						placeLabel.setText("You were 4th");
//					}
					int playerPlace = getPlayerPlace(playerScore);
					
					for(int i=playerPlace; i>=0; i--){
						if(i==0 || playerScore[i].getPlayerScore()!=playerScore[i-1].getPlayerScore()){
							placeLabel.setText(getPlaceDescriptionByPlace(i));
							break;
						}
					}
					
					endTable.add(placeLabel).colspan(5);
					
					endTable.row();
					
					Label titleLabelUpper1 = new Label("", tSkin);
					Label titleLabelUpper2 = new Label("", tSkin);
					Label titleLabelUpper3 = new Label("Balls scored", tSkin);
					Label titleLabelUpper4 = new Label("Balls scored", tSkin);
					Label titleLabelUpper5 = new Label("Games", tSkin);
					
					endTable.add(titleLabelUpper1).spaceLeft(10).spaceRight(10).spaceTop(10);
					endTable.add(titleLabelUpper2).spaceLeft(10).spaceRight(10).spaceTop(10);
					endTable.add(titleLabelUpper3).spaceLeft(10).spaceRight(10).spaceTop(10);
					if(firstRound==false){
						endTable.add(titleLabelUpper4).spaceLeft(10).spaceRight(10).spaceTop(10);
						endTable.add(titleLabelUpper5).spaceLeft(10).spaceRight(10).spaceTop(10);
					}
					else{
						endTable.add().spaceLeft(10).spaceRight(10).spaceTop(10);
						endTable.add().spaceLeft(10).spaceRight(10).spaceTop(10);
					}
					endTable.row();
					
					Label titleLabel1 = new Label("Pos.", tSkin);
					Label titleLabel2 = new Label("Player", tSkin);
					Label titleLabel3 = new Label("this round", tSkin);
					Label titleLabel4 = new Label("overall", tSkin);
					Label titleLabel5 = new Label("won", tSkin);
					
					endTable.add(titleLabel1).spaceLeft(10).spaceRight(10).spaceBottom(10);
					endTable.add(titleLabel2).spaceLeft(10).spaceRight(10).spaceBottom(10);
					endTable.add(titleLabel3).spaceLeft(10).spaceRight(10).spaceBottom(10);
					if(firstRound==false){
						endTable.add(titleLabel4).spaceLeft(10).spaceRight(10).spaceBottom(10);
						endTable.add(titleLabel5).spaceLeft(10).spaceRight(10).spaceBottom(10);
					}
					else{
						endTable.add().spaceLeft(10).spaceRight(10).spaceBottom(10);
						endTable.add().spaceLeft(10).spaceRight(10).spaceBottom(10);
					}
					endTable.row();
					
					int j=0;
					Label tLabel1=null,tLabel2=null, tLabel3=null, tLabel4=null, tLabel5=null;
					for(int i=0;i<playerScore.length;i++){
						if(i>0 && playerScore[i-1].getPlayerScore()==playerScore[i].getPlayerScore()){
							tLabel1 = new Label("", tSkin);
						}
						else{
							j++;
							tLabel1 = new Label(j+".", tSkin);
						}
						
						tLabel2 = new Label(""+playerScore[i].getPlayerType(), tSkin);
						if(playerScore[i].getPlayerType()==playerColor){
							tLabel2.setText(tLabel2.getText()+" [YOU]");
						}
						tLabel3 = new Label(""+playerScore[i].getPlayerScore(), tSkin);
						if(firstRound==false){
							try {
								tLabel4 = new Label(""+turnStats.getPlayerGameBalls(playerScore[i].getPlayerType()), tSkin);
								tLabel5 = new Label(""+turnStats.getPlayerGameWons(playerScore[i].getPlayerType()), tSkin);
							} catch (TurnStatsArgumentException e) {
								e.printStackTrace();
							}
						}
						
						if(playerScore[0].getPlayerType()==playerColor){
							soundManager.play(SoundType.WIN);
						}
						else{
							soundManager.play(SoundType.LOSE);
						}
						
						if(tLabel1!=null)endTable.add(tLabel1).space(10);
						if(tLabel2!=null)endTable.add(tLabel2).space(10);
						if(tLabel3!=null)endTable.add(tLabel3).space(10);
						if(tLabel4!=null)endTable.add(tLabel4).space(10);
						if(tLabel5!=null)endTable.add(tLabel5).space(10);
						endTable.row();
					}
					
					if(playerName.equals(roomInfo.getAdminName())){
//						TextButton nextRoundB = new TextButton("Start next round", tSkin);
//						nextRoundB.addListener(new ClickListener(){
//							@Override
//							public void clicked(InputEvent event, float x, float y) {
//								clonesClient.send(new MoveRoom(ActionTypeMoveRoom.RESTART_GAME, null, null));
//							}
//						});
//						endTable.add(nextRoundB).colspan(2);
						
						TextButton endGameB = new TextButton("End Game", tSkin, "dark");
						endGameB.addListener(new ClickListener(){
							@Override
							public void clicked(InputEvent event, float x, float y) {
								clonesClient.send(new MoveRoom(ActionTypeMoveRoom.STOP_GAME, null, null));
							}
						});
//						endTable.add(endGameB).colspan(2);
//						endTable.add();
						endTable.add(endGameB).colspan(5).minSize(Config.width*1/4-6, 25).spaceTop(6).spaceBottom(6);
						endTable.row();
					}
					else{
						TextButton leaveRoomB = new TextButton("Leave room", tSkin, "dark");
						leaveRoomB.addListener(new ClickListener(){
							@Override
							public void clicked(InputEvent event, float x, float y) {
								clonesClient.send(new MoveRoom(ActionTypeMoveRoom.LEAVE, null, null));
							}
						});
						endTable.add(leaveRoomB).colspan(5).minSize(Config.width*1/4-6, 25).spaceTop(6).spaceBottom(6);
						endTable.row();
					}
					
					prepareNextRoundAutoStart();
					
					endTable.add(nextRoundAutoStartTimeLabel).colspan(5);
					
//					mainStage.addActor(endTable);
					messageStage.addActor(endTable);
					endTable.toFront();
//			}
		         }});
		}
		if(obj instanceof GameStart){
			final GameStart gameStart = (GameStart) obj;
			Gdx.app.postRunnable(new Runnable() {
	        public void run() {
	        	//may cause errors
	        	if(endTable!=null) endTable.remove();
	        	construct(clonesClient, gameStart.getMap(), gameStart.getPlayerColor(),gameStart.getToken(), gameStart.getRoomInfo(), playerName, startScreen, gameStart.getPlayerTypeOrder(), true);
	        }});
		}
//		if(obj instanceof MessageShowRequest){
//			MessageShowRequest messageShowRequest = (MessageShowRequest) obj;
//			if(messageShowRequest.getMessageToShow()==MessageToShowType.ONE_OF_PLAYERS_LEFT_ROOM){
//				QuickMessageManager.showMessageOnSpecifiedStage(QuickMessagePresets.ONE_OF_PLAYERS_LEFT_ROOM.getQuickMessage(), mainStage);
//			}
//			else if(messageShowRequest.getMessageToShow()==MessageToShowType.ADMIN_STOPPED){
//				QuickMessageManager.showMessageOnSpecifiedStage(QuickMessagePresets.ADMIN_STOPPED.getQuickMessage(), mainStage);
//			}
//		}
	}
	
	private int getPlayerPlace(PlayerScore[] playerScore) {
		for(int i=0; i<playerScore.length; i++){
			if(playerScore[i].getPlayerType()==playerColor){
				return i;
			}
		}
		return -1;
	}
	
	private String getPlaceDescriptionByPlace(int place){
		switch(place){
		case 0:
			return "You won!";
		case 1:
			return "You were 2nd";
		case 2:
			return "You were 3rd";
		case 3:
			return "You were 4th";
		default:
			return "Label error";
		}
	}
	
	private synchronized void prepareNextRoundAutoStart(){
			nextRoundAutoStartTimeMeasure.start();
			
			nextRoundAutoStartTimer = new Timer(true);

			TimerTask hideMessageTask = new TimerTask() {
				@Override
				public void run() {
					Gdx.app.postRunnable(new Runnable() {
				        public void run() {
				        	updateNextRoundAutoStartTimeText();
				        }
					});
					if(nextRoundAutoStartTimeMeasure.getMeasuredTimeInMs()
							>=ConfigUtils.getNextRoundAutoStartTimeDependingOnNumberOfPlayers(roomInfo.getPlayerLimit())*1000){
						Gdx.app.postRunnable(new Runnable() {
					        public void run() {
//					        	hideMessage();
					        	nextRoundAutoStartTimer.cancel();
					        }
						});
					}
				}
			};
			
			nextRoundAutoStartTimer.scheduleAtFixedRate(hideMessageTask, 0, 1000);
	}

	private void updateNextRoundAutoStartTimeText(){
			int measuredTimeInSeconds = Math.round(nextRoundAutoStartTimeMeasure.getMeasuredTimeInMs()/1000f);
			int timeToStart = ConfigUtils.getNextRoundAutoStartTimeDependingOnNumberOfPlayers(roomInfo.getPlayerLimit()) - measuredTimeInSeconds;
			nextRoundAutoStartTimeLabel.setText("Next round starts in "+timeToStart+"s");
	}
	
	@Override
	public void disconnected(Connection connection) {
		Gdx.app.postRunnable(new Runnable() {
			public void run() {
				startScreen.setToMainMenuWithMessage(QuickMessagePresets.CONNECTION_TO_SERVER_LOST.getQuickMessage());
			}
		});
		startScreen.getAndroidNativeHandler().showBannerAds();
	}

	@Override
	public void idle(Connection connection) {
		
	}
	
}
