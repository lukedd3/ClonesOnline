package com.luke.clonesbot.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.RandomStringUtils;

import com.luke.clones.map.model.AvailableMapModelsType;
import com.luke.clones.model.type.PlayerMoveTimeLimitType;
import com.luke.clones.network.communication.PlayerMoveTimeLimit;
import com.luke.clones.network.communication.RoomCreateInfo;
import com.luke.clones.test.production.ProductionTest;
import com.luke.clones.test.production.helper.ClonesClientTestWrapper;
import com.luke.clones.test.production.implementation.helper.TestRoomInfoWrapper;
import com.luke.clonesbot.mock.MockAndroidNativeHandler;
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

public class Bot extends ProductionTest{
	
	private TestRoomInfoWrapper testRoom;
	private ClonesClient botClonesClient;
	
	private BotRoomWaiting botRoomWaiting;
	
	List<RoomCreateInfo> roomCreateInfoList;
	
	Timer timer;
	TimerTask timerTask;

	public Bot(int serverNumberOnServerList) {
		super(new MockAndroidNativeHandler(), serverNumberOnServerList);
	}

	protected void test() {
		initialize();
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				
			}
		};
		timer.schedule(timerTask, 0, 1000);
	}
	
	private void initialize() {
		botClonesClient = ((ClonesClientTestWrapper)clonesClientTestWrappers.get(0)).getClonesClient();
		
		testRoom = new TestRoomInfoWrapper();
		roomCreateInfoList = prepareRoomCreateInfoList();
		testRoom.createRoom(botClonesClient, roomCreateInfoList.get(0));
		
		botRoomWaiting = new BotRoomWaiting(botClonesClient);
		botClonesClient.setListener(botRoomWaiting);
	}
	private List<RoomCreateInfo> prepareRoomCreateInfoList(){
		List<RoomCreateInfo> roomCreateInfoLocalList = new ArrayList<RoomCreateInfo>();
		
		String roomName = "Jan [Bot] room";
		int playerNumber = 2;
		PlayerMoveTimeLimit playerMoveTimeLimit = PlayerMoveTimeLimitType.T30S.getPlayerMoveTimeLimit();
		String mapName = AvailableMapModelsType.CHESS_LIKE.getMap().getMapName();
		
		RoomCreateInfo roomCreateInfo = new RoomCreateInfo(
				roomName,
				playerNumber,
				playerMoveTimeLimit,
				mapName);
		
		roomCreateInfoLocalList.add(roomCreateInfo);
		
		return roomCreateInfoLocalList;
	}

	protected void stopTest() {
		// TODO Auto-generated method stub
	}

}
