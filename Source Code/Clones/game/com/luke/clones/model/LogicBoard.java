package com.luke.clones.model;

import java.util.HashMap;

import com.luke.clones.model.type.FieldType;
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

public class LogicBoard {
	private int width, height;
	private LogicField[][] field;
	private HashMap<PlayerType, Integer> ballCount;
	private int maxBallCount;
	
	public LogicBoard(int width, int height){
		this.width = width;
		this.height = height;
		
		field = new LogicField[width][height];
		for(int i=0;i<width;i++){
			for(int j=0; j<height;j++){
				field[i][j] = new LogicField(FieldType.EMPTY, PlayerType.NONE);
			}
		}
		
		ballCount = new HashMap<PlayerType, Integer>();
		ballCount.put(PlayerType.RED,0);
		ballCount.put(PlayerType.GREEN,0);
		ballCount.put(PlayerType.BLUE,0);
		ballCount.put(PlayerType.ORANGE,0);
		
		maxBallCount = width * height;
	}
	
	public void setFieldTypeSolid(int posX, int posY){
		if(posX>=0 && posY>=0 && posX<width && posY<height){
				if(field[posX][posY].getFieldType()== FieldType.TAKEN){
					ballCount.put(field[posX][posY].getPlayer(), ballCount.get(field[posX][posY].getPlayer())-1);
				}
				field[posX][posY].setType(FieldType.SOLID);
				field[posX][posY].setPlayer(PlayerType.NONE);
		}
		else throw new ArrayIndexOutOfBoundsException();
	}
	
	public void setFieldTypeEmpty(int posX, int posY){
		if(posX>=0 && posY>=0 && posX<width && posY<height){
			if(field[posX][posY].getFieldType()!=FieldType.SOLID){
				if(field[posX][posY].getFieldType()== FieldType.TAKEN){
					ballCount.put(field[posX][posY].getPlayer(), ballCount.get(field[posX][posY].getPlayer())-1);
				}
				field[posX][posY].setType(FieldType.EMPTY);
				field[posX][posY].setPlayer(PlayerType.NONE);
			}
		}
		else throw new ArrayIndexOutOfBoundsException();
	}
	
	public FieldType getFieldType(int posX, int posY){
		if(posX>=0 && posY>=0 && posX<width && posY<height){
			return field[posX][posY].getFieldType();
		}
		else throw new ArrayIndexOutOfBoundsException();
	}
	
	public void setFieldPlayer(int posX, int posY, PlayerType playerType){
		if(posX>=0 && posY>=0 && posX<width && posY<height){
			if(field[posX][posY].getFieldType()!=FieldType.SOLID){
				field[posX][posY].setPlayer(playerType);
				field[posX][posY].setType(FieldType.TAKEN);
				ballCount.put(playerType, ballCount.get(playerType)+1);
			}
		}
		else throw new ArrayIndexOutOfBoundsException();
	}
	
	public PlayerType getFieldPlayer(int posX, int posY){
		if(posX>=0 && posY>=0 && posX<width && posY<height){
			return field[posX][posY].getPlayer();
		}
		else throw new ArrayIndexOutOfBoundsException();
	}
	
	public boolean isMovePossible(int fromX,int fromY,int toX,int toY){
		
		if(fromX == toX && fromY == toY) return false;
		
		if(fromX<0 || fromY<0 || fromX>width-1 || fromY>height-1) return false;
		
		if(field[fromX][fromY].getFieldType()!=FieldType.TAKEN) return false;
		if(field[fromX][fromY].getPlayer()==PlayerType.NONE) return false;
		
		if(toX<0 || toY<0 || toX>width-1 || toY>height-1) return false;
		
		if(field[toX][toY].getFieldType()!=FieldType.EMPTY) return false;
		if(field[toX][toY].getPlayer()!=PlayerType.NONE) return false;//
		
		int xDif = toX - fromX;
		int yDif = toY - fromY;
		
		if(Math.abs(xDif)>=0 && Math.abs(xDif)<=2 && Math.abs(yDif)>=0 && Math.abs(yDif)<=2) return true;
		else return false;
	}

