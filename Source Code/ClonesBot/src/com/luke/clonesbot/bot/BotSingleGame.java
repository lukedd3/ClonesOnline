package com.luke.clonesbot.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.RandomUtils;

import com.esotericsoftware.kryonet.Connection;
import com.luke.clones.bot.BotMoveType;
import com.luke.clones.bot.PossibleMove;
import com.luke.clones.model.LogicBoard;
import com.luke.clones.model.type.PlayerPosition;
import com.luke.clones.model.type.PlayerType;
import com.luke.clones.model.type.Position;
import com.luke.clones.model.type.Turn;
import com.luke.clones.network.communication.ActionTypeBoard;
import com.luke.clones.network.communication.ActionTypeMove;
import com.luke.clones.network.communication.ActionTypeMoveRoom;
import com.luke.clones.network.communication.ActionTypeUpdateRoom;
import com.luke.clones.network.communication.GameOver;
import com.luke.clones.network.communication.GameStart;
import com.luke.clones.network.communication.MoveBoard;
import com.luke.clones.network.communication.MoveRoom;
import com.luke.clones.network.communication.UpdateBoard;
import com.luke.clones.network.communication.UpdateRoom;
import com.luke.clones.network.config.NetworkConfig;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.network.controller.ClonesClient;

public class BotSingleGame implements ListenerService{
	
	private BotRoomWaiting botRoomWaiting;
	private ClonesClient clonesClient;
	private GameStart gameStart;
	
	private LogicBoard board;
	private Turn turn;
	private PlayerType thisPlayer;
	
	private Position thisPlayerFirstPosition;
	private Position opponentFirstPosition;
	
	private List<PossibleMove> possibleClones = new ArrayList<PossibleMove>();
	private List<PossibleMove> possibleJumps = new ArrayList<PossibleMove>();
	
	private List<PossibleMove> clonesTowardOpponent = new ArrayList<PossibleMove>();
	private List<PossibleMove> jumpsTowardOpponent = new ArrayList<PossibleMove>();
	
	private List<PossibleMove> clonesPartlyTowardOpponent = new ArrayList<PossibleMove>();
	private List<PossibleMove> jumpsPartlyTowardOpponent = new ArrayList<PossibleMove>();
	
	TimerTask moveTimerTask;
	Timer moveTimer = new Timer();
	
	protected BotSingleGame(BotRoomWaiting botRoomWaiting, ClonesClient clonesClient, GameStart gameStart) {
		newSingleGame(botRoomWaiting, clonesClient, gameStart);
	}
	
	protected void newSingleGame(BotRoomWaiting botRoomWaiting, ClonesClient clonesClient, GameStart gameStart) {
		this.botRoomWaiting = botRoomWaiting;
		this.clonesClient = clonesClient;
		this.gameStart = gameStart;
		
		thisPlayer = gameStart.getPlayerColor();
		
		board = new LogicBoard(gameStart.getMap().getWidth(), gameStart.getMap().getHeight());
		
		if(gameStart.getMap().getStartSolids() != null){
			for(int i=0; i<gameStart.getMap().getStartSolids().length; i++){
				board.setFieldTypeSolid(gameStart.getMap().getStartSolids()[i].getX(), gameStart.getMap().getStartSolids()[i].getY());
			}
		}
		
		for(int i=0; i<gameStart.getMap().getStartPlayers().length; i++){
			board.setFieldPlayer(gameStart.getMap().getStartPlayers()[i].getX(), gameStart.getMap().getStartPlayers()[i].getY(), gameStart.getMap().getStartPlayers()[i].getPlayerType());
		}
		PlayerType startingPlayer = gameStart.getToken().getPlayerType();
		if(startingPlayer!=null) turn = new Turn(gameStart.getPlayerTypeOrder(), startingPlayer);
		
		thisPlayerFirstPosition = getThisPlayerBallPositions().get(0);
		opponentFirstPosition = getOpponentBallPositions().get(0);
		
		move();
	}
	
