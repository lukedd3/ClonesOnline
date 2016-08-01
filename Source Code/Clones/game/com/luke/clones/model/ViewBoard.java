package com.luke.clones.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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
import com.luke.clones.autogame.AutoGameUtil;
import com.luke.clones.bot.BotMoveType;
import com.luke.clones.bot.PossibleMove;
import com.luke.clones.chart.TableChart;
import com.luke.clones.chart.TableProgressBarCreator;
import com.luke.clones.config.Config;
import com.luke.clones.map.model.MapType;
import com.luke.clones.model.SingleStat.TurnStatsArgumentException;
import com.luke.clones.model.actor.FieldActor2;
import com.luke.clones.model.actor.HighlightActor;
import com.luke.clones.model.actor.HighlightActorAnimatedColorChange;
import com.luke.clones.model.button.ButtonBar;
import com.luke.clones.model.button.CustomDialog;
import com.luke.clones.model.button.ImgButton;
import com.luke.clones.model.button.StatusBar;
import com.luke.clones.model.button.StatusBarOffline;
import com.luke.clones.model.type.FieldType;
import com.luke.clones.model.type.PlayerMoveTimeLimitType;
import com.luke.clones.model.type.PlayerPosition;
import com.luke.clones.model.type.PlayerType;
import com.luke.clones.model.type.Position;
import com.luke.clones.model.type.Turn;
import com.luke.clones.screen.StartScreen;
import com.luke.clones.sound.SoundManager;
import com.luke.clones.sound.SoundType;

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

public class ViewBoard {
	private static final int ENSURE_MS = 2;
	private static final int SECONDS_TO_MILLISECONDS_MULTIPLER = 1000;
	
	private int screenWidth, screenHeight;
	private int boardWidth, boardHeight;
	private LogicBoard board;
	private Viewport mainViewport, layerViewport, messageViewport;
	private Stage mainStage, layerStage, messageStage;
	private int fieldSize;
	private FieldListener fieldListener;
	private int unusedSpace;
	private Turn turn;
	private PlayerPosition clickedPlayer;
	private StatusBar statusBar;
	private HashMap<Position,FieldActor2> fieldMap;
	private Texture emptyTexture, solidTexture, ballTexture;
	private TurnStats turnStats;
	private boolean pause;
	private StartScreen startScreen;
//	private PlayerType startingPlayer;
	Map map;
	
	private Skin skin, smallSkin;
	private Texture selectTexture;
	private Texture markBallTexture;
	
	private CustomDialog socialDialog, optionsDialog;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;
	
	private SoundManager soundManager = SoundManager.INSTANCE;
	private TableProgressBarCreator tableProgressBarCreator;
	
	private TableChart timeBar;
	private TimeMeasure playerMoveTime;
	private Actor playerMoveTimeActor;
	
	private PlayerMoveTimeLimitType playerMoveTimeLimitType;
	
	private TimerTask checkingPlayerMoveTimeTask;
	private Timer checkingPlayerMoveTimeTimer;
	
	private PlayerType startPlayer;
	
	public ViewBoard(Map map, PlayerMoveTimeLimitType playerMoveTimeLimit, StartScreen startScreen){
		this(map.getWidth(),map.getHeight(), map, startScreen, playerMoveTimeLimit);
	}
	