	public boolean isClonePossible(int fromX,int fromY,int toX,int toY){
		if(!isMovePossible(fromX, fromY, toX, toY)) return false;
		else{
			int xDif = toX - fromX;
			int yDif = toY - fromY;
			
			if(Math.abs(xDif)>=0 && Math.abs(xDif)<=1 && Math.abs(yDif)>=0 && Math.abs(yDif)<=1) return true;
			else return false;
		}
	}
	
	public boolean isJumpPossible(int fromX,int fromY,int toX,int toY){
		if(!isMovePossible(fromX, fromY, toX, toY)) return false;
		else{
			int xDif = toX - fromX;
			int yDif = toY - fromY;
			
			if(Math.abs(xDif)>=0 && Math.abs(xDif)<=1 && Math.abs(yDif)>=0 && Math.abs(yDif)<=1) return false;
			else return true;
		}
	}
	
	public boolean clone(int fromX, int fromY, int toX, int toY){
		if(isClonePossible(fromX, fromY, toX, toY)){
			field[toX][toY].setPlayer(field[fromX][fromY].getPlayer());
			field[toX][toY].setType(FieldType.TAKEN);
			ballCount.put(field[fromX][fromY].getPlayer(), ballCount.get(field[fromX][fromY].getPlayer())+1);
			
			for(int i=toX-1; i<=toX+1; i++){
				for(int j=toY-1; j<=toY+1; j++){
					if(i>=0 && i<width && j>=0 && j<height && !(i==toX && j==toY) && field[i][j].getFieldType() == FieldType.TAKEN){
						ballCount.put(field[i][j].getPlayer(), ballCount.get(field[i][j].getPlayer())-1);
						ballCount.put(field[fromX][fromY].getPlayer(), ballCount.get(field[fromX][fromY].getPlayer())+1);
						field[i][j].setPlayer(field[fromX][fromY].getPlayer());
					}
				}
			}
			
			return true;
		}
		else return false;
	}
	
	public boolean jump(int fromX, int fromY, int toX, int toY){
		if(isJumpPossible(fromX, fromY, toX, toY)){
			field[toX][toY].setPlayer(field[fromX][fromY].getPlayer());
			field[toX][toY].setType(FieldType.TAKEN);
			
			for(int i=toX-1; i<=toX+1; i++){
				for(int j=toY-1; j<=toY+1; j++){ 
					if(i>=0 && i<width && j>=0 && j<height && !(i==toX && j==toY) && field[i][j].getFieldType() == FieldType.TAKEN){
						ballCount.put(field[i][j].getPlayer(), ballCount.get(field[i][j].getPlayer())-1);
						ballCount.put(field[fromX][fromY].getPlayer(), ballCount.get(field[fromX][fromY].getPlayer())+1);
						field[i][j].setPlayer(field[fromX][fromY].getPlayer());
					}
				}
			}
			
			field[fromX][fromY].setPlayer(PlayerType.NONE);
			field[fromX][fromY].setType(FieldType.EMPTY);
			
			
			return true;
		}
		else return false;
	}

	/**
	 * Returns true when selected player  is able to move anywhere. You can use this method to skip
	 * the turn if player can't move anywhere.
	 * @param playerType
	 * @return boolean
	 */
	public boolean isPlayerMoveable(PlayerType playerType){
		boolean moveable=false;
		
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				if(field[i][j].getPlayer()==playerType){
					for(int k=i-2;k<=i+2;k++){
						for(int l=j-2;l<=j+2;l++){
							if(k>=0 && l>=0 && k<width && l<height){
								if(field[k][l].getFieldType()==FieldType.EMPTY){
									moveable = true;
									break;
								}
							}
						}
					}
				}
			}
		}
		
		return moveable;
	}
	
	public int getWidth(){return width;}
	
	public int getHeight(){return height;}
	
	public int getBallCount(PlayerType playerType){
		return ballCount.get(playerType);
	}
	
	public int getMaxBallCount(){
		return maxBallCount;
	}
}