	//equivalent of updateStage()
	private void move(){
		if(turn.getCurrentPlayer()==thisPlayer){
			System.out.println("move move mock");
			List<Position> thisPlayerBallPositions = getThisPlayerBallPositions();
			List<Position> opponentBallPositions = getOpponentBallPositions();
			
			possibleClones = getPossibleMoves(BotMoveType.CLONE, thisPlayerBallPositions);
			possibleJumps = getPossibleMoves(BotMoveType.JUMP, thisPlayerBallPositions);
			
			clonesTowardOpponent = getMovesTowardOpponent(BotMoveType.CLONE, MoveTowardOpponentType.ONE_AXIS_FORWARD_SECOND_AT_LEAST_NEUTRAL);
//			jumpsTowardOpponent = getMovesTowardOpponent(BotMoveType.JUMP, MoveTowardOpponentType.ONE_AXIS_FORWARD_SECOND_AT_LEAST_NEUTRAL);
			
			clonesPartlyTowardOpponent = getMovesTowardOpponent(BotMoveType.CLONE, MoveTowardOpponentType.ONE_AXIS_FORWARD_SECOND_BACKWARD);
//			jumpsPartlyTowardOpponent = getMovesTowardOpponent(BotMoveType.JUMP, MoveTowardOpponentType.ONE_AXIS_FORWARD_SECOND_BACKWARD);
			
			moveTimerTask = new TimerTask() {
				@Override
				public void run() {
					if(cloneOrJumpForBiggestProfitToLossRatio()){
						System.out.println("Move in result of cloneOrJumpForBiggestProfitToLossRatio()");
						return;
					}
					if(moveTowardOpponent()){
						System.out.println("Move in result of moveTowardOpponent()");
						return;
					}
					if(moveAnywhere()){
						System.out.println("Move in result of moveAnywhere()");
						return;
					}
					
					sendSkipTurn();
					System.out.println("Move in result of sendSkipTurn() (Turn skipped due to move inability");
				}
			};
			
			moveTimer.schedule(moveTimerTask, RandomUtils.nextInt(2000, 4300));

		}
		else{
			System.out.println("move wait mock");
		}
	}

	@Override
	public void connected(Connection connection) {

	}

	@Override
	public void received(Connection connection, Object obj) {
		System.out.println("BotSingleGame received message in general: "+obj);
		if(obj instanceof UpdateRoom){
			NetworkConfig.log("received update room");
			final UpdateRoom updateRoom = (UpdateRoom) obj;
			ActionTypeUpdateRoom actionType = updateRoom.getActionType();
			if(actionType==ActionTypeUpdateRoom.BACK_TO_ROOM_LIST){
				//TODO
			}
			if(actionType==ActionTypeUpdateRoom.BACK_TO_ROOM_INTERIOR){
				try{
					moveTimer.cancel();
				}catch(IllegalStateException e){
					
				}
				clonesClient.setListener(botRoomWaiting);
			}
		}
		if(obj instanceof UpdateBoard){
			UpdateBoard update = (UpdateBoard) obj;
			PlayerPosition playerPos = update.getPlayerPosition();
			Position movePos = update.getPosition();
			
			if(update.getActionType()==ActionTypeBoard.CLONE){
				board.clone(playerPos.getX(), playerPos.getY(), movePos.getX(), movePos.getY());
			}
			else if(update.getActionType()==ActionTypeBoard.JUMP){
				board.jump(playerPos.getX(), playerPos.getY(), movePos.getX(), movePos.getY());
			}
			else if(update.getActionType()==ActionTypeBoard.NEXT_TURN){
				//just do nothing
			}
			
			turn.nextTurn();
			move();
		}
		
		//to delete
//		if(obj instanceof UpdateGame){
//			UpdateGame update = (UpdateGame) obj;
//			if(update.getActionType()==ActionTypeGame.NEXT_TURN){
//				turn.nextTurn();
//				updateStage();
//			}
//		}
		
//		if(obj instanceof GameOver){
//			clonesClient.setListener(botRoomWaiting);
//			clonesClient.send(new MoveRoom(ActionTypeMoveRoom.STOP_GAME, null, null));
//			botRoomWaiting.nextMapAndGameRestart();
//		}
		
		if(obj instanceof GameStart){
			final GameStart gameStart = (GameStart) obj;
			newSingleGame(botRoomWaiting, clonesClient, gameStart);
//			BotSingleGame newBotSingleGame = new BotSingleGame(botRoomWaiting, clonesClient, gameStart);
//			clonesClient.setListener(newBotSingleGame);
		}
		
	}

