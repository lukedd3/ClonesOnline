package com.luke.clones.network.communication;

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

/**
 * From server to client
 */
public class RoomInfo{
	private int roomId;
	private String roomName;
	private String[] playerNames;
	private int playerLimit;
	private PlayerMoveTimeLimit playerMoveTimeLimit;
	private String adminName;
	private String mapName;
	
	public RoomInfo (){

	}
	
	public RoomInfo(int roomId, String roomName, String[] playerNames, int playerLimit, PlayerMoveTimeLimit playerMoveTimeLimit, String adminName, String mapName) {
		this.roomId = roomId;
		this.roomName = roomName;
		this.playerNames = playerNames;
		this.playerLimit = playerLimit;
		this.playerMoveTimeLimit = playerMoveTimeLimit;
		this.adminName = adminName;
		this.mapName = mapName;
	}

	public int getRoomId(){
		return roomId;
	}
	
	public String getRoomName(){
		return roomName;
	}
	
	public String[] getPlayerNames(){
		return playerNames;
	}
	
	public int getPlayerLimit(){
		return playerLimit;
	}
	
	public int getNumberOfPlayers(){
		return playerNames.length;
	}
	
	public String getAdminName(){
		return adminName;
	}

	public PlayerMoveTimeLimit getPlayerMoveTimeLimit() {
		return playerMoveTimeLimit;
	}

	public void setPlayerMoveTimeLimit(PlayerMoveTimeLimit playerMoveTimeLimit) {
		this.playerMoveTimeLimit = playerMoveTimeLimit;
	}

	public String getMapName() {
		return mapName;
	}
	
}
