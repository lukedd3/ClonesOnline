package com.luke.clones.test.production.implementation.helper;

import org.apache.commons.lang3.RandomStringUtils;

import com.luke.clones.map.model.AvailableMapModelsType;
import com.luke.clones.model.TimeMeasure;
import com.luke.clones.model.type.PlayerMoveTimeLimitType;
import com.luke.clones.network.communication.ActionTypeMove;
import com.luke.clones.network.communication.ActionTypeMoveRoom;
import com.luke.clones.network.communication.MoveBoard;
import com.luke.clones.network.communication.MoveRoom;
import com.luke.clones.network.communication.PlayerMoveTimeLimit;
import com.luke.clones.network.communication.RoomCreateInfo;
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

public class TestRoomInfoWrapper {
	private RoomCreateInfo roomCreateInfo;
	private TestRoomState testRoomState;
	private TimeMeasure existingTime;
	private TimeMeasure nonExistingTime;
	
	public TestRoomInfoWrapper(){
		existingTime = new TimeMeasure();
		nonExistingTime = new TimeMeasure();
	}
	
	public void createRoom(ClonesClient clonesClient){
		System.out.println("createRoom");
		
		String roomName = prepareRoomInfo();
		
		clonesClient.send(new MoveRoom(ActionTypeMoveRoom.CREATE, roomCreateInfo, roomName));
		testRoomState = TestRoomState.EXISTING;
		existingTime.start();
	}
	
	public void createRoom(ClonesClient clonesClient, RoomCreateInfo externalRoomCreateInfo){
		System.out.println("createRoom");
		
		String roomName = prepareRoomInfo();
		
		clonesClient.send(new MoveRoom(ActionTypeMoveRoom.CREATE, externalRoomCreateInfo, roomName));
		testRoomState = TestRoomState.EXISTING;
		existingTime.start();
	}

	private String prepareRoomInfo() {
		String roomName = "Test room "+RandomStringUtils.randomAlphanumeric(10);
		int playerNumber = 2;
		PlayerMoveTimeLimit playerMoveTimeLimit = PlayerMoveTimeLimitType.T15S.getPlayerMoveTimeLimit();
		String mapName = AvailableMapModelsType.TUNNEL_RATS.getMap().getMapName();
		
		roomCreateInfo = new RoomCreateInfo(
				roomName,
				playerNumber,
				playerMoveTimeLimit,
				mapName);
		return roomName;
	}
	
	public void removeRoom(ClonesClient clonesClient){
		System.out.println("removeRoom");
		clonesClient.send(new MoveRoom(ActionTypeMoveRoom.LEAVE, null, null));
		testRoomState = TestRoomState.NON_EXISTING;
		nonExistingTime.start();
	}
	
	public void joinRoom(ClonesClient clonesClient){
		clonesClient.send(new MoveRoom(ActionTypeMoveRoom.ENTER, null, roomCreateInfo.getRoomName()));
	}
	
	public void skipTurn(ClonesClient clonesClient){
		clonesClient.send(new MoveBoard(ActionTypeMove.SKIP_TURN, null, null));
	}
	
	public void startGame(ClonesClient clonesClient){
		clonesClient.send(new MoveRoom(ActionTypeMoveRoom.START_GAME, null, null));
	}

	public TestRoomState getTestRoomState() {
		return testRoomState;
	}

	public TimeMeasure getExistingTime() {
		return existingTime;
	}

	public TimeMeasure getNonExistingTime() {
		return nonExistingTime;
	}

	public RoomCreateInfo getRoomCreateInfo() {
		return roomCreateInfo;
	}
	
}
