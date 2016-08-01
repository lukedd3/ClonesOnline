package com.luke.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.luke.authentication.model.GoogleUserData;
import com.luke.authentication.model.TokenValidation;
import com.luke.backstage.LeaveOrDisconnectActionType;
import com.luke.backstage.Player;
import com.luke.backstage.PlayerList;
import com.luke.backstage.Room;
import com.luke.backstage.RoomList;
import com.luke.clones.model.MapAutoCreator.MapCreatorArgumentException;
import com.luke.clones.model.type.PlayerType;
import com.luke.clones.network.communication.AchievementType;
import com.luke.clones.network.communication.AchievementsRequest;
import com.luke.clones.network.communication.AchievementsResponse;
import com.luke.clones.network.communication.ActionTypeMoveRoom;
import com.luke.clones.network.communication.ActionTypeStats;
import com.luke.clones.network.communication.ActionTypeUpdateLogin;
import com.luke.clones.network.communication.ActionTypeUpdateRoom;
import com.luke.clones.network.communication.GetServerBackendInfo;
import com.luke.clones.network.communication.MessageToShowType;
import com.luke.clones.network.communication.MoveBoard;
import com.luke.clones.network.communication.MoveLogin;
import com.luke.clones.network.communication.MoveRoom;
import com.luke.clones.network.communication.RoomCreateInfo;
import com.luke.clones.network.communication.RoomInfo;
import com.luke.clones.network.communication.RoomModifyInfo;
import com.luke.clones.network.communication.ServerBackendInfo;
import com.luke.clones.network.communication.StatsRequest;
import com.luke.clones.network.communication.StatsResponse;
import com.luke.clones.network.communication.StatsType;
import com.luke.clones.network.communication.UpdateLogin;
import com.luke.clones.network.communication.UpdateRoom;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.config.Config;
import com.luke.service.AchievementsService;
import com.luke.service.StatsGetService;
import com.luke.service.UsersService;
import com.luke.util.AuthorizationUtil;

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

public class ServerService implements ListenerService{
	private Logger log = Logger.getLogger(ServerService.class);
	
	private PlayerList playerList;
	private RoomList roomList;
	private UsersService usersService = UsersService.INSTANCE;
	private StatsGetService statsGetService = StatsGetService.INSTANCE;
	private AchievementsService achievementsService = AchievementsService.INSTANCE;
	
	public ServerService(){
		playerList = new PlayerList();
		roomList = new RoomList();
	}
	
	@Override
	public void connected(Connection connection) {
		
	}