	@Override
	public void disconnected(Connection connection) {

	}

	@Override
	public void idle(Connection connection) {

	}
	
	private List<Position> getThisPlayerBallPositions(){
		return getPlayerBallPositions(thisPlayer);
	}
	
	private List<Position> getOpponentBallPositions(){
		PlayerType opponent = PlayerType.RED;
		if(thisPlayer==PlayerType.RED) opponent = PlayerType.BLUE;
		return getPlayerBallPositions(opponent);
	}
	
	private List<Position> getPlayerBallPositions(PlayerType playerType){
		List<Position> playerBallPositions = new ArrayList<Position>();
		for(int i=0; i<board.getWidth();i++){
			for(int j=0; j<board.getHeight();j++){
				if(board.getFieldPlayer(i, j)==playerType){
					playerBallPositions.add(new Position(i, j));
				}
			}
		}
//		System.out.println(playerType+" positions:");
//		System.out.println(playerBallPositions);
		return playerBallPositions;
	}

	private List<PossibleMove> getPossibleMoves(BotMoveType botMoveType, List<Position> playerBallPositions){
		int moveRange = 1;
		if(botMoveType==BotMoveType.CLONE){
			moveRange=1;
		}
		else if(botMoveType==BotMoveType.JUMP){
			moveRange=2;
		}
		List<PossibleMove> possibleMoves = new ArrayList<PossibleMove>();
		for(Position thisPlayerBallPosition: playerBallPositions){
			int startX = thisPlayerBallPosition.getX()-moveRange;
			int startY = thisPlayerBallPosition.getY()-moveRange;
			int stopX = thisPlayerBallPosition.getX()+moveRange;
			int stopY = thisPlayerBallPosition.getY()+moveRange;
			for(int i=startX; i<=stopX; i++){
				for(int j=startY; j<=stopY; j++){
					boolean isMovePossible = false;
					if(botMoveType==BotMoveType.CLONE){
						isMovePossible = board.isClonePossible(thisPlayerBallPosition.getX(), thisPlayerBallPosition.getY(), i, j);
						if(isMovePossible){
							Position movePosition = new Position(i,j);
							int enclosingOpponentBalls = getNumberOfEnclosingOpponentBallsIncludingPlayerPositionBall(movePosition);
							int possiblyLostPlayerBalls = getNumberOfPossiblyLostBallsAsResultOfMove(movePosition, false);
							PossibleMove possibleMove = new PossibleMove(thisPlayerBallPosition, movePosition, enclosingOpponentBalls, possiblyLostPlayerBalls);
							possibleMoves.add(possibleMove);
						}
					}
					else if(botMoveType==BotMoveType.JUMP){
						isMovePossible = board.isJumpPossible(thisPlayerBallPosition.getX(), thisPlayerBallPosition.getY(), i, j);
						if(isMovePossible){
							Position movePosition = new Position(i,j);
							int enclosingOpponentBalls = getNumberOfEnclosingOpponentBallsIncludingPlayerPositionBall(movePosition);
							int possiblyLostPlayerBalls = getNumberOfPossiblyLostBallsAsResultOfMove(thisPlayerBallPosition, true);
							PossibleMove possibleMove = new PossibleMove(thisPlayerBallPosition, movePosition, enclosingOpponentBalls, possiblyLostPlayerBalls);
							possibleMoves.add(possibleMove);
						}
					}
					//TODO: lepszy algorytm na obliczanie profit loss przy klonowaniu, obecny dzia³a jak ten od skoku
//					if(isMovePossible){
//						Position movePosition = new Position(i,j);
//						int enclosingOpponentBalls = getNumberOfEnclosingOpponentBallsIncludingPlayerPositionBall(movePosition);
//						int possiblyLostPlayerBalls = getNumberOfPossiblyLostBallsAsResultOfMoveIncludingPlayerPositionBall(thisPlayerBallPosition);
//						PossibleMove possibleMove = new PossibleMove(thisPlayerBallPosition, movePosition, enclosingOpponentBalls, possiblyLostPlayerBalls);
//						possibleMoves.add(possibleMove);
//					}
				}
			}
		}
//		if(botMoveType==BotMoveType.CLONE){
//			System.out.println("Possible clones:");
//		}
//		else if(botMoveType==BotMoveType.JUMP){
//			System.out.println("Possible jumps:");
//		}
//		System.out.println(possibleMoves);
		return possibleMoves;
	}
	
