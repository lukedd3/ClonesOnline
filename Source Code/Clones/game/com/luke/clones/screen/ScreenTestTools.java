package com.luke.clones.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.luke.clones.config.Config;
import com.luke.clones.model.SpecialKeyHandler;
import com.luke.clones.sound.SoundManager;
import com.luke.clones.sound.SoundType;
import com.luke.clones.test.production.ProductionTestManager;
import com.luke.clones.test.production.implementation.GameSimulationTest;
import com.luke.clones.test.production.implementation.RoomCreateRemoveTest;

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

public class ScreenTestTools implements Screen{
	private StartScreen startScreen;
	private int width, height;
	private Viewport mainViewport;
	private Stage mainStage;
	private Skin skin;
	private Table mainTable;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;
	
	RoomCreateRemoveTest roomCreateRemoveTest;
	private GameSimulationTest gameSimulationTest;
	
	boolean roomCreateRemoveTestStarted = false;
	boolean gameSimulationTestStarted = false;
	
	public ScreenTestTools(StartScreen startScreen){
		this.startScreen = startScreen;
	}

	@Override
	public void show() {
		width = Config.width;
		height = Config.height;
		mainViewport = new FitViewport(width, height);
		mainStage = new Stage();
		mainStage.setViewport(mainViewport);
		
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
				startScreen.setToMainMenu();
			}
		};
		
		inputMultiplexer = new InputMultiplexer(mainStage, specialKeyHandler);
		
		Gdx.input.setInputProcessor(inputMultiplexer);
		skin = new Skin(Gdx.files.internal("data/skins/skin_medium.json"));
		
		buildInterface();
	}
	
	private void buildInterface() {
		buildMainTable();
		
		TextButton backToMenuButton = new TextButton("Back to menu", skin);
		mainTable.add(backToMenuButton).minSize(width*3/4-6, 70).spaceBottom(3).spaceTop(3);
		
		backToMenuButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				startScreen.setToMainMenu();
			}
		});
		
		mainTable.row();
		
		final TextButton roomCreateRemoveTestButton = new TextButton("Start room create remove test", skin);
		mainTable.add(roomCreateRemoveTestButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		roomCreateRemoveTestButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				if(roomCreateRemoveTestStarted==false){
					roomCreateRemoveTestStarted = true;
					roomCreateRemoveTestButton.setText("Stop room create remove test");
					roomCreateRemoveTest = new RoomCreateRemoveTest(startScreen.getAndroidNativeHandler(), 0);
					ProductionTestManager.startTest(roomCreateRemoveTest);
				}
				else{
					roomCreateRemoveTestStarted = false;
					roomCreateRemoveTestButton.setText("Start room create remove test");
					ProductionTestManager.stopTest(roomCreateRemoveTest);
				}
				return true;
			}
		});
		
		mainTable.row();
		
		final TextButton gameSimulationTestButton = new TextButton("Start game simulation test", skin);
		mainTable.add(gameSimulationTestButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		gameSimulationTestButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				if(gameSimulationTestStarted==false){
					gameSimulationTestStarted = true;
					gameSimulationTestButton.setText("Stop game simulation test");
					gameSimulationTest = new GameSimulationTest(startScreen.getAndroidNativeHandler(), 0);
					ProductionTestManager.startTest(gameSimulationTest);
				}
				else{
					gameSimulationTestStarted = false;
					gameSimulationTestButton.setText("Start game simulation test");
					ProductionTestManager.stopTest(gameSimulationTest);
				}
				return true;
			}
		});
	}


	private void buildMainTable(){

		mainTable = new Table(skin);
		mainTable.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainTable.setSize(width-Config.widthOffset/2, height-(Config.menuBottomHeightOffset+Config.menuTopHeightOffset));
		mainTable.setBackground(skin.getDrawable("default-scroll"));
		mainTable.align(Align.top | Align.center);
		
		ScrollPane mainPane = new ScrollPane(mainTable, skin);
		mainPane.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainPane.setSize(width-Config.widthOffset/2, height-(Config.menuTopHeightOffset+Config.menuBottomHeightOffset));
		
		mainStage.addActor(mainPane);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(54f/255f, 47f/255f, 45f/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mainStage.act();
		mainStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		if(mainViewport!=null) mainViewport.update(width, height, true);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		mainStage = null;
		skin = null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
}