	@Override
	public void received(Connection connection, Object obj) {
		if(obj.getClass()!=KeepAlive.class){
			log.debug("Message received. Msg type: "+obj.getClass().toString()+". Connection id: "+connection.getID());
		}
		if(obj instanceof GetServerBackendInfo){
			GetServerBackendInfo message = (GetServerBackendInfo) obj;
			connection.sendTCP(new ServerBackendInfo(playerList.size(), Config.maxNumberOfPlayers));
		}
		else if(obj instanceof MoveLogin){
			try {
				MoveLogin login = (MoveLogin) obj;
				String token = login.getToken();
				
				TokenValidation tokenValidation = new TokenValidation(false, null);
				//Should be commented on production level for security reasons
				if(Config.testModeOn && token.equals("pcUKwZZuUpmMajlTWlgLhVwpskzybSaqlmBZcQKpYOIMNGzUka")){
					GoogleUserData googleUser = new GoogleUserData();
					googleUser.setId("1");
					tokenValidation = new TokenValidation(true, googleUser);
				}
				else{
					tokenValidation = AuthorizationUtil.isTokenValid(token);
				}
				boolean tokenValid = tokenValidation.isTokenValid();
				
				String playerName = login.getPlayerName();
				
				if(playerList.size()>=Config.maxNumberOfPlayers){
					connection.sendTCP(new UpdateLogin(ActionTypeUpdateLogin.SERVER_FULL));
					return;
				}
				
				if(playerName.length()==0){
//					connection.sendTCP(new UpdateLogin(ActionTypeUpdateLogin.FORBIDDEN_NAME));
					connection.sendTCP(new UpdateLogin(ActionTypeUpdateLogin.UNEXPECTED_ERROR));
					return;
				}
				else if(playerList.contains(playerName)==true){
//					connection.sendTCP(new UpdateLogin(ActionTypeUpdateLogin.NAME_ALREADY_TAKEN));
					connection.sendTCP(new UpdateLogin(ActionTypeUpdateLogin.UNEXPECTED_ERROR));
					return;
				}
				
				if(tokenValid){
					playerList.add(new Player(playerName, tokenValidation.getUserData(), connection));
					connection.sendTCP(new UpdateLogin(ActionTypeUpdateLogin.SUCCESS));
					log.info("Player \""+login.getPlayerName()+ "\" successfully logged in");
					usersService.addUserIfNotPresent(tokenValidation.getUserData().getId(), login.getPlayerName());
				}
				else{
					//for test only
					connection.sendTCP(new UpdateLogin(ActionTypeUpdateLogin.INVALID_AUTH_TOKEN));
				}
			} catch (IOException e) {
				connection.sendTCP(new UpdateLogin(ActionTypeUpdateLogin.UNEXPECTED_ERROR));
				log.error("", e);
			}
			
		}
		else if(obj instanceof MoveRoom){
			MoveRoom moveRoom = (MoveRoom) obj;
			ActionTypeMoveRoom action = moveRoom.getActionType();
			if(action == ActionTypeMoveRoom.GET_LIST){
				//sending room list to client
				ActionTypeUpdateRoom actionType = ActionTypeUpdateRoom.ROOM_LIST;
				RoomInfo[] roomInfoList = roomList.getRoomInfoList();
				RoomInfo roomInterior = null;//we need to send only list this time
				UpdateRoom updateRoom = new UpdateRoom(actionType, roomInfoList, roomInterior);
				connection.sendTCP(updateRoom);
			}
			if(action == ActionTypeMoveRoom.CREATE){
				RoomCreateInfo roomCreateInfo = moveRoom.getRoomCreateInfo();
				String tempRoomName = moveRoom.getRoomName();
				if(tempRoomName.length()==0){
					connection.sendTCP(new UpdateRoom(ActionTypeUpdateRoom.FORBIDDEN_NAME, null, null));
				}
				else if(roomList.contains(tempRoomName)==true){
					connection.sendTCP(new UpdateRoom(ActionTypeUpdateRoom.NAME_ALREADY_TAKEN, null, null));
				}
				else{
					Room tempRoom = new Room(tempRoomName, playerList.get(connection));
					tempRoom.setRoomCreateInfo(roomCreateInfo);
					roomList.add(tempRoom);
					connection.sendTCP(new UpdateRoom(ActionTypeUpdateRoom.CREATE_SUCCESS, null, tempRoom.getRoomInfo()));
					//sending info to all without room
					playerList.sendToAllPlayersWithoutRoom((new UpdateRoom(ActionTypeUpdateRoom.ROOM_LIST, roomList.getRoomInfoList(), null)), roomList);
					log.info("Room \""+tempRoomName+"\" created");
				}
			}
			
			if(action == ActionTypeMoveRoom.MODIFY_REQUEST){
				Room tempRoom = roomList.get(connection);
				if(tempRoom!=null){
					Player roomAdmin = tempRoom.getAdmin();
					if(roomAdmin.getName().equals(playerList.get(connection).getName())){
						connection.sendTCP(new UpdateRoom(ActionTypeUpdateRoom.MODIFY_REQUEST_RESPONSE, null,
								tempRoom.getRoomInfo()));
					}
				}
			}
			if(action == ActionTypeMoveRoom.MODIFY){
				RoomModifyInfo roomModifyInfo = moveRoom.getRoomUpdateInfo();
				Room tempRoom = roomList.get(roomModifyInfo.getRoomName());
				if(tempRoom!=null){
					Player roomAdmin = tempRoom.getAdmin();
					if(roomAdmin.getName().equals(playerList.get(connection).getName())){
						tempRoom.setRoomCreateInfo(roomModifyInfo);
						connection.sendTCP(new UpdateRoom(ActionTypeUpdateRoom.ROOM_INTERIOR, null, tempRoom.getRoomInfo()));
						tempRoom.sendToAllPlayersExceptSender(new UpdateRoom(ActionTypeUpdateRoom.ROOM_INTERIOR, null, tempRoom.getRoomInfo()), playerList.get(connection));
						playerList.sendToAllPlayersWithoutRoom((new UpdateRoom(ActionTypeUpdateRoom.ROOM_LIST, roomList.getRoomInfoList(), null)), roomList);
					}
				}
			}
			if(action == ActionTypeMoveRoom.ENTER){
				String tempRoomName = moveRoom.getRoomName();
				Room tempRoom = roomList.get(tempRoomName);
				if(tempRoom==null){
					connection.sendTCP(new UpdateRoom(ActionTypeUpdateRoom.NOT_EXIST, null, null));
				}
				else if(tempRoom.getNumberOfPlayers() == tempRoom.getPlayerLimit()){
					connection.sendTCP(new UpdateRoom(ActionTypeUpdateRoom.IS_FULL, null, null));
				}
				else{
					Player tempPlayer = playerList.get(connection);
					tempRoom.addPlayer(tempPlayer);
					connection.sendTCP(new UpdateRoom(ActionTypeUpdateRoom.ENTER_SUCCESS, null, tempRoom.getRoomInfo()));
					//sending info to all other players in this room
					tempRoom.sendToAllPlayersExceptSender(new UpdateRoom(ActionTypeUpdateRoom.ROOM_INTERIOR, null, tempRoom.getRoomInfo()), playerList.get(connection));
					//sending info to all without room
					playerList.sendToAllPlayersWithoutRoom((new UpdateRoom(ActionTypeUpdateRoom.ROOM_LIST, roomList.getRoomInfoList(), null)), roomList);
				}
			}
			if(action == ActionTypeMoveRoom.START_GAME){
				Player tempPlayer = playerList.get(connection);
				Room tempRoom = roomList.get(tempPlayer);
				if(tempRoom!=null && tempPlayer.getName().equals(tempRoom.getAdmin().getName()) && tempRoom.getNumberOfPlayers()==tempRoom.getPlayerLimit()){
					try {
						tempRoom.startNewGame();
					} catch (MapCreatorArgumentException e) {
						log.error("", e);
					}
				}
				else{
					log.info("Game start rejected");
				}
			}
			if(action == ActionTypeMoveRoom.RESTART_GAME){
				Player tempPlayer = playerList.get(connection);
				Room tempRoom = roomList.get(tempPlayer);
				if(tempRoom!=null && tempPlayer.getName().equals(tempRoom.getAdmin().getName()) && tempRoom.getNumberOfPlayers()==tempRoom.getPlayerLimit()){
					try {
						tempRoom.restartGame();
					} catch (MapCreatorArgumentException e) {
						log.error("", e);
					}
				}
				else{
					log.info("Game start rejected");
				}
			}
			if(action == ActionTypeMoveRoom.GET_INTERIOR){
				Player tempPlayer = playerList.get(connection);
				Room tempRoom = roomList.get(tempPlayer);
				if(tempRoom!=null) connection.sendTCP(new UpdateRoom(ActionTypeUpdateRoom.ROOM_INTERIOR, null, tempRoom.getRoomInfo()));
			}
			if(action == ActionTypeMoveRoom.LEAVE){
				Player tempPlayer = playerList.get(connection);
				Room tempRoom = roomList.get(tempPlayer);
				if(tempRoom!=null){
					if(tempPlayer.getName().equals(tempRoom.getAdmin().getName())){
						adminLeaveOrDisconnectAction(tempPlayer, tempRoom, LeaveOrDisconnectActionType.LEAVE);
					}
					else{
						nonAdminPlayerLeaveOrDisconnectAction(tempPlayer, tempRoom, LeaveOrDisconnectActionType.LEAVE);
					}
					tempRoom.setPause(true);
				}
			}
			if(action == ActionTypeMoveRoom.STOP_GAME){
				Player tempPlayer = playerList.get(connection);
				Room tempRoom = roomList.get(tempPlayer);
				if(tempRoom!=null){
					if(tempPlayer.getName().equals(tempRoom.getAdmin().getName())){
						Player[] tempRoomPlayers = tempRoom.getAllPlayers();
						for(Player tempRoomPlayer: tempRoomPlayers){
							if(tempRoomPlayer.getName().equals(tempRoom.getAdmin().getName())){
								tempRoomPlayer.getConnection().sendTCP(new UpdateRoom(ActionTypeUpdateRoom.BACK_TO_ROOM_INTERIOR, null, null));
							}
							else{
								UpdateRoom updateRoom = new UpdateRoom(ActionTypeUpdateRoom.BACK_TO_ROOM_INTERIOR, null, null);
								updateRoom.setMessageToShow(MessageToShowType.ADMIN_STOPPED);
								tempRoomPlayer.getConnection().sendTCP(updateRoom);
							}
//							tempRoomPlayer.getConnection().sendTCP(new UpdateRoom(ActionTypeUpdateRoom.BACK_TO_ROOM_INTERIOR, null, null));
//							tempRoomPlayer.getConnection().sendTCP(new MessageShowRequest(MessageToShowType.ADMIN_STOPPED));
						}
						tempRoom.setPause(true);
					}
				}
			}
		}
		else if(obj instanceof MoveBoard){
			MoveBoard playerMove = (MoveBoard) obj;
			
			Room room = roomList.get(connection);
			PlayerType senderPlayerType = room.getPlayerType(playerList.get(connection));
			log.info("MoveBoard received, ActionType:"+playerMove.getActionType()+", playerPosition"+playerMove.getPlayerPosition()+", move destination:"+playerMove.getPosition());
			log.info("Additional Info, room: "+room+", PlayerType: "+senderPlayerType);
			if(room.isPause()==false) room.playerMove(playerMove, senderPlayerType);
		}
		else if(obj instanceof StatsRequest){
			StatsRequest statsRequest = (StatsRequest) obj;
			ActionTypeStats actionTypeStats = statsRequest.getActionTypeStats();
			Player tempPlayer = playerList.get(connection);
			String tempPlayerGoogleId = tempPlayer.getGoogleUserData().getId();
			
			if(actionTypeStats==ActionTypeStats.GET_GENERAL_STATS){
				Map<StatsType, Long> generalStatsMap = statsGetService.getAllGeneralStatsValuesMap(tempPlayerGoogleId);
				long level = statsGetService.getUserLevel(tempPlayerGoogleId);
				long percentToNextLevel = statsGetService.getUserPercentToNextLevel(tempPlayerGoogleId);
				StatsResponse statsResponse = new StatsResponse();
				statsResponse.setActionTypeStats(ActionTypeStats.GET_GENERAL_STATS);
				statsResponse.setStatsMap(generalStatsMap);
				statsResponse.setLevel(level);
				statsResponse.setPercentToNextLevel(percentToNextLevel);
				tempPlayer.getConnection().sendTCP(statsResponse);
			}
		}
		else if(obj instanceof AchievementsRequest){
			AchievementsRequest achievementRequest = (AchievementsRequest) obj;
			
			Player tempPlayer = playerList.get(connection);
			String tempPlayerGoogleId = tempPlayer.getGoogleUserData().getId();
			
			AchievementsResponse achievementsResponse = new AchievementsResponse();
			achievementsResponse.setCurrentAchievements(achievementsService.getUserAchievements(tempPlayerGoogleId));
			tempPlayer.getConnection().sendTCP(achievementsResponse);
			
		}
	}

