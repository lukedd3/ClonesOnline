package com.luke.backstage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.luke.clones.autogame.AutoGameUtil;
import com.luke.clones.bot.BotMoveType;
import com.luke.clones.bot.PossibleMove;
import com.luke.clones.map.model.MapModelUtils;
import com.luke.clones.map.model.MapType;
import com.luke.clones.model.LogicBoard;
import com.luke.clones.model.Map;
import com.luke.clones.model.MapAutoCreator;
import com.luke.clones.model.MapAutoCreator.MapCreatorArgumentException;
import com.luke.clones.model.PlayerScore;
import com.luke.clones.model.SingleStat.TurnStatsArgumentException;
import com.luke.clones.model.TimeMeasure;
import com.luke.clones.model.TurnStats;
import com.luke.clones.model.type.FieldType;
import com.luke.clones.model.type.PlayerPosition;
import com.luke.clones.model.type.PlayerType;
import com.luke.clones.model.type.Position;
import com.luke.clones.model.type.Turn;
import com.luke.clones.network.communication.ActionTypeBoard;
import com.luke.clones.network.communication.ActionTypeMove;
import com.luke.clones.network.communication.GameOver;
import com.luke.clones.network.communication.GameStart;
import com.luke.clones.network.communication.MoveBoard;
import com.luke.clones.network.communication.PlayerMoveTimeLimit;
import com.luke.clones.network.communication.RoomCreateInfo;
import com.luke.clones.network.communication.RoomInfo;
import com.luke.clones.network.communication.Token;
import com.luke.clones.network.communication.UpdateBoard;
import com.luke.config.Config;
import com.luke.service.StatsAddService;
import com.luke.type.GameResult;
import com.luke.type.GameType;
import com.luke.type.SuddenlyInterruptedGameResult;
import com.luke.type.WinLoseType;
import com.luke.util.MapUtil;
import com.luke.util.StatsUtil;

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

public class Room {
	private static final int ENSURE_MS = 2;
	private static final int SECONDS_TO_MILLISECONDS_MULTIPLER = 1000;
	
	Logger log = Logger.getLogger(Room.class);
	
	private StatsAddService statsAddService = StatsAddService.INSTANCE;
	
	private HashMap<Player,PlayerType> playerMap;
	private Player admin;
	
	private int id;
	private String name;
	private int playerLimit;
	/**
	 * Used to create map using autoCreator
	 */
	private RoomCreateInfo roomCreateInfo;
	
	private boolean pause;
	private TurnStats turnStats;
	
	private Map map;
	private LogicBoard board;
	private volatile Turn turn;
	
	private Player startPlayer;
	
	private volatile PlayerMoveTimeLimit playerMoveTimeLimit;
	
	private volatile TimeMeasure playerMoveTime;
	private TimerTask checkingPlayerMoveTimeTask;
	private Timer checkingPlayerMoveTimeTimer;
	
	private volatile TimeMeasure nextRoundAutoStartTimeMeasure;
	private Timer nextRoundAutoStartTimer;
	
	private TimeMeasure playTime;
	
	private GameRound gameRound;
	
	public Room(String name, Player admin){
		this.name = name;
		this.id = IdGenerator.getGeneratedId();
		
		playerMap = new LinkedHashMap<Player, PlayerType>();
		
		this.admin = admin;
		addPlayer(admin);
		
		pause = true;
		turnStats=null;
		gameRound = new GameRound();
	}
	
