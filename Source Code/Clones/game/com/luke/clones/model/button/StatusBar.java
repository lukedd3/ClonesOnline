package com.luke.clones.model.button;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.luke.clones.config.Config;
import com.luke.clones.model.LogicBoard;
import com.luke.clones.model.type.PlayerType;
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

public abstract class StatusBar{
	private Stage stage;
	protected Turn turn;
	protected LogicBoard logicBoard;
	private Skin skin, secondSkin;
	private int x, y, width, height;
	private Table table;
	protected Label turnL;
	protected PlayerType[] availablePlayers;
	protected Label[] countLabel;
	
	public StatusBar(Stage stage, Turn turn, LogicBoard logicBoard,  Skin skin, Skin secondSkin){
		this.stage = stage;
		this.turn = turn;
		this.logicBoard = logicBoard;
		this.skin = skin;
		this.secondSkin = secondSkin;
		
		table = new Table(skin);
		table.setBackground(skin.getDrawable("default-round-large"));
		if(Config.debug) table.debug();
		
		stage.addActor(table);
		
		Config.log("In status max elment:"+(turn.getAvailablePlayers().length-1));
		
		turnL = new Label(turn.getCurrentPlayer()+ " TURN", skin);
		turnL.setColor(Config.textColor);
		table.add(turnL);
		
		availablePlayers = turn.getAvailablePlayers();
		countLabel = new Label[availablePlayers.length];
		if(availablePlayers.length<3){
			for(int i=0; i<availablePlayers.length; i++){
				Image space = new Image(new Texture(Gdx.files.internal("data/skins/space.png")));
				table.add(space);
				countLabel[i] = new Label(availablePlayers[i]+": "+logicBoard.getBallCount(availablePlayers[i]),secondSkin);
				countLabel[i].setColor(Config.textColor);
				table.add(countLabel[i]);
			}
			Image space = new Image(new Texture(Gdx.files.internal("data/skins/space.png")));
			table.add(space);
		}
		else{
			Image space = new Image(new Texture(Gdx.files.internal("data/skins/space.png")));
			table.add(space);
			Table smallTable = new Table();
			for(int i=0; i<availablePlayers.length; i++){
				if(i>0 && !(i==2&&availablePlayers.length==3)){
						space = new Image(new Texture(Gdx.files.internal("data/skins/space.png")));
						smallTable.add(space);
				}
				if(i==2) smallTable.row();
				countLabel[i] = new Label(availablePlayers[i]+": "+logicBoard.getBallCount(availablePlayers[i]),secondSkin);
				countLabel[i].setColor(Config.textColor);
				smallTable.add(countLabel[i]);
			}
			space = new Image(new Texture(Gdx.files.internal("data/skins/space.png")));
			smallTable.add(space);
			table.add(smallTable);
			if(availablePlayers.length==3){
				space = new Image(new Texture(Gdx.files.internal("data/skins/space.png")));
				table.add(space);
			}
		}
		
		stage.addActor(table);
	}
	
	public void setPosition(int posX, int posY){
		x = posX;
		y = posY;
		table.setPosition(x, y);
	}
	
	public int getPosX(){
		return x;
	}
	
	public int getPosY(){
		return y;
	}
	
	public void setSize(int width, int height){
		this.width = width;
		this.height = height;
		table.setSize(width, height);
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	
	
	public LogicBoard getLogicBoard() {
		return logicBoard;
	}

	public void setLogicBoard(LogicBoard logicBoard) {
		this.logicBoard = logicBoard;
	}

	public abstract void reflesh();
}