	@Override
	public void disconnected(Connection connection) {
		Player tempPlayer = playerList.get(connection);
		Room tempRoom = roomList.get(tempPlayer);
		System.out.println("disconnect");
		if(tempRoom!=null){
			System.out.println("disconnect and tempRoom!=null");
			if(tempPlayer.getName().equals(tempRoom.getAdmin().getName())){
				adminLeaveOrDisconnectAction(tempPlayer, tempRoom, LeaveOrDisconnectActionType.DISCONNECT);
			}
			else{
				nonAdminPlayerLeaveOrDisconnectAction(tempPlayer, tempRoom, LeaveOrDisconnectActionType.DISCONNECT);
			}
			tempRoom.setPause(true);
		}
		
		playerList.remove(connection);

		log.info("player disconnected, connection id: "+connection.getID());
		log.info("Player list: "+buildPlayerList(playerList.getAllPlayers()));
	}
	
	private void adminLeaveOrDisconnectAction(Player admin, Room room, LeaveOrDisconnectActionType leaveOrDisconnectActionType){
		room.removePlayer(admin);
		UpdateRoom updateRoom = new UpdateRoom(ActionTypeUpdateRoom.BACK_TO_ROOM_LIST, null, null);
		updateRoom = updatePlayerAchievements(updateRoom, admin, leaveOrDisconnectActionType);
		admin.getConnection().sendTCP(updateRoom);
		Player[] tempRoomPlayers = room.getAllPlayers();
		if(tempRoomPlayers.length>0){
			room.markCurrentPlayerAsAdmin(room.getAllPlayers()[0]);
			for(Player tempRoomPlayer: tempRoomPlayers){
				sendBackToRoomInteriorWithMessage(tempRoomPlayer);
			}
		}
		else{
			roomList.remove(room);
		}
		playerList.sendToAllPlayersWithoutRoom(new UpdateRoom(ActionTypeUpdateRoom.ROOM_LIST, roomList.getRoomInfoList(), null), roomList);
	}
	