	/**
	 * Starts new game using map info that is held in roomCreateInfo
	 * @param map
	 * @throws MapCreatorArgumentException 
	 */
	public void startNewGame() throws MapCreatorArgumentException{
		Map map = MapAutoCreator.createMap(MapModelUtils.resolveNameToMap(roomCreateInfo.getMapName()));
		startNewGame(map);
	}
	/**
	 * Restarts game using map info that is held in roomCreateInfo
	 * @param map
	 * @throws MapCreatorArgumentException 
	 */
//	public void restartGame() throws MapCreatorArgumentException{
//		Map map = MapAutoCreator.createMap(MapModelUtils.resolveNameToMap(roomCreateInfo.getMapName())); //old
//		restartGame();
//		restartGame(map); //old
//	}
	/**
	 * Starts new game using map given from outside
	 * @param map
	 */
	public void startNewGame(Map map){
		init();
		
		this.map = map;
		
		pause=false;
		turnStats=null;
//		playerMoveTimeCheckerThreadWorking=false;
		gameRound.reset();
		
		Player[] playerArray = getAllPlayers();
		//just giving first move to random player
//		if(map.getMapType()==MapType.SYMMETRIC){
		Random random = new Random();
		startPlayer = playerArray[random.nextInt(playerArray.length)];
//		}
//		else if(map.getMapType()==MapType.ASYMMETRIC){
//			int redPlayerNumber;
//			for(int i=0; i<playerArray.length; i++){
//				if(playerArray[i].get)
//			}
//			startPlayer = playerArray[0];
//		}
		Token token = new Token(getPlayerType(startPlayer));
		
		PlayerType[] playerTypeOrder = Arrays.copyOf(PlayerOrder.getPlayerOrderArray(), getNumberOfPlayers());
		
		turn = new Turn(playerTypeOrder, token.getPlayerType());
		
		log.debug("playerArray:");
		for(Player player: playerArray){
			log.debug(""+getPlayerType(player));
		}
		
		log.debug("startPlayersFromMap:");
		for(PlayerPosition player: map.getStartPlayers()){
			log.debug(""+player.getPlayerType());
		}
		
		board = new LogicBoard(map.getWidth(),map.getHeight());
		
		Position[] startSolids = map.getStartSolids();
		if(startSolids != null){
			for(int i=0; i<startSolids.length; i++){
				board.setFieldTypeSolid(startSolids[i].getX(), startSolids[i].getY());
			}
		}
		
		if(map.getMapType()==MapType.ASYMMETRIC){
			while(true){
				if(map.getStartPlayers()[0].getPlayerType()==turn.getCurrentPlayer()){
					break;
				}
				log.debug("swap loop in start");
				PlayerPosition[] startPlayersToSwap = map.getStartPlayers();
				PlayerPosition[] swappedPlayers = new PlayerPosition[startPlayersToSwap.length];
				for(int i=0; i<startPlayersToSwap.length; i++){
					PlayerType playerColor = startPlayersToSwap[i].getPlayerType();
					int playerColorOrdinal = playerColor.ordinal();
					playerColorOrdinal++;
					if(playerColorOrdinal>playerLimit-1){
						playerColorOrdinal=0;
					}
					playerColor = PlayerType.values()[playerColorOrdinal];
					swappedPlayers[i] = new PlayerPosition(startPlayersToSwap[i].getX(), startPlayersToSwap[i].getY(), playerColor);
				}
				map.setStartPlayers(swappedPlayers);
			}
		}
		
		PlayerPosition[] startPlayers = map.getStartPlayers();
		for(int i=0; i<startPlayers.length; i++){
			board.setFieldPlayer(startPlayers[i].getX(), startPlayers[i].getY(), startPlayers[i].getPlayerType());
		}
		
		String[] playerNames = getRoomInfo().getPlayerNames();
		
		log.debug("Player names in room info server side:");
		for(String pName: playerNames){
			log.debug("- "+pName);
		}
		
		for(int i=0; i<playerArray.length;i++){
			Config.log("Sending game start");
			playerArray[i].getConnection().sendTCP(new GameStart(map, getPlayerType(playerArray[i]), token, getRoomInfo(),playerTypeOrder));
		
			statsAddService.addStartedGame(playerArray[i].getGoogleUserData().getId(), StatsUtil.maxNumberOfPlayersToGameType(playerLimit));
		}
		
		playTime.start();
		
		measureAndControlPlayerMoveTime();
		
	}
	
	private void init(){
		playerMoveTime = new TimeMeasure();
		playTime = new TimeMeasure();
		nextRoundAutoStartTimeMeasure = new TimeMeasure();
	}
	