	private int getNumberOfEnclosingOpponentBallsIncludingPlayerPositionBall(Position playerPosition){
		PlayerType oppositePlayer = PlayerType.RED;
		if(thisPlayer==PlayerType.RED) oppositePlayer = PlayerType.BLUE;
		int enclosingBalls = getNumberOfEnclosingBallsNotRegardingPlayerPositionBall(playerPosition, oppositePlayer);
		enclosingBalls++; //including player position ball
//		System.out.println("Enclosing opponent balls including player position ball: "+enclosingBalls);
		return enclosingBalls;
	}
	
	private int getNumberOfPossiblyLostBallsAsResultOfMove(Position playerPosition, boolean includePlayerPosition){
		int enclosingBalls = getNumberOfEnclosingBallsNotRegardingPlayerPositionBall(playerPosition, thisPlayer);
		if(includePlayerPosition==true) enclosingBalls++;//including player position ball
//		System.out.println("Possibly lost balls including player position ball "+enclosingBalls);
		return enclosingBalls;
	}
	
	private int getNumberOfEnclosingBallsNotRegardingPlayerPositionBall(Position playerPosition, PlayerType playerWhoIsEnclosing){
		int range = 1;
		int startX = playerPosition.getX()-range;
		int startY = playerPosition.getY()-range;
		int stopX = playerPosition.getX()+range;
		int stopY = playerPosition.getY()+range;
		int enclosingCounter = 0;
		for(int i=startX; i<=stopX; i++){
			for(int j=startY; j<=stopY; j++){
				if(!(i==playerPosition.getX()&&j==playerPosition.getY())){
					PlayerType fieldPlayer = null;
					try{
						fieldPlayer = board.getFieldPlayer(i, j);
					}
					catch(ArrayIndexOutOfBoundsException e){
						
					}
					if(fieldPlayer!=null && fieldPlayer==playerWhoIsEnclosing){
						enclosingCounter++;
					}
				}
			}
		}
		return enclosingCounter;
	}
	
