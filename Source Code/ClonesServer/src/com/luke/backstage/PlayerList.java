package com.luke.backstage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.esotericsoftware.kryonet.Connection;

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

public class PlayerList {
	private volatile List<Player> playerSet;
	
	public PlayerList(){
//		playerSet = new ArrayList<Player>();
		playerSet = new CopyOnWriteArrayList<Player>();
	}
	
	public void add(Player player){
		playerSet.add(player);
	}
	
	public Player get(String name){
		for(Player player: playerSet){
			if(player.getName().equals(name)){
				return player;
			}
		}
		return null;
	}
	
	public Player get(Connection connection){
		for(Player player: playerSet){
			if(player.getConnection().equals(connection)){
				return player;
			}
		}
		return null;
	}
	
	public void remove(Connection connection){
		Player playerToRemove = null;
		for(Player player: playerSet){
			if(player.getConnection().equals(connection)){
				playerToRemove = player;
				break;
			}
		}
		playerSet.remove(playerToRemove);
	}
	
	public void remove(String name){
		Player playerToRemove = null;
		for(Player player: playerSet){
			if(player.getName().equals(name)){
				playerToRemove = player;
				break;
			}
		}
		playerSet.remove(playerToRemove);
	}
	
	public boolean contains(String playerName){
		for(Player player: playerSet){
			if(player.getName().equals(playerName)){
				return true;
			}
		}
		return false;
	}

	public int size(){
		return playerSet.size();
	}
	
	public Player[] getAllPlayers(){
		return playerSet.toArray(new Player[0]);
	}
	
//	Room list is needed to check whether player is member of any room
	public void sendToAllPlayersWithoutRoom(Object obj, RoomList roomList){
		for(Player player: playerSet){
			if(roomList.get(player)==null){
				player.getConnection().sendTCP(obj);
			}
		}
//		Iterator<Player> iter =playerSet.iterator();
//		while(iter.hasNext()){
//			Player player = iter.next();
//			if(roomList.get(player)==null){
//				player.getConnection().sendTCP(obj);
//			}
//		}
	}
}
