package com.luke.clones.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.luke.clones.config.Config;
import com.luke.clones.model.Map;
import com.luke.clones.model.ViewBoard;
import com.luke.clones.model.button.CustomDialog;
import com.luke.clones.model.type.PlayerMoveTimeLimitType;
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

public class ScreenLocalMultiplayerGame implements Screen{
	private StartScreen startScreen;
	private ViewBoard viewBoard;
	private Viewport mainViewport;
	private Viewport layerViewport;
	private Viewport messageViewport;
	private Stage mainStage, layerStage, messageStage;
	
	public ScreenLocalMultiplayerGame(StartScreen startScreen){
		this.startScreen=startScreen;
		init();
	}
	
	//startPlayer might be null (so he will be randomly selected)
	public void newGame(Map map, PlayerMoveTimeLimitType playerMoveTimeLimitType){
		viewBoard = new ViewBoard(map, playerMoveTimeLimitType, startScreen);
		mainStage = viewBoard.getMainStage();
		layerStage = viewBoard.getLayerStage();
		messageStage = viewBoard.getMessageStage();
		mainViewport = mainStage.getViewport();
		layerViewport = layerStage.getViewport();
		messageViewport = messageStage.getViewport();
	}
	
	private void init(){
		Label errorLabel = new Label("Game not started yet (use newGame())", new Skin(Gdx.files.internal("data/skins/skin_medium.json")));
//		mainStage = new Stage(Config.width,Config.height,false);
		mainViewport = new FitViewport(Config.width, Config.height);
		mainStage = new Stage();
		mainStage.setViewport(mainViewport);
		mainStage.addActor(errorLabel);
	}
	
	@Override
	public void show() {
	}
	
	@Override
	public void hide() {
		mainStage = null;
		layerStage = null;
		messageStage=null;
		viewBoard = null;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(54f/255f, 47f/255f, 45f/255f, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(mainStage!=null){
			mainStage.act();
			mainStage.draw();
		}
		if(layerStage!=null){
			layerStage.act();
			layerStage.draw();
		}
		if(messageStage!=null){
			messageStage.act();
			messageStage.draw();
		}
		
//		if(Config.debug) Table.drawDebug(mainStage);
	}

	@Override
	public void resize(int width, int height) {
		//Generalnie tutaj nale¿y resizowaæ te 3 viewporty z ró¿nych layer stagów
		
		if(mainViewport!=null) mainViewport.update(width, height, true);
		if(layerViewport!=null) layerViewport.update(width, height, true);
		if(messageViewport!=null) messageViewport.update(width, height, true);
		
//		if(mainViewport!=null) mainViewport.update(Config.width, Config.height, true);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		
	}

}
