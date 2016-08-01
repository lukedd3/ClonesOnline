package com.luke.backstage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryonet.Connection;
import com.luke.clones.network.communication.RoomInfo;
import com.luke.clones.util.ObjectSizeUtil;

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

public class RoomList {
	Set<Room> roomSet;
	
	public RoomList(){
		roomSet = new HashSet<Room>();
	}
	
	public void add(Room room){
		roomSet.add(room);
	}
	
	public Room get(Player player){
		for(Room room : roomSet){
			Player[] players = room.getAllPlayers();
			for(Player tempPlayer : players){
				if(tempPlayer.equals(player)) return room;
			}
		}
		return null;
	}
	
	public Room get(Connection connection){
		for(Room room : roomSet){
			Player[] players = room.getAllPlayers();
			for(Player tempPlayer : players){
				if(tempPlayer.getConnection().equals(connection)) return room;
			}
		}
		return null;
	}
	
	public Room get(String roomName){
		for(Room room : roomSet){
			if(room.getName().equals(roomName)){
				return room;
			}
		}
		return null;
	}
	
	public boolean contains(String roomName){
		for(Room room : roomSet){
			if(room.getName().equals(roomName)){
				return true;
			}
		}
		return false;
	}
	
	public RoomInfo[] getRoomInfoList(){
		ArrayList<RoomInfo> roomList = new ArrayList<RoomInfo>();
		for(Room room: roomSet){
			Player[] players = room.getAllPlayers();
			String[] playerNames = new String[players.length];
			for(int i=0; i<players.length;i++){
				playerNames[i] = players[i].getName();
			}
			roomList.add(new RoomInfo(
					room.getId(),
					room.getName(),
					playerNames,
					room.getPlayerLimit(),
					room.getPlayerMoveTimeLimit(),
					room.getAdmin().getName(),
					room.getRoomCreateInfo().getMapName()));
		}
		return roomList.toArray(new RoomInfo[0]);
	}
	
	public void remove(Room room){
		Room roomToRemove=null;
		for(Room tempRoom : roomSet){
			if(tempRoom.getName().equals(room.getName())){
				roomToRemove = tempRoom;
				break;
			}
		}
		if(roomToRemove!=null) roomSet.remove(roomToRemove);
	}
	
	public void remove(String roomName){
		Room roomToRemove=null;
		for(Room tempRoom : roomSet){
			if(tempRoom.getName().equals(roomName)){
				roomToRemove = tempRoom;
				break;
			}
		}
		if(roomToRemove!=null) roomSet.remove(roomToRemove);
	}
	
}