	private List<PossibleMove> getMovesTowardOpponent(BotMoveType botMoveType, MoveTowardOpponentType moveTowardOpponentType){		
		List<PossibleMove> movesTowardOpponent = new ArrayList<PossibleMove>();
		List<PossibleMove> possibleMoves = new ArrayList<PossibleMove>();
		if(botMoveType==BotMoveType.CLONE){
			possibleMoves = possibleClones;
		}
		else if(botMoveType==BotMoveType.JUMP){
			possibleMoves = possibleJumps;
		}
		
		for(PossibleMove possibleMove: possibleMoves){
			int fromToOpponentDistanceX = Math.abs((possibleMove.getFrom().getX()-opponentFirstPosition.getX()));
			int fromToOpponentDistanceY = Math.abs((possibleMove.getFrom().getY()-opponentFirstPosition.getY()));
			
			int toToOpponentDistanceX = Math.abs((possibleMove.getTo().getX()-opponentFirstPosition.getX()));
			int toToOpponentDistanceY = Math.abs((possibleMove.getTo().getY()-opponentFirstPosition.getY()));
			
			DistanceState xDistanceState = DistanceState.UNCHANGED;
			DistanceState yDistanceState = DistanceState.UNCHANGED;
			
			if(toToOpponentDistanceX<fromToOpponentDistanceX){
				xDistanceState = DistanceState.SHORTENED;
			}
			else if(toToOpponentDistanceX==fromToOpponentDistanceX){
				xDistanceState = DistanceState.UNCHANGED;
			}
			else{
				xDistanceState = DistanceState.LENGTHEN;
			}
			if(toToOpponentDistanceY<fromToOpponentDistanceY){
				yDistanceState = DistanceState.SHORTENED;
			}
			else if(toToOpponentDistanceY==fromToOpponentDistanceY){
				yDistanceState = DistanceState.UNCHANGED;
			}
			else{
				yDistanceState = DistanceState.LENGTHEN;
			}
						
			boolean isTowardsOpponent = false;
			
			if(moveTowardOpponentType==MoveTowardOpponentType.ONE_AXIS_FORWARD_SECOND_AT_LEAST_NEUTRAL){
				if((xDistanceState==DistanceState.SHORTENED&&yDistanceState==DistanceState.UNCHANGED)
						||(xDistanceState==DistanceState.UNCHANGED&&yDistanceState==DistanceState.SHORTENED)){
					isTowardsOpponent=true;
				}
			}
			else if(moveTowardOpponentType==MoveTowardOpponentType.ONE_AXIS_FORWARD_SECOND_BACKWARD){
				if((xDistanceState==DistanceState.SHORTENED&&yDistanceState==DistanceState.LENGTHEN)
						||(xDistanceState==DistanceState.LENGTHEN&&yDistanceState==DistanceState.SHORTENED)){
					isTowardsOpponent=true;
				}
			}
			
			if(isTowardsOpponent){
				movesTowardOpponent.add(possibleMove);
			}

		}
		
//		if(botMoveType==BotMoveType.CLONE){
//			if(moveTowardOpponentType==MoveTowardOpponentType.ONE_AXIS_FORWARD_SECOND_AT_LEAST_NEUTRAL){
//				System.out.println("Clones towards opponent:");
//			}
//			else if(moveTowardOpponentType==MoveTowardOpponentType.ONE_AXIS_FORWARD_SECOND_BACKWARD){
//				System.out.println("Clones partly towards opponent:");
//			}
//		}
//		else if(botMoveType==BotMoveType.JUMP){
//			if(moveTowardOpponentType==MoveTowardOpponentType.ONE_AXIS_FORWARD_SECOND_AT_LEAST_NEUTRAL){
//				System.out.println("Jumps towards opponent:");
//			}
//			else if(moveTowardOpponentType==MoveTowardOpponentType.ONE_AXIS_FORWARD_SECOND_BACKWARD){
//				System.out.println("Jumps partly towards opponent:");
//			}
//		}
//		System.out.println(movesTowardOpponent);
		
		return movesTowardOpponent;
	}
	
	private boolean moveTowardOpponent(){
		PossibleMove move = null;
		if(!clonesTowardOpponent.isEmpty()){
			move = clonesTowardOpponent.get(0);
		}
		else if(!clonesPartlyTowardOpponent.isEmpty()){
			move = clonesPartlyTowardOpponent.get(0);
		}
//		else if(!jumpsTowardOpponent.isEmpty()){
//			move = jumpsTowardOpponent.get(0);
//		}
//		else if(!jumpsPartlyTowardOpponent.isEmpty()){
//			move = jumpsPartlyTowardOpponent.get(0);
//		}
		
		if(move!=null){
			PlayerPosition positionFrom = new PlayerPosition(move.getFrom().getX(), move.getFrom().getY(), thisPlayer);
			Position positionTo = move.getTo();
			sendMove(positionFrom, positionTo);
			return true;
		}
		
		return false;
	}
	