	public void restartGame() throws MapCreatorArgumentException{
//	public void restartGame(Map map){
//		this.map = map;
		
		pause=false;
		stopCheckingNextRoundAutoStartTime();
		
		Player[] playerArray = getAllPlayers();
		

		//just giving first move to next player
		int currentStartPlayerNum = -1;
		for(int i=0;i<playerArray.length;i++){
			if(startPlayer.getName().equals(playerArray[i].getName())){
				currentStartPlayerNum = i;
				break;
			}
		}
			
		currentStartPlayerNum++;
		if(currentStartPlayerNum>=playerArray.length) currentStartPlayerNum=0;
		
		startPlayer = playerArray[currentStartPlayerNum];
		
		Token token = new Token(getPlayerType(startPlayer));
		
		PlayerType[] playerTypeOrder = Arrays.copyOf(PlayerOrder.getPlayerOrderArray(), getNumberOfPlayers());
		
		turn = new Turn(playerTypeOrder, token.getPlayerType());
		
		
		board = new LogicBoard(map.getWidth(),map.getHeight());
		
		Position[] startSolids = map.getStartSolids();
		if(startSolids != null){
			for(int i=0; i<startSolids.length; i++){
				board.setFieldTypeSolid(startSolids[i].getX(), startSolids[i].getY());
			}
		}
		
		if(map.getMapType()==MapType.ASYMMETRIC){
//		if(map.getMapType()==MapType.ASYMMETRIC && gameRound.isEven()==true){
			log.debug("swap in restart");
			PlayerPosition[] startPlayersToSwap = map.getStartPlayers();
			PlayerPosition[] swappedPlayers = new PlayerPosition[startPlayersToSwap.length];
			for(int i=0; i<startPlayersToSwap.length; i++){
				PlayerType playerColor = startPlayersToSwap[i].getPlayerType();
				int playerColorOrdinal = playerColor.ordinal();
				playerColorOrdinal++;
				if(playerColorOrdinal>playerLimit-1){
					playerColorOrdinal=0;
				}
				playerColor = PlayerType.values()[playerColorOrdinal];
				swappedPlayers[i] = new PlayerPosition(startPlayersToSwap[i].getX(), startPlayersToSwap[i].getY(), playerColor);
			}
			map.setStartPlayers(swappedPlayers);
		}
		
		PlayerPosition[] startPlayers = map.getStartPlayers();
		for(int i=0; i<startPlayers.length; i++){
			board.setFieldPlayer(startPlayers[i].getX(), startPlayers[i].getY(), startPlayers[i].getPlayerType());
		}
		
		for(int i=0; i<playerArray.length;i++){
			Config.log("Sending game start");
			playerArray[i].getConnection().sendTCP(new GameStart(map, getPlayerType(playerArray[i]), token, getRoomInfo(),playerTypeOrder));
		
			statsAddService.addStartedGame(playerArray[i].getGoogleUserData().getId(), StatsUtil.maxNumberOfPlayersToGameType(playerLimit));
		}
		
		playTime.start();
		
		measureAndControlPlayerMoveTime();
		
	}
	
	public void playerMove(MoveBoard playerMove, PlayerType senderPlayerType){
		PlayerPosition playerPos = playerMove.getPlayerPosition();
		Position movePos = playerMove.getPosition();
		Player[] playerArray = getAllPlayers();
		
		Config.log("CurrentPlayer:"+turn.getCurrentPlayer()+" Sender:"+senderPlayerType);
		
		if(turn.getCurrentPlayer() == senderPlayerType){
			
			if(playerMove.getActionType()==ActionTypeMove.MOVE){
				PlayerType fieldPlayer = board.getFieldPlayer(playerPos.getX(), playerPos.getY());
				if(board.getFieldType(playerPos.getX(), playerPos.getY()) == FieldType.TAKEN && fieldPlayer == turn.getCurrentPlayer()){
					if(board.getFieldType(movePos.getX(), movePos.getY()) == FieldType.EMPTY){
						if(board.isMovePossible(playerPos.getX(), playerPos.getY(), movePos.getX(), movePos.getY())){
							UpdateBoard updateBoard = null;
							
							if(board.isClonePossible(playerPos.getX(), playerPos.getY(), movePos.getX(), movePos.getY())){
								board.clone(playerPos.getX(), playerPos.getY(), movePos.getX(), movePos.getY());
								updateBoard = new UpdateBoard(ActionTypeBoard.CLONE,playerPos,movePos);
							}
							else if(board.isJumpPossible(playerPos.getX(), playerPos.getY(), movePos.getX(), movePos.getY())){
								board.jump(playerPos.getX(), playerPos.getY(), movePos.getX(), movePos.getY());
								updateBoard = new UpdateBoard(ActionTypeBoard.JUMP,playerPos,movePos);
							}
							turn.nextTurn();
							//sendBoardUpdateToEveryPlayerInTheRoom
							sendToAllPlayers(updateBoard);
						}
					}
				}
			}
			else if(playerMove.getActionType()==ActionTypeMove.SKIP_TURN){
				skipTurn();
			}
			
		updateStage();
		}
	}
	