	public ViewBoard(int boardWidth, int boardHeight, Map map, final StartScreen startScreen, PlayerMoveTimeLimitType playerMoveTimeLimit){
		this.map = map;
//		this.startingPlayer = startingPlayer;
		
		skin = new Skin(Gdx.files.internal("data/skins/skin_medium.json"));
		smallSkin = new Skin(Gdx.files.internal("data/skins/skin_small.json"));
		
		screenWidth = Config.width - Config.widthOffset;
		screenHeight = Config.height - Config.gameHeightOffset;
		
//		screenWidth = Config.width - Config.widthOffset;
//		screenHeight = Config.height - Config.gameHeightOffset;
		
		this.playerMoveTimeLimitType = playerMoveTimeLimit;
		
		this.startScreen = startScreen;
		
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		
//		if(startingPlayer!=null) turn = new Turn(map.getStartPlayers(), startingPlayer);
//		else 
		turn = new Turn(map.getStartPlayers(), null);
		
		Random random = new Random();
		PlayerType[] playerArray = turn.getAvailablePlayers();
		startPlayer = playerArray[random.nextInt(playerArray.length)];
		turn = new Turn(map.getStartPlayers(), startPlayer);
		
//		System.out.println("Current player(constructor):"+turn.getCurrentPlayer());
		
		if(map.getMapType()==MapType.ASYMMETRIC){
			while(true){
				if(map.getStartPlayers()[0].getPlayerType()==turn.getCurrentPlayer()){
					break;
				}
				System.out.println("Swap at the beggining:");
				PlayerPosition[] startPlayersToSwap = map.getStartPlayers();
				PlayerPosition[] swappedPlayers = new PlayerPosition[startPlayersToSwap.length];
				for(int i=0; i<startPlayersToSwap.length; i++){
					PlayerType playerColor = startPlayersToSwap[i].getPlayerType();
					int playerColorOrdinal = playerColor.ordinal();
					playerColorOrdinal++;
					if(playerColorOrdinal>turn.getAvailablePlayers().length-1){
						playerColorOrdinal=0;
					}
					playerColor = PlayerType.values()[playerColorOrdinal];
					swappedPlayers[i] = new PlayerPosition(startPlayersToSwap[i].getX(), startPlayersToSwap[i].getY(), playerColor);
				}
				map.setStartPlayers(swappedPlayers);
			}
		}
		
		board = createLogicBoard(boardWidth, boardHeight, map.getStartSolids(), map.getStartPlayers());
		
//		mainStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
//		layerStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		
		mainViewport = new FitViewport(Config.width, Config.height);
		mainStage = new Stage();
		mainStage.setViewport(mainViewport);
		
		layerViewport = new FitViewport(Config.width, Config.height);
		layerStage = new Stage();
		layerStage.setViewport(layerViewport);
		
		messageViewport = new FitViewport(Config.width, Config.height);
		messageStage = new Stage();
		messageStage.setViewport(messageViewport);
		
		if(mainViewport!=null) mainViewport.update(Config.width, Config.height, true);
		if(layerViewport!=null) layerViewport.update(Config.width, Config.height, true);
		if(messageViewport!=null) messageViewport.update(Config.width, Config.height, true);
		
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
		
		Label[] playerLabelList = new Label[turn.getAvailablePlayers().length];

		for (int i=0; i<turn.getAvailablePlayers().length;i++){
			String tempName = turn.getAvailablePlayers()[i].name();
			//TODO arrayIndexOutOfBounds possible
//			if(turn.getAvailablePlayers()[i].equals(turn.getCurrentPlayer())) tempName+= " (Current move)";
			playerLabelList[i] = new Label(tempName, skin);
		}
		
		for(Label tempLabel : playerLabelList){
			socialDialog.add(tempLabel);
			socialDialog.row();
		}
		
		socialDialog.add(closeSocialDialogButton).minSize(Config.width*1/4-6,40).spaceTop(8).spaceBottom(8);
		socialDialog.row();

		TextButton stopGameButton = new TextButton("Stop game", skin);
		stopGameButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				startScreen.setToMainMenu();
				startScreen.getAndroidNativeHandler().showBannerAds();
			}
		});
		optionsDialog.add(stopGameButton).minSize(Config.width*1/3-6,35).spaceTop(8).spaceBottom(8);
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
		
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
				messageStage.addActor(optionsDialog);
			}
		};
		
		inputMultiplexer = new InputMultiplexer(messageStage, mainStage, specialKeyHandler);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		statusBar = createStatusBar();
		
		ButtonBar buttonBar = createButtonBar();
		
		buttonBar.setPosition((int)mainStage.getWidth()-(48+2)*3, (int)mainStage.getHeight()-Config.gameHeightOffset/2);
		buttonBar.setButtonSize(48, 46);
		buttonBar.setSpacing(2,2);
		
		fieldMap = new HashMap<Position,FieldActor2>();
		
		tableProgressBarCreator = new TableProgressBarCreator();
		tableProgressBarCreator.setTextures(new Texture(Gdx.files.internal("data/images/table_chart_bars_brown_part1.png")), 
				new Texture(Gdx.files.internal("data/images/table_chart_bars_brown_part2.png")), 
				new Texture(Gdx.files.internal("data/images/table_chart_bars_brown_part3.png")));
		tableProgressBarCreator.setPosition(0, (int)mainStage.getHeight()-Config.gameHeightOffset-Config.timeBarHeight);
		tableProgressBarCreator.setChartSize(Math.round(mainStage.getWidth()), Config.timeBarHeight);
		
