package com.luke.clones.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.luke.clones.config.Config;
import com.luke.clones.model.SpecialKeyHandler;
import com.luke.clones.settings.SettingsManager;
import com.luke.clones.settings.SettingType;
import com.luke.clones.sound.SoundManager;
import com.luke.clones.sound.SoundType;

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

public class ScreenSettings implements Screen{
	private StartScreen startScreen;
	private int width, height;
	private Viewport viewport;
	private Stage stage;
	private Skin skin;
	private Table mainTable;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;
	
	public ScreenSettings(StartScreen startScreen){
		this.startScreen = startScreen;
	}

	@Override
	public synchronized void show() {
		width = Config.width;
		height = Config.height;
//		stage = new Stage(width, height, false);
		viewport = new FitViewport(width, height);
		stage = new Stage();
		stage.setViewport(viewport);
		
		specialKeyHandler = new SpecialKeyHandler() {
			@Override
			public void backKeyAction() {
				startScreen.setToMainMenu();
			}
		};
		
		inputMultiplexer = new InputMultiplexer(stage, specialKeyHandler);
		
		Gdx.input.setInputProcessor(inputMultiplexer);
		skin = new Skin(Gdx.files.internal("data/skins/skin_medium.json"));
		
		loadSettingsFromMemory();
		
		buildInterface();
	}

	@Override
	public synchronized void hide() {
		stage = null;
		skin = null;
	}

	@Override
	public synchronized void render(float delta) {
		Gdx.gl.glClearColor(54f/255f, 47f/255f, 45f/255f, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
	}
	
	private void loadSettingsFromMemory() {

	}
	
	private void buildInterface() {
		buildMainTable();
		
		TextButton backToMenuButton = new TextButton("Back to menu", skin);
		mainTable.add(backToMenuButton).minSize(width*3/4-6, 70).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		backToMenuButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				startScreen.setToMainMenu();
			}
		});
		
		CheckBox musicBox = new CheckBox("Music", skin);
		musicBox.setChecked(SettingsManager.getSettingValue(SettingType.MUSIC_ON));
		mainTable.add(musicBox).minHeight(40).spaceTop(12);
		
		mainTable.row();
		
		CheckBox soundsBox = new CheckBox("Sounds", skin);
		soundsBox.setChecked(SettingsManager.getSettingValue(SettingType.SOUND_ON));
		mainTable.add(soundsBox).minHeight(40).spaceTop(5);
		
		musicBox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				reverseSettingValueAndSaveIt(SettingType.MUSIC_ON);
			}
		});
		
		soundsBox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				reverseSettingValueAndSaveIt(SettingType.SOUND_ON);
			}
		});
	}
	
	private void reverseSettingValueAndSaveIt(SettingType setting){
		boolean currentValue = SettingsManager.getSettingValue(setting);
		boolean newValue = !currentValue;
		SettingsManager.setSettingValue(setting, newValue);
		
		updateMusicPlayStateDependingOnSettings();
	}
	
	private void updateMusicPlayStateDependingOnSettings(){
		if(SettingsManager.getSettingValue(SettingType.MUSIC_ON)==true){
			startScreen.getMusicManager().play();
		}
		else{
			startScreen.getMusicManager().pauseAndScheduleGradualVolumeIncreaseBeforePlay();
		}
	}
	
	private void buildMainTable(){
		mainTable = new Table(skin);
		mainTable.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainTable.setSize(width-Config.widthOffset/2, height-(Config.menuBottomHeightOffset+Config.menuTopHeightOffset));
		mainTable.setBackground(skin.getDrawable("default-scroll"));
		mainTable.align(Align.top);
		
		stage.addActor(mainTable);
	}

	@Override
	public void resize(int width, int height) {
		if(viewport!=null) viewport.update(width, height, true);
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
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