	public void updateStage(){
		//skips player turn when he is eliminated (when he has 0 balls) and when he has no opportunity to move anywhere
		log.debug("Current player color: "+turn.getCurrentPlayer()+
				";number of his balls:"+board.getBallCount(turn.getCurrentPlayer())+
				";is he moveable?:"+board.isPlayerMoveable(turn.getCurrentPlayer()));
		
		if(board.getBallCount(turn.getCurrentPlayer()) == 0 || board.isPlayerMoveable(turn.getCurrentPlayer())==false){

			//game end condition
			boolean endGame = true;
			PlayerType[] availablePlayers = turn.getAvailablePlayers();
			for(PlayerType player:availablePlayers){
				if(player!=turn.getCurrentPlayer()){
					if(board.isPlayerMoveable(player)==true) endGame=false;
				}
			}
			
			//what should be done at the end
			//game Ends
			if(endGame){
				pause = true;
				playTime.stop();
				stopCheckingPlayerMoveTime();
				gameRound.nextRound();
				
				PlayerType[] playerList = turn.getAvailablePlayers();
				int[] scoreList = new int[playerList.length];
				PlayerScore[] playerScores = new PlayerScore[playerList.length];
				for(int i=0; i<scoreList.length;i++){
					scoreList[i] = board.getBallCount(playerList[i]);
					playerScores[i] = new PlayerScore(playerList[i], scoreList[i]);
				}
				
				Arrays.sort(playerScores, new Comparator<PlayerScore>() {
					@Override
					public int compare(PlayerScore o1, PlayerScore o2) {
						if(o1.getPlayerScore()<o2.getPlayerScore()) return 1;
						else if(o1.getPlayerScore()>o2.getPlayerScore()) return -1;
						else return 0;
					}
				});
				
				boolean firstRound;
				if(getTurnStats()==null){
					firstRound = true;
					try {
						setTurnStats(new TurnStats(playerScores));
					} catch (TurnStatsArgumentException e) {
						e.printStackTrace();
					}
				}
				else{
					firstRound = false;
					try {
						getTurnStats().saveTurnStats(playerScores);
					} catch (TurnStatsArgumentException e) {
						e.printStackTrace();
					}
				}
				
				log.debug("bef addFinishedGameResultToStats(playerScores) PlayerScores: "+playerScores);

				addFinishedGameResultToStats(playerScores);
				
				log.debug("aft addFinishedGameResultToStats(playerScores) PlayerScores: "+playerScores);
				
				GameOver gameOver = new GameOver(playerScores, getTurnStats(), firstRound);
				
				log.debug("aft game over create PlayerScores: "+playerScores);
				
				sendToAllPlayers(gameOver);
				
				log.debug("aft game over send PlayerScores: "+playerScores);
				
				measureAndControlNextRoundAutoStart();
				
				log.debug("aft measureAndControlNextRoundAutoStart()");
			}
			
			//when it's not end but just current player is stuck for a round
			else{
				log.debug("skipping turn for player "+turn.getCurrentPlayer());
				skipTurn();
			}
			
		}
		
//		if(board.isPlayerMoveable(turn.getCurrentPlayer())==true){
//			log.debug("if(board.isPlayerMoveable(turn.getCurrentPlayer())==true){");
//		}
		
		if(board.isPlayerMoveable(turn.getCurrentPlayer())==true && AutoGameUtil.isOnlyOnePlayerMoveable(turn, board)){
			log.debug("if(board.isPlayerMoveable(turn.getCurrentPlayer())==true && isOnlyOnePlayerMoveable()){");
			List<Position> thisPlayerBallPositions = AutoGameUtil.getThisPlayerBallPositions(turn, board);
			List<PossibleMove> possibleClones = new ArrayList<PossibleMove>();
			List<PossibleMove> possibleJumps = new ArrayList<PossibleMove>();
			possibleClones = AutoGameUtil.getPossibleMoves(BotMoveType.CLONE, thisPlayerBallPositions, board);
			possibleJumps = AutoGameUtil.getPossibleMoves(BotMoveType.JUMP, thisPlayerBallPositions, board);
			if(possibleClones.size()>0){
				//clone
				PlayerPosition playerPos = new PlayerPosition(possibleClones.get(0).getFrom().getX(),
						possibleClones.get(0).getFrom().getY(),turn.getCurrentPlayer());
				Position movePos = new Position(possibleClones.get(0).getTo().getX(), possibleClones.get(0).getTo().getY());
				
				board.clone(playerPos.getX(), playerPos.getY(),
						movePos.getX(), movePos.getY());
				
				turn.nextTurn();
				
				UpdateBoard updateBoard = new UpdateBoard(ActionTypeBoard.CLONE,playerPos,movePos);
				sendToAllPlayers(updateBoard);
			}
			else if(possibleJumps.size()>0){
				//jump
				PlayerPosition playerPos = new PlayerPosition(possibleJumps.get(0).getFrom().getX(),
						possibleJumps.get(0).getFrom().getY(),turn.getCurrentPlayer());
				Position movePos = new Position(possibleJumps.get(0).getTo().getX(), possibleJumps.get(0).getTo().getY());
				
				board.jump(playerPos.getX(), playerPos.getY(),
						movePos.getX(), movePos.getY());
				
				turn.nextTurn();
				
				UpdateBoard updateBoard = new UpdateBoard(ActionTypeBoard.JUMP,playerPos,movePos);
				sendToAllPlayers(updateBoard);
			}

			Timer timer = new Timer();
			
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					updateStage();
				}
			};
			