//		startMeasureAndSignalizePlayerMoveTime();
		
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
//				float playerMoveTimeRatio = playerMoveTimeNumber/playerMoveTimeLimitType.getPlayerMoveTimeLimit().getLimitInSeconds();
//				int playerMoveTimePercent = Math.round(playerMoveTimeRatio*1000);
//				timeBar = tableProgressBarCreator.createWithPerMilScale(1000-playerMoveTimePercent, playerMoveTimePercent, 0);
//			}
			@Override
			public void draw(Batch batch, float parentAlpha) {
				float playerMoveTimeNumber = playerMoveTime.getMeasuredTimeInMs()/1000f;
				float playerMoveTimeRatio = playerMoveTimeNumber/playerMoveTimeLimitType.getPlayerMoveTimeLimit().getLimitInSeconds();
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
		return playerMoveTimeLimitType.getPlayerMoveTimeLimit().isUnlimited();
	}
	
	private LogicBoard createLogicBoard(int boardWidth, int boardHeight, Position[] startSolids, PlayerPosition[] startPlayers){
		board = new LogicBoard(boardWidth,boardHeight);
		
		if(startSolids != null){
			for(int i=0; i<startSolids.length; i++){
				board.setFieldTypeSolid(startSolids[i].getX(), startSolids[i].getY());
			}
		}
		
		for(int i=0; i<startPlayers.length; i++){
			board.setFieldPlayer(startPlayers[i].getX(), startPlayers[i].getY(), startPlayers[i].getPlayerType());
		}
		
		return board;
	}
	
	private StatusBar createStatusBar(){
		statusBar = new StatusBarOffline(mainStage, turn, board, new Skin(Gdx.files.internal("data/skins/skin_medium.json")), new Skin(Gdx.files.internal("data/skins/skin_small.json")));
		statusBar.setPosition(0, (int)mainStage.getHeight()-Config.gameHeightOffset);
		statusBar.setSize((int)mainStage.getWidth(), Config.gameHeightOffset/2);
		return statusBar;
	}
	
	private ButtonBar createButtonBar(){
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
				if(clicked.equals(imgButton[0]) && pause==false){
					SoundManager.INSTANCE.play(SoundType.CLICK);
					clickedPlayer=null;
					layerStage.clear();
					turn.nextTurn();
					updateStage();
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
		
		return buttonBar;
	}
	
	private void buildStage(){
		emptyTexture = new Texture(Gdx.files.internal("data/models/blank.png"));
		solidTexture = new Texture(Gdx.files.internal("data/models/solid.png"));
		ballTexture = new Texture(Gdx.files.internal("data/models/ball.png"));
		selectTexture = new Texture(Gdx.files.internal("data/models/select2.png"));
		markBallTexture = new Texture(Gdx.files.internal("data/models/select_ball.png"));
		
//		if(map.getMapType()==MapType.ASYMMETRIC){
//				PlayerPosition[] startPlayersToSwap = map.getStartPlayers();
//				PlayerPosition[] swappedPlayers = new PlayerPosition[startPlayersToSwap.length];
//				for(int i=0; i<startPlayersToSwap.length; i++){
//					PlayerType playerColor = startPlayersToSwap[i].getPlayerType();
//					int playerColorOrdinal = playerColor.ordinal();
//					playerColorOrdinal++;
//					if(playerColorOrdinal>turn.getAvailablePlayers().length-1){
//						playerColorOrdinal=0;
//					}
//					playerColor = PlayerType.values()[playerColorOrdinal];
//					swappedPlayers[i] = new PlayerPosition(startPlayersToSwap[i].getX(), startPlayersToSwap[i].getY(), playerColor);
//				}
//				map.setStartPlayers(swappedPlayers);
//			}
		
//		System.out.println("Current player(build stage):"+turn.getCurrentPlayer());
		if(map.getMapType()==MapType.ASYMMETRIC){
			while(true){
				if(map.getStartPlayers()[0].getPlayerType()==turn.getCurrentPlayer()){
					break;
				}
				System.out.println("Swap at the game:");
				PlayerPosition[] startPlayersToSwap = map.getStartPlayers();
				PlayerPosition[] swappedPlayers = new PlayerPosition[startPlayersToSwap.length];
				for(int i=0; i<startPlayersToSwap.length; i++){
					PlayerType playerColor = startPlayersToSwap[i].getPlayerType();
					int playerColorOrdinal = playerColor.ordinal();
					playerColorOrdinal++;
					if(playerColorOrdinal>turn.getAvailablePlayers().length-1){
						playerColorOrdinal=0;
					}
					playerColor = PlayerType.values()[playerColorOrdinal];
					swappedPlayers[i] = new PlayerPosition(startPlayersToSwap[i].getX(), startPlayersToSwap[i].getY(), playerColor);
				}
				map.setStartPlayers(swappedPlayers);
			}
		}
		
		board = createLogicBoard(boardWidth, boardHeight, map.getStartSolids(), map.getStartPlayers());
		statusBar.setLogicBoard(board);
		
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
		startMeasureAndSignalizePlayerMoveTime();
		System.out.println("macpmt after restart");
		measureAndControlPlayerMoveTime();
		
		if(Config.debug) System.out.println(" Red: "+board.getBallCount(PlayerType.RED)+" Blue: "+board.getBallCount(PlayerType.BLUE)+" Green: "+board.getBallCount(PlayerType.GREEN)+" Orange: "+board.getBallCount(PlayerType.ORANGE));
	}
	
	private void startCountingPlayerMoveTime(){
		if(!isPlayerMoveTimeLimitUnlimited()){
			playerMoveTime.start();
		}
	}
	
	private void updateStage(){
		
		flashCurrentPlayerBalls();
		
		//skips player turn when he is eliminated (when he has 0 balls) and when he has no opportunity to move anywhere
		if(board.getBallCount(turn.getCurrentPlayer()) == 0
				|| board.isPlayerMoveable(turn.getCurrentPlayer())==false){
			
			//game end condition
			boolean endGame = true;
			PlayerType[] availablePlayers = turn.getAvailablePlayers();
			for(PlayerType player:availablePlayers){
				if(player!=turn.getCurrentPlayer()){
					if(board.isPlayerMoveable(player)==true) endGame=false;
				}
			}
			
			//what program should do at the end
			if(endGame){
				//game Ends Man
				pause = true;
				
			    stopMeasureAndSignalizePlayerMoveTime();
			    layerStage.clear();
				
				PlayerType[] playerList = turn.getAvailablePlayers();
				int[] scoreList = new int[playerList.length];
				PlayerScore[] playerScore = new PlayerScore[playerList.length];
				for(int i=0; i<scoreList.length;i++){
					scoreList[i] = board.getBallCount(playerList[i]);
					playerScore[i] = new PlayerScore(playerList[i], scoreList[i]);
				}
				
				Arrays.sort(playerScore, new Comparator<PlayerScore>() {
					@Override
					public int compare(PlayerScore o1, PlayerScore o2) {
						if(o1.getPlayerScore()<o2.getPlayerScore()) return 1;
						else if(o1.getPlayerScore()>o2.getPlayerScore()) return -1;
						else return 0;
					}
				});
				
				boolean firstRound;
				if(turnStats==null){
					firstRound = true;
					try {
						turnStats = new TurnStats(playerScore);
					} catch (TurnStatsArgumentException e) {
						e.printStackTrace();
					}
				}
				else{
					firstRound = false;
					try {
						turnStats.saveTurnStats(playerScore);
					} catch (TurnStatsArgumentException e) {
						e.printStackTrace();
					}
				}
				
				Skin tSkin = new Skin(Gdx.files.internal("data/skins/skin_small.json"));
				final Table endTable = new Table(tSkin);
//				endTable.setBackground(tSkin.getDrawable("default-round-large"));
				endTable.setBackground(tSkin.getDrawable("message-round-large-bright"));
				endTable.setSize(screenWidth, 160);
				if(playerScore.length>3){
					endTable.setHeight(endTable.getHeight()+40);
				}
				endTable.setPosition(screenWidth/2-endTable.getWidth()/2, screenHeight/2-endTable.getHeight()/2);
				
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
				
				Label titleLabel1 = new Label("Pos", tSkin);
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
					tLabel3 = new Label(""+playerScore[i].getPlayerScore(), tSkin);
					if(firstRound==false){
						try {
							tLabel4 = new Label(""+turnStats.getPlayerGameBalls(playerScore[i].getPlayerType()), tSkin);
							tLabel5 = new Label(""+turnStats.getPlayerGameWons(playerScore[i].getPlayerType()), tSkin);
						} catch (TurnStatsArgumentException e) {
							e.printStackTrace();
						}
					}
					
					soundManager.play(SoundType.WIN);
					
					if(tLabel1!=null)endTable.add(tLabel1);
					if(tLabel2!=null)endTable.add(tLabel2);
					if(tLabel3!=null)endTable.add(tLabel3);
					if(tLabel4!=null)endTable.add(tLabel4);
					if(tLabel5!=null)endTable.add(tLabel5);
					endTable.row();
				}
				
				TextButton playAgainB = new TextButton("Play Again", tSkin, "dark");
				playAgainB.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						SoundManager.INSTANCE.play(SoundType.CLICK);
						
						board = createLogicBoard(boardWidth, boardHeight, map.getStartSolids(), map.getStartPlayers());
						
//						if(startingPlayer!=null) turn = new Turn(startPlayers, startingPlayer);
						PlayerType[] playerArray = turn.getAvailablePlayers();
						
						//just giving first move to next player
//						int currentStartPlayerNum = -1;
//						for(int i=0;i<playerArray.length;i++){
//							if(startingPlayer==playerArray[i]){
//								currentStartPlayerNum = i;
//								break;
//							}
//						}
//						
//						currentStartPlayerNum++;
//						if(currentStartPlayerNum>=playerArray.length) currentStartPlayerNum=0;

						//just giving first move to next player
						int currentStartPlayerNum = -1;
						for(int i=0;i<playerArray.length;i++){
							if(startPlayer.equals(playerArray[i])){
								currentStartPlayerNum = i;
								break;
							}
						}
							
						currentStartPlayerNum++;
						if(currentStartPlayerNum>=playerArray.length) currentStartPlayerNum=0;
						
						startPlayer = playerArray[currentStartPlayerNum];
						
//						System.out.println("Current player(in restart, from which it is being changed):"+turn.getCurrentPlayer());
						
//						startingPlayer = playerArray[currentStartPlayerNum];
						turn = new Turn(playerArray, startPlayer);
//						System.out.println("Current player(in restart, to which it is being changed):"+playerArray[currentStartPlayerNum]);
//						System.out.println("Current player(in restart, just after change):"+turn.getCurrentPlayer());
						
						fieldMap = new HashMap<Position,FieldActor2>();
						
						statusBar = createStatusBar();
						
						ButtonBar buttonBar = createButtonBar();
						
						buttonBar.setPosition((int)mainStage.getWidth()-(48+2)*3, (int)mainStage.getHeight()-Config.gameHeightOffset/2);
						buttonBar.setButtonSize(48, 46);
						buttonBar.setSpacing(2,2);
						
						pause = false;
						
						if(endTable!=null) endTable.remove();
						
						buildStage();
						
					}
				});
				endTable.add(playAgainB).colspan(2).minSize(Config.width*1/4-6, 25).space(6,3,6,3);
				
				TextButton endGameB = new TextButton("End Game", tSkin, "dark");
				endGameB.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y) {
						SoundManager.INSTANCE.play(SoundType.CLICK);
						 Gdx.app.postRunnable(new Runnable() {
				         public void run() {
				        	 startScreen.setToMainMenu();
				        	 startScreen.getAndroidNativeHandler().showBannerAds();
				         }});
//						pause = false;
					}
				});
				endTable.add(endGameB).colspan(2).minSize(Config.width*1/4-6, 25).space(6,3,6,3);
				endTable.add();
				
