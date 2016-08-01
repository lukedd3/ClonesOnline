package com.luke.clones.model;

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

public class SingleStat{
	private PlayerType playerType;
	private int gameBalls;
	private int gamesWon;
	
	/**
	 * For serialization
	 */
	public SingleStat(){
		
	}
	
	public SingleStat(PlayerType playerType){
		this.playerType = playerType;
		gameBalls = 0;
		gamesWon = 0;
	}
	
	public PlayerType getPlayerType() {
		return playerType;
	}

	public int getGameBalls() {
		return gameBalls;
	}

	public int getGamesWon() {
		return gamesWon;
	}
	
	public void saveTurnStats(boolean turnWon, int turnBalls){
		this.gameBalls += turnBalls;
		if(turnWon) this.gamesWon += 1;
	}
	
	public static class TurnStatsArgumentException extends Exception{
		private static final long serialVersionUID = 3332457183145463260L;

		@Override
		public String getMessage() {
			String message = "Illegal argument value passed to the method";
			return super.getMessage() + message;
		}
	}
	
}