			timer.schedule(timerTask, com.luke.clones.config.Config.autoendOneMoveTimeMs);
		}
		
		measureAndControlPlayerMoveTime();
		
	}
	
	private synchronized void skipTurn(){
		log.debug("Skip turn by player: "+turn.getCurrentPlayer()+" in room: "+name);
		turn.nextTurn();
		UpdateBoard updateBoard = new UpdateBoard(ActionTypeBoard.NEXT_TURN,null,null);
		sendToAllPlayers(updateBoard);
		updateStage();//
	}
	
	private void measureAndControlPlayerMoveTime(){
		if(!playerMoveTimeLimit.isUnlimited()){
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
					log.debug("Time: "+playerMoveTimeNumber);
			    	if(playerMoveTimeNumber>playerMoveTimeLimit.getLimitInSeconds()){
			    		log.debug("Skipping turn because of elapsed time, time: "+playerMoveTimeNumber);
			    		skipTurn();
			    		updateStage();
			    	}
				}
			};

			checkingPlayerMoveTimeTimer = new Timer(false);
			checkingPlayerMoveTimeTimer.schedule(checkingPlayerMoveTimeTask,
					playerMoveTimeLimit.getLimitInSeconds()*SECONDS_TO_MILLISECONDS_MULTIPLER+ENSURE_MS);
		}
	}
	
	private void stopCheckingPlayerMoveTime(){
		if(checkingPlayerMoveTimeTimer!=null){
			log.debug("checkingPlayerMoveTimeTimer stopped");
			checkingPlayerMoveTimeTimer.cancel();
			checkingPlayerMoveTimeTimer.purge();
		}
		else{
			log.debug("checkingPlayerMoveTimeTimer is null so it can't stopped");
		}
	}
	
	private synchronized void measureAndControlNextRoundAutoStart(){
		nextRoundAutoStartTimeMeasure.start();
		nextRoundAutoStartTimer = new Timer(false);
		TimerTask hideMessageTask = new TimerTask() {
			@Override
			public void run() {
				if(nextRoundAutoStartTimeMeasure.getMeasuredTimeInMs()>=
						com.luke.clones.config.ConfigUtils.getNextRoundAutoStartTimeDependingOnNumberOfPlayers(playerLimit)*1000){
					stopCheckingNextRoundAutoStartTime();
					if(getNumberOfPlayers()==getPlayerLimit()){
						try {
							restartGame();
						} catch (MapCreatorArgumentException e) {
							log.error("", e);
						}
					}
				}
			}
		};
		
		nextRoundAutoStartTimer.schedule(hideMessageTask,
				com.luke.clones.config.ConfigUtils.getNextRoundAutoStartTimeDependingOnNumberOfPlayers(playerLimit)
				*SECONDS_TO_MILLISECONDS_MULTIPLER+ENSURE_MS);
	}
	
	private void stopCheckingNextRoundAutoStartTime(){
		if(nextRoundAutoStartTimer!=null){
			nextRoundAutoStartTimer.cancel();
			nextRoundAutoStartTimer.purge();
			log.debug("checkingNextRoundAutoStartTime stopped");
		}
	}
	
	public void addPlayer(Player player){
		List<PlayerType> notUsedPlayerColors = createNotUsedPlayerColorsList();
		if(notUsedPlayerColors.size()>0){
			playerMap.put(player, notUsedPlayerColors.get(0));
			log.info("Player \""+player.getName()+"\" added as "+getPlayerType(player)+" to room "+name);
		}
		else{
			log.info("Player \""+player.getName()+"\" can't be added to room "+name+" reason: all places are taken");
		}
	}
	
	private List<PlayerType> createNotUsedPlayerColorsList(){
		List<PlayerType> notUsedPlayerColors = new ArrayList<PlayerType>(Arrays.asList(PlayerOrder.getPlayerOrderArray()));
		for(PlayerType playerType: playerMap.values()){
			if(notUsedPlayerColors.contains(playerType)){
				notUsedPlayerColors.remove(playerType);
			}
		}
		return notUsedPlayerColors;
	}
	
	public void removePlayer(Connection connection){
		Player player = null;
		Player[] playerArray = getAllPlayers();
		for(Player tempPlayer : playerArray){
				if(tempPlayer.getConnection().equals(connection)){
					player = tempPlayer;
					break;
				}
		}
		if(player.getName().equals(admin.getName())) admin = null;
		removePlayer(player);
	}
	
	public void removePlayer(Player player){
		if(admin!=null && player.getName().equals(admin.getName())) admin = null;
		playerMap.remove(player);
		gameInterruptedActionInRemovePlayer(player);
	}
	
	private void gameInterruptedActionInRemovePlayer(Player removedPlayer){
		if(pause==false){
			addSuddenlyInterruptedGameResultToStats(removedPlayer);
			pause = true;
		}
	}
	
	public PlayerType getPlayerType(Player player){
		return playerMap.get(player);
	}
	
	public Player getPlayer(PlayerType playerType){
		for(Entry<Player, PlayerType> playerEntry: playerMap.entrySet()){
			if(playerType == playerEntry.getValue()){
				return playerEntry.getKey();
			}
		}
		return null;
	}
	
	public void sendToAllPlayers(Object obj){
		Player[] players = getAllPlayers();
		for(Player player: players){
			player.getConnection().sendTCP(obj);
		}
	}
	
	public void sendToAllPlayersExceptSender(Object obj, Player sender){
		Player[] players = getAllPlayers();
		for(Player player: players){
			if(player.getName()!=sender.getName()) player.getConnection().sendTCP(obj);
		}
	}
	
	public Player[] getAllPlayers(){
		return playerMap.keySet().toArray(new Player[0]);
	}
	
	public int getNumberOfPlayers(){
		return playerMap.size();
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		log.debug("in setPause");
		if(this.pause!=pause){
			if(pause==true){
				log.debug("pause==true");
				stopCheckingPlayerMoveTime();
//				addSuddenlyInterruptedGameResultToStats(null);
			}
			this.pause = pause;
		}
		if(pause==true){
			stopCheckingNextRoundAutoStartTime();
		}
	}

	public TurnStats getTurnStats() {
		return turnStats;
	}

	public void setTurnStats(TurnStats turnStats) {
		this.turnStats = turnStats;
	}
	
	public String getName(){
		return name;
	}
	
	public int getId(){
		return id;
	}
	
	public RoomInfo getRoomInfo(){
		int roomId = id;
		String roomName = name;
		
		Player[] playerArray = getAllPlayers();
		
		String[] playerNames = new String[playerArray.length];
		for(int i=0;i<playerArray.length;i++){
			playerNames[i] = playerArray[i].getName();
		}
		
		return new RoomInfo(roomId, roomName, playerNames, playerLimit, roomCreateInfo.getPlayerMoveTimeLimit(), admin.getName(), roomCreateInfo.getMapName());
	}
	
	public void setRoomCreateInfo(RoomCreateInfo roomCreateInfo){
		this.roomCreateInfo = roomCreateInfo;
		this.name = roomCreateInfo.getRoomName();
		this.playerLimit = roomCreateInfo.getPlayerNumber();
		this.playerMoveTimeLimit = roomCreateInfo.getPlayerMoveTimeLimit();
	}
	
	public RoomCreateInfo getRoomCreateInfo() {
		return roomCreateInfo;
	}
	
	public int getPlayerLimit(){
		return playerLimit;
	}
	
