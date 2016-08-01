package com.luke.clones.model;

import java.util.ArrayList;

import com.luke.clones.model.SingleStat.TurnStatsArgumentException;
import com.luke.clones.model.type.PlayerType;

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

public class TurnStats {
	private SingleStat[] stats;
	
	/**
	 * For serialization
	 */
	public TurnStats(){
		
	}
	
	public TurnStats(PlayerType[] playerList){
		stats = new SingleStat[playerList.length];
		for(int i =0; i<stats.length;i++){
			stats[i] = new SingleStat(playerList[i]);
		}
	}
	
	public TurnStats(PlayerScore[] playerScore) throws TurnStatsArgumentException {
		stats = new SingleStat[playerScore.length];
		for(int i =0; i<stats.length;i++){
			stats[i] = new SingleStat(playerScore[i].getPlayerType());
		}
		saveTurnStats(playerScore);
	}
	
	public void addRoundStats(PlayerScore[] playerScore) throws TurnStatsArgumentException {
		stats = new SingleStat[playerScore.length];
		for(int i =0; i<stats.length;i++){
			stats[i] = new SingleStat(playerScore[i].getPlayerType());
		}
		saveTurnStats(playerScore);
	}
	
	public void saveTurnStats(PlayerScore[] playerScore) throws TurnStatsArgumentException{
		if(stats.length!=playerScore.length) throw new TurnStatsArgumentException();
		
		ArrayList<PlayerType> winnerList = new ArrayList<PlayerType>();
		int bestScore = playerScore[0].getPlayerScore();
		for(int i = 1;i<playerScore.length;i++){
			if(playerScore[i].getPlayerScore()>bestScore) bestScore = playerScore[i].getPlayerScore();
		}
		for(int i = 0;i<playerScore.length;i++){
			if(playerScore[i].getPlayerScore()==bestScore) winnerList.add(playerScore[i].getPlayerType());
		}
		
		int checkSum=0;
		for(int i=0; i<stats.length; i++){
			for(int j=0;j<playerScore.length;j++){
				if(stats[i].getPlayerType() == playerScore[j].getPlayerType()){
					boolean turnWinner = false;
					if(winnerList.contains(playerScore[j].getPlayerType())) turnWinner = true;
					stats[i].saveTurnStats(turnWinner, playerScore[j].getPlayerScore());
					checkSum++;
					break;
				}
			}
		}
		
		if(checkSum!=stats.length) throw new TurnStatsArgumentException();
	}
	
	public int getPlayerGameBalls(PlayerType playerType) throws TurnStatsArgumentException{
		for(int i=0; i<stats.length;i++){
			if(stats[i].getPlayerType() == playerType) return stats[i].getGameBalls();
		}
		throw new TurnStatsArgumentException();
	}
	
	public int getPlayerGameWons(PlayerType playerType) throws TurnStatsArgumentException{
		for(int i=0; i<stats.length;i++){
			if(stats[i].getPlayerType() == playerType) return stats[i].getGamesWon();
		}
		throw new TurnStatsArgumentException();
	}
}