	private void nonAdminPlayerLeaveOrDisconnectAction(Player player, Room room, LeaveOrDisconnectActionType leaveOrDisconnectActionType){
		room.removePlayer(player);
		UpdateRoom updateRoom = new UpdateRoom(ActionTypeUpdateRoom.BACK_TO_ROOM_LIST, null, null);
		updateRoom = updatePlayerAchievements(updateRoom, player, leaveOrDisconnectActionType);
		player.getConnection().sendTCP(updateRoom);
		Player[] tempRoomPlayers = room.getAllPlayers();
		for(Player tempRoomPlayer: tempRoomPlayers){
			sendBackToRoomInteriorWithMessage(tempRoomPlayer);
		}
		playerList.sendToAllPlayersWithoutRoom(new UpdateRoom(ActionTypeUpdateRoom.ROOM_LIST, roomList.getRoomInfoList(), null), roomList);
	}
	
	private UpdateRoom updatePlayerAchievements(UpdateRoom updateRoom, Player player, LeaveOrDisconnectActionType leaveOrDisconnectActionType){
		if(leaveOrDisconnectActionType==LeaveOrDisconnectActionType.LEAVE){
			List<AchievementType> newAchievements = achievementsService.updateUserAchievements(player.getGoogleUserData().getId());
			updateRoom.setNewAchievements(newAchievements.toArray(new AchievementType[0]));
		}
		return updateRoom;
	}

	private void sendBackToRoomInteriorWithMessage(Player tempRoomPlayer) {
		UpdateRoom updateRoom = new UpdateRoom(ActionTypeUpdateRoom.BACK_TO_ROOM_INTERIOR, null, null);
		updateRoom.setMessageToShow(MessageToShowType.ONE_OF_PLAYERS_LEFT_ROOM);
		tempRoomPlayer.getConnection().sendTCP(updateRoom);
	}

	@Override
	public void idle(Connection connection) {

	}

	private String buildPlayerList(Player[] playerList){
		String players = "";
		for(int i=0;i<playerList.length;i++){
			players += "player"+playerList[i].getConnectionID()+", ";
		}
		return players;
	}
	

}
