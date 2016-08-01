package com.luke.clones.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.luke.clones.config.Config;
import com.luke.clones.model.ViewBoardOnline;
import com.luke.clones.network.communication.GameStart;
import com.luke.network.controller.ClonesClient;

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

public class ScreenOnlineGame implements Screen{
	private StartScreen startScreen;
	private ViewBoardOnline viewBoardOnline;
	private Viewport mainViewport;
	private Viewport layerViewport;
	private Viewport messageViewport;
	private volatile Stage mainStage, layerStage, messageStage;
	
	public ScreenOnlineGame(StartScreen startScreen){
		this.startScreen=startScreen;
		init();
	}
	
	private synchronized void init(){
		Label errorLabel = new Label("Online game not started yet (use newGame())", new Skin(Gdx.files.internal("data/skins/skin_medium.json")));
//		mainStage = new Stage(Config.width,Config.height,false);
		mainViewport = new FitViewport(Config.width, Config.height);
		mainStage = new Stage();
		mainStage.setViewport(mainViewport);
		mainStage.addActor(errorLabel);
	}
	
	//use this method before show()!
	public void newGame(ClonesClient clonesClient, GameStart gameStart, String playerName){
		viewBoardOnline = new ViewBoardOnline(
				clonesClient,
				gameStart.getMap(),
				gameStart.getPlayerColor(),
				gameStart.getToken(),
				gameStart.getRoomInfo(),
				playerName,
				startScreen,
				gameStart.getPlayerTypeOrder());
		mainStage = viewBoardOnline.getMainStage();
		layerStage = viewBoardOnline.getLayerStage();
		messageStage = viewBoardOnline.getMessageStage();
		mainViewport = mainStage.getViewport();
		layerViewport = layerStage.getViewport();
		messageViewport = messageStage.getViewport();
	}
	
	//use start game instead of show
	@Override
	public void show() {
		
	}

	@Override
	public synchronized void hide() {
		mainStage = null;
		layerStage = null;
		viewBoardOnline = null;
	}
	
	@Override
	public synchronized void render(float delta) {
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
//		if(mainViewport!=null) mainViewport.update(width, height, true);
		
		if(mainViewport!=null) mainViewport.update(width, height, true);
		if(layerViewport!=null) layerViewport.update(width, height, true);
		if(messageViewport!=null) messageViewport.update(width, height, true);
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