	private void sendMove(PlayerPosition positionFrom, Position positionTo){
		System.out.println("Moving from "+positionFrom+" to "+positionTo);
		clonesClient.send(new MoveBoard(ActionTypeMove.MOVE,positionFrom,positionTo));
	}
	
	private boolean cloneOrJumpForBiggestProfitToLossRatio(){
		PossibleMove bestClone = getMoveWithBiggestProfitToLossDifference(possibleClones);
		int bestCloneProfitToLoseDifference = 0;
		if(bestClone!=null){
			bestCloneProfitToLoseDifference = bestClone.getProfit()-bestClone.getLoss();
		}
		System.out.println("Best clone: "+bestCloneProfitToLoseDifference);
		PossibleMove bestJump = getMoveWithBiggestProfitToLossDifference(possibleJumps);
		int bestJumpProfitToLoseDifference = 0;
		if(bestJump!=null){
			bestJumpProfitToLoseDifference = bestJump.getProfit()-bestJump.getLoss();
		}
		System.out.println("Best jump: "+bestJumpProfitToLoseDifference);
		
		if(bestClone!=null && bestJump!=null){
			if(bestJumpProfitToLoseDifference>bestCloneProfitToLoseDifference && bestJumpProfitToLoseDifference>=2){
				PlayerPosition positionFrom = new PlayerPosition(bestJump.getFrom().getX(), bestJump.getFrom().getY(), thisPlayer);
				Position positionTo = bestJump.getTo();
				sendMove(positionFrom, positionTo);
				return true;
			}
		}
		if(bestClone!=null){
			if(bestCloneProfitToLoseDifference>=1){
				PlayerPosition positionFrom = new PlayerPosition(bestClone.getFrom().getX(), bestClone.getFrom().getY(), thisPlayer);
				Position positionTo = bestClone.getTo();
				sendMove(positionFrom, positionTo);
				return true;
			}
		}
		
		return false;
	}
	
	private PossibleMove getMoveWithBiggestProfitToLossDifference(List<PossibleMove> possibleMoves){
		if(possibleMoves.size()>0){
			int biggestDiffrence = possibleMoves.get(0).getProfit()-possibleMoves.get(0).getLoss();
			PossibleMove bestMove = possibleMoves.get(0);
			for(PossibleMove possibleMove: possibleMoves){
				int currentDifference = possibleMove.getProfit()-possibleMove.getLoss();
				if(currentDifference>biggestDiffrence){
					bestMove = possibleMove;
				}
			}
			return bestMove;
		}
		return null;
	}
	
	private boolean moveAnywhere(){
		if(possibleClones.size()>0){
			PlayerPosition positionFrom = new PlayerPosition(possibleClones.get(0).getFrom().getX(), possibleClones.get(0).getFrom().getY(), thisPlayer);
			Position positionTo = possibleClones.get(0).getTo();
			sendMove(positionFrom, positionTo);
			return true;
		}
		if(possibleJumps.size()>0){
			PlayerPosition positionFrom = new PlayerPosition(possibleJumps.get(0).getFrom().getX(), possibleJumps.get(0).getFrom().getY(), thisPlayer);
			Position positionTo = possibleJumps.get(0).getTo();
			sendMove(positionFrom, positionTo);
			return true;
		}
		return false;
	}
	
	private void sendSkipTurn(){
		System.out.println("Sending skip turn");
		clonesClient.send(new MoveBoard(ActionTypeMove.SKIP_TURN, null, null));
	}
}
