package com.luke.clonesbot.bot;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.RandomUtils;

import com.esotericsoftware.kryonet.Connection;
import com.luke.clones.map.model.AvailableMapModelsType;
import com.luke.clones.model.type.PlayerMoveTimeLimitType;
import com.luke.clones.network.communication.ActionTypeMoveRoom;
import com.luke.clones.network.communication.ActionTypeUpdateRoom;
import com.luke.clones.network.communication.GameStart;
import com.luke.clones.network.communication.MoveRoom;
import com.luke.clones.network.communication.PlayerMoveTimeLimit;
import com.luke.clones.network.communication.RoomModifyInfo;
import com.luke.clones.network.communication.UpdateRoom;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.network.controller.ClonesClient;

public class BotRoomWaiting implements ListenerService{
	
	private BotSingleGame botSingleGame;
	
	TimerTask startGameTimerTask;
	Timer startGameTimer = new Timer();
	
//	private TestRoomInfoWrapper testRoom;
	private ClonesClient clonesClient;
	
	public BotRoomWaiting(ClonesClient clonesClient){
//		this.testRoom = testRoom;
		this.clonesClient = clonesClient;
	}

	@Override
	public void connected(Connection connection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void received(Connection connection, Object obj) {
		waitingForAnotherPlayer(connection, obj);
	}
	
	private void waitingForAnotherPlayer(Connection connection, Object obj){
		if(obj instanceof UpdateRoom){
			final UpdateRoom updateRoom = (UpdateRoom) obj;
			ActionTypeUpdateRoom actionType = updateRoom.getActionType();
			System.out.println("BotRoomWaiting Update room in general: "+actionType);
			if(actionType==ActionTypeUpdateRoom.ROOM_INTERIOR){
//				System.out.println("Room interior update");
				if(updateRoom.getRoomInterior().getNumberOfPlayers()==updateRoom.getRoomInterior().getPlayerLimit()){

					startGameTimerTask = new TimerTask() {
						@Override
						public void run() {
							clonesClient.send(new MoveRoom(ActionTypeMoveRoom.START_GAME, null, null));
						}
					};
					try{
						startGameTimer.cancel();
						startGameTimer = new Timer();
					}catch(IllegalStateException e){
						
					}
					startGameTimer.schedule(startGameTimerTask, RandomUtils.nextInt(4700, 5700));
				}
				else{
					try{
						startGameTimer.cancel();
						startGameTimer = new Timer();
					}catch(IllegalStateException e){
						
					}
				}
			}
			else if(actionType==ActionTypeUpdateRoom.BACK_TO_ROOM_INTERIOR){
				clonesClient.send(new MoveRoom(ActionTypeMoveRoom.GET_INTERIOR, null, null));
			}
		}
		else if(obj instanceof GameStart){
			final GameStart gameStart = (GameStart) obj;
			botSingleGame = new BotSingleGame(this, clonesClient, gameStart);
			clonesClient.setListener(botSingleGame);
		}
	}

	@Override
	public void disconnected(Connection connection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void idle(Connection connection) {
		// TODO Auto-generated method stub
		
	}
	
//	public void nextMapAndGameRestart(){
//		String roomName = "Jan [Bot] room";
//		int playerNumber = 2;
//		PlayerMoveTimeLimit playerMoveTimeLimit = PlayerMoveTimeLimitType.T15S.getPlayerMoveTimeLimit();
//		String mapName = AvailableMapModelsType.IN_THE_MIDDLE.getMap().getMapName();
//		RoomModifyInfo roomUpdatePreferencesInfo = new RoomModifyInfo(
//				roomName,
//				playerNumber,
//				playerMoveTimeLimit,
//				mapName);
//		MoveRoom moveRoom = new MoveRoom(ActionTypeMoveRoom.MODIFY, null, roomName);
//		moveRoom.setRoomUpdateInfo(roomUpdatePreferencesInfo);
//		clonesClient.send(moveRoom);
//	}

}