//				mainStage.addActor(endTable);
				messageStage.addActor(endTable);
				endTable.toFront();
			}
			//when it's not end but just current player is stuck for a round
			else{
				//if player skipped his turn manually
				clickedPlayer=null;
				turn.nextTurn();
				updateStage();
					
			}
			
		}
		
		if(board.isPlayerMoveable(turn.getCurrentPlayer())==true && AutoGameUtil.isOnlyOnePlayerMoveable(turn, board)){
//			System.out.println("SpecialNonPlayerGameEndSequenceState action");
			List<Position> thisPlayerBallPositions = AutoGameUtil.getThisPlayerBallPositions(turn, board);
			List<PossibleMove> possibleClones = new ArrayList<PossibleMove>();
			List<PossibleMove> possibleJumps = new ArrayList<PossibleMove>();
			possibleClones = AutoGameUtil.getPossibleMoves(BotMoveType.CLONE, thisPlayerBallPositions, board);
			possibleJumps = AutoGameUtil.getPossibleMoves(BotMoveType.JUMP, thisPlayerBallPositions, board);
			if(possibleClones.size()>0){
				//clone
				board.clone(possibleClones.get(0).getFrom().getX(), possibleClones.get(0).getFrom().getY(),
						possibleClones.get(0).getTo().getX(), possibleClones.get(0).getTo().getY());
				SoundManager.INSTANCE.play(SoundType.CLONE);
			}
			else if(possibleJumps.size()>0){
				//jump
				board.jump(possibleJumps.get(0).getFrom().getX(), possibleJumps.get(0).getFrom().getY(),
						possibleJumps.get(0).getTo().getX(), possibleJumps.get(0).getTo().getY());
				SoundManager.INSTANCE.play(SoundType.JUMP);
			}
			
			layerStage.clear();
			clickedPlayer=null;
			turn.nextTurn();
			refleshStageVisualPart();
			
			Timer timer = new Timer();
			
			TimerTask timerTask = new TimerTask() {
				
				@Override
				public void run() {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							updateStage();
						}
					});
				}
			};
			
			timer.schedule(timerTask, Config.autoendOneMoveTimeMs);
			
		}
		
		refleshStageVisualPart();
		
		measureAndControlPlayerMoveTime();
		
		if(Config.debug) System.out.println(" Red: "+board.getBallCount(PlayerType.RED)+" Blue: "+board.getBallCount(PlayerType.BLUE)+" Green: "+board.getBallCount(PlayerType.GREEN)+" Orange: "+board.getBallCount(PlayerType.ORANGE));
	}
	
	private void refleshStageVisualPart(){
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
	}
	
	private void measureAndControlPlayerMoveTime(){
		if(!isPlayerMoveTimeLimitUnlimited()){
			playerMoveTime.start();
			stopCheckingPlayerMoveTime();
			startCheckingPlayerMoveTime();
		}
	}
	
	private void startCheckingPlayerMoveTime(){
		if(pause==false){
			checkingPlayerMoveTimeTask = new TimerTask() {
				@Override
				public void run() {
					float playerMoveTimeNumber = playerMoveTime.getMeasuredTimeInMs()/1000f;
			    	if(playerMoveTimeNumber>playerMoveTimeLimitType.getPlayerMoveTimeLimit().getLimitInSeconds()){
			    		skipTurn();
			    		updateStage();
			    	}
				}
			};

			checkingPlayerMoveTimeTimer = new Timer(false);
			checkingPlayerMoveTimeTimer.schedule(checkingPlayerMoveTimeTask,
					playerMoveTimeLimitType.getPlayerMoveTimeLimit().getLimitInSeconds()*SECONDS_TO_MILLISECONDS_MULTIPLER+ENSURE_MS);
		}
	}
	
	private void stopCheckingPlayerMoveTime(){
		if(checkingPlayerMoveTimeTimer!=null){
			checkingPlayerMoveTimeTimer.cancel();
			checkingPlayerMoveTimeTimer.purge();
		}
		else{
		}
	}
	
	private synchronized void skipTurn(){
		turn.nextTurn();
		updateStage();
	}
	
	public Stage getMainStage(){
		return mainStage;
	}
	
	public Stage getLayerStage(){
		return layerStage;
	}
	
	public Stage getMessageStage() {
		return messageStage;
	}

	private class FieldListener extends ClickListener{

		@Override
		public void clicked(InputEvent event, float x, float y) {
			super.clicked(event, x, y);
			FieldActor2 field = (FieldActor2) event.getListenerActor();
			Position pos = field.getPosition();
			PlayerType fieldPlayer = board.getFieldPlayer(pos.getX(), pos.getY());
			if(board.getFieldType(pos.getX(), pos.getY()) == FieldType.TAKEN && fieldPlayer == turn.getCurrentPlayer()){
				clickedPlayer = new PlayerPosition(pos.getX(), pos.getY(), fieldPlayer);
				
				if(clickedPlayer.getPlayerType() == turn.getCurrentPlayer()){
					highlightPossibleMoveFields(clickedPlayer);
				}
			}
			else if(board.getFieldType(pos.getX(), pos.getY()) == FieldType.EMPTY){
				if(clickedPlayer!=null){
					if(board.isMovePossible(clickedPlayer.getX(), clickedPlayer.getY(), pos.getX(), pos.getY())){
						if(board.isClonePossible(clickedPlayer.getX(), clickedPlayer.getY(), pos.getX(), pos.getY())){
							board.clone(clickedPlayer.getX(), clickedPlayer.getY(), pos.getX(), pos.getY());
							soundManager.play(SoundType.CLONE);
						}
						if(board.isJumpPossible(clickedPlayer.getX(), clickedPlayer.getY(), pos.getX(), pos.getY())){
							board.jump(clickedPlayer.getX(), clickedPlayer.getY(), pos.getX(), pos.getY());
							soundManager.play(SoundType.JUMP);
						}
						
						layerStage.clear();
						clickedPlayer=null;
						turn.nextTurn();
						updateStage();
					}
				}
			}
		}

	}
	
	private void highlightPossibleMoveFields(PlayerPosition chosenPlayerPosition){
		if(chosenPlayerPosition.getPlayerType() == turn.getCurrentPlayer()){
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
		if(currentPlayer != turn.getCurrentPlayer()){
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
			Color highlightColor = new Color(flashColor.r, flashColor.g, flashColor.b, 1.0f);
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
						board.getFieldPlayer(i, j)==turn.getCurrentPlayer();
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
}
