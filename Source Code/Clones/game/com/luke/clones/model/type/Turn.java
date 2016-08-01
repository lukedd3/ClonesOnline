package com.luke.clones.model.type;

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

import java.util.ArrayList;
import java.util.Random;

public class Turn {
	private PlayerType[] availablePlayers;
	private int currentPlayer;
	
//	private boolean specialNonPlayerGameEndSequenceState;
	
	public Turn(PlayerPosition[] startPlayers, PlayerType startPlayer){ // if startPlayer is null game starts from random player
//		specialNonPlayerGameEndSequenceState = false;
		
		ArrayList<PlayerType> players = new ArrayList<PlayerType>();
		for(int i=0; i<startPlayers.length; i++){
			PlayerType playerType = startPlayers[i].getPlayerType();
			if(!players.contains(playerType)) players.add(playerType);
		}
		availablePlayers = players.toArray(new PlayerType[players.size()]);
		
		if(startPlayer == null){
			Random random = new Random();
			this.currentPlayer = random.nextInt(availablePlayers.length);
		}
		else{
			//this.currentPlayer = Arrays.binarySearch(availablePlayers, startPlayer); //uzywalem binary search na nieposortowanym zbiorze co dawa³o np. index = -2 !!!
			for(int i=0; i<availablePlayers.length; i++){
				if(availablePlayers[i] == startPlayer){
					currentPlayer = i;
					break;
				}
			}
		}
	}
	
	public Turn(PlayerType[] availablePlayers, PlayerType startPlayer){ // if startPlayer is null game starts from random player
//		specialNonPlayerGameEndSequenceState = false;
		this.availablePlayers = availablePlayers;
		
		if(startPlayer == null){
			Random random = new Random();
			this.currentPlayer = random.nextInt(availablePlayers.length);
		}
		else{
			//this.currentPlayer = Arrays.binarySearch(availablePlayers, startPlayer); //uzywalem binary search na nieposortowanym zbiorze co dawa³o np. index = -2 !!!
			for(int i=0; i<availablePlayers.length; i++){
				if(availablePlayers[i] == startPlayer){
					currentPlayer = i;
					break;
				}
			}
		}
	}
	
	public PlayerType getCurrentPlayer(){
//		if(specialNonPlayerGameEndSequenceState==false){
			return availablePlayers[currentPlayer];
//		}
//		else{
//			return null;
//		}
	}
	
	public void nextTurn(){
		if(++currentPlayer >= availablePlayers.length) currentPlayer = 0;
	}
	
	public PlayerType[] getAvailablePlayers(){
		return availablePlayers;
	}
	
//	public void setSpecialNonPlayerGameEndSequenceState(
//			boolean specialNonPlayerGameEndSequenceState) {
//		this.specialNonPlayerGameEndSequenceState = specialNonPlayerGameEndSequenceState;
//	}

//	public boolean isSpecialNonPlayerGameEndSequenceState() {
//		return specialNonPlayerGameEndSequenceState;
//	}
	
}
