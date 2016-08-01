package com.luke.clones.autogame;

import java.util.ArrayList;
import java.util.List;

import com.luke.clones.bot.BotMoveType;
import com.luke.clones.bot.PossibleMove;
import com.luke.clones.model.LogicBoard;
import com.luke.clones.model.type.PlayerType;
import com.luke.clones.model.type.Position;
import com.luke.clones.model.type.Turn;

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

public class AutoGameUtil {
	
	public static boolean isOnlyOnePlayerMoveable(Turn turn, LogicBoard board){
		int moveablePlayersNum = 0;
		PlayerType[] moveablePlayers = turn.getAvailablePlayers();
		for(PlayerType player:moveablePlayers){
			if(board.isPlayerMoveable(player)==true){
				moveablePlayersNum++;
			}
		}
		if(moveablePlayersNum==1){
			return true;
		}
		return false;
	}
	
	public static List<Position> getThisPlayerBallPositions(Turn turn, LogicBoard board){
		return getPlayerBallPositions(turn.getCurrentPlayer(), board);
	}
	
	public static List<Position> getPlayerBallPositions(PlayerType playerType, LogicBoard board){
		List<Position> playerBallPositions = new ArrayList<Position>();
		for(int i=0; i<board.getWidth();i++){
			for(int j=0; j<board.getHeight();j++){
				if(board.getFieldPlayer(i, j)==playerType){
					playerBallPositions.add(new Position(i, j));
				}
			}
		}
		return playerBallPositions;
	}
	
	public static List<PossibleMove> getPossibleMoves(BotMoveType botMoveType, List<Position> playerBallPositions, LogicBoard board){
		int moveRange = 1;
		if(botMoveType==BotMoveType.CLONE){
			moveRange=1;
		}
		else if(botMoveType==BotMoveType.JUMP){
			moveRange=2;
		}
		List<PossibleMove> possibleMoves = new ArrayList<PossibleMove>();
		for(Position thisPlayerBallPosition: playerBallPositions){
			int startX = thisPlayerBallPosition.getX()-moveRange;
			int startY = thisPlayerBallPosition.getY()-moveRange;
			int stopX = thisPlayerBallPosition.getX()+moveRange;
			int stopY = thisPlayerBallPosition.getY()+moveRange;
			for(int i=startX; i<=stopX; i++){
				for(int j=startY; j<=stopY; j++){
					boolean isMovePossible = false;
					if(botMoveType==BotMoveType.CLONE){
						isMovePossible = board.isClonePossible(thisPlayerBallPosition.getX(), thisPlayerBallPosition.getY(), i, j);
						if(isMovePossible){
							Position movePosition = new Position(i,j);
							PossibleMove possibleMove = new PossibleMove(thisPlayerBallPosition, movePosition, 0, 0);
							possibleMoves.add(possibleMove);
						}
					}
					else if(botMoveType==BotMoveType.JUMP){
						isMovePossible = board.isJumpPossible(thisPlayerBallPosition.getX(), thisPlayerBallPosition.getY(), i, j);
						if(isMovePossible){
							Position movePosition = new Position(i,j);
							PossibleMove possibleMove = new PossibleMove(thisPlayerBallPosition, movePosition, 0, 0);
							possibleMoves.add(possibleMove);
						}
					}
				}
			}
		}
		return possibleMoves;
	}
	
}