//	public void setAdmin(Player admin){
//		this.admin = admin;
//	}
	
	public void markCurrentPlayerAsAdmin(Player player){
//		this.removePlayer(player);
//		addAdminPlayer(player);
		this.admin = player;
	}
	
	public Player getAdmin(){
		return admin;
	}
	
	public PlayerMoveTimeLimit getPlayerMoveTimeLimit() {
		return playerMoveTimeLimit;
	}

	public void removeAllPlayers(){
		playerMap = new HashMap<Player, PlayerType>();
		this.admin = null;
	}

	private void addFinishedGameResultToStats(PlayerScore[] playerScores){
		log.debug("addFinishedGameResultToStats start");
		long playTimeInSeconds = Math.round(Math.ceil(playTime.getMeasuredTimeInMs()/1000f));
		//clean play time there
		for(int i=0; i<playerScores.length;i++){
			Player tPlayer = MapUtil.getKeyByValue(playerMap, playerScores[i].getPlayerType());
			log.debug("player: "+tPlayer);
			String playerGoogleId = tPlayer.getGoogleUserData().getId();
			
			log.debug("player id: "+playerGoogleId);
			
			//remember gameType
			GameType gameType = StatsUtil.maxNumberOfPlayersToGameType(playerLimit);
			
			WinLoseType winLose;
			if(i==0){
				winLose=WinLoseType.WIN;
			}
			else{
				winLose=WinLoseType.LOSE;
			}
			
			int ballsScored = playerScores[i].getPlayerScore();
			
			GameResult gameResult = new GameResult();
			gameResult.setWinLose(winLose);
			gameResult.setBallsScored(ballsScored);
			gameResult.setPlayTimeInSeconds(playTimeInSeconds);
			
			statsAddService.addFinishedGameResult(playerGoogleId, gameType, gameResult);
		}
		log.debug("addFinishedGameResultToStats end");
	}
	
	private void addSuddenlyInterruptedGameResultToStats(Player removedPlayer){
		Set<Player> tPlayers = new HashSet<Player>(playerMap.keySet());
		if(removedPlayer!=null) tPlayers.add(removedPlayer);
		long playTimeInSeconds = Math.round(Math.ceil(playTime.getMeasuredTimeInMs()/1000f));
		//clean playTime there
		for(Player tPlayer: tPlayers){
			String playerGoogleId = tPlayer.getGoogleUserData().getId();
			GameType gameType = StatsUtil.maxNumberOfPlayersToGameType(playerLimit);
			SuddenlyInterruptedGameResult suddenlyInterruptedGameResult = new SuddenlyInterruptedGameResult();
			suddenlyInterruptedGameResult.setPlayTimeInSeconds(playTimeInSeconds);
			statsAddService.addSuddenlyInterruptedGameResult(playerGoogleId, gameType, suddenlyInterruptedGameResult);
		}
	}
}
