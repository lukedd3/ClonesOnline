package com.luke.clones.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.luke.clones.config.Config;
import com.luke.clones.messages.QuickMessageShowable;
import com.luke.clones.messages.SoundMessage;
import com.luke.clones.model.SpecialKeyHandler;
import com.luke.clones.model.button.SoundTextButton;
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

public class ScreenMainMenu implements Screen, QuickMessageShowable{
	private StartScreen startScreen;
	private int width, height;
	private Viewport viewport;
	private Stage stage;
	private Skin skin;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;
	
	public ScreenMainMenu(StartScreen startScreen){
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
				exitApp();
			}
		};
		inputMultiplexer = new InputMultiplexer(stage, specialKeyHandler);
		
		Gdx.input.setInputProcessor(inputMultiplexer);
		skin = new Skin(Gdx.files.internal("data/skins/skin_medium.json"));	
		
		
		buildMenu();
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
	
	private synchronized void buildMenu(){

		
		Table menuTable = new Table(skin);
		menuTable.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		menuTable.setSize(width-Config.widthOffset/2, height-(Config.menuBottomHeightOffset+Config.menuTopHeightOffset));
		menuTable.setBackground(skin.getDrawable("default-scroll"));
		menuTable.align(Align.top);
		
		ScrollPane menuPane = new ScrollPane(menuTable, skin);
		menuPane.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		menuPane.setSize(width-Config.widthOffset/2, height-(Config.menuTopHeightOffset+Config.menuBottomHeightOffset));
		
		stage.addActor(menuPane);
		
		TextButton onlineMultiButton = new TextButton("Online Multiplayer", skin);
		menuTable.add(onlineMultiButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		menuTable.row();
		
		TextButton localMultiButton = new TextButton("Local Multiplayer", skin);
		menuTable.add(localMultiButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		menuTable.row();
		
		TextButton howToPlayButton = new TextButton("How to play?", skin);
		menuTable.add(howToPlayButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		menuTable.row();
		
		TextButton aboutButton = new TextButton("About", skin);
		menuTable.add(aboutButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		menuTable.row();
		
		if(Config.showMapCreator){
			TextButton mapCreatorButton = new TextButton("Map Creator", skin);
			menuTable.add(mapCreatorButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
			
			menuTable.row();
			
			mapCreatorButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					SoundManager.INSTANCE.play(SoundType.CLICK);
					startScreen.setToMapCreator();
				}
			});
		}
		
		if(Config.showTestTools){
			TextButton testToolsButton = new TextButton("Test tools", skin);
			menuTable.add(testToolsButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
			
			menuTable.row();
			
			testToolsButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					SoundManager.INSTANCE.play(SoundType.CLICK);
					startScreen.setToTestTools();
				}
			});
		}
		
		TextButton settingsButton = new TextButton("Settings", skin);
		menuTable.add(settingsButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		menuTable.row();
		
//		TextButton rateBetaButton = new TextButton("Review (rate) beta version", skin);
//		TextButtonStyle rateBetaButtonStyle = new TextButtonStyle(rateBetaButton.getStyle());
//		rateBetaButtonStyle.fontColor = Color.GREEN;
//		rateBetaButton.setStyle(rateBetaButtonStyle);
//		
//		menuTable.add(rateBetaButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
//		
//		menuTable.row();
		
		TextButton reportBugButton = new TextButton("Report bug/suggestion", skin);
		menuTable.add(reportBugButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		menuTable.row();
		
		TextButton exitButton = new TextButton("Exit", skin);
		menuTable.add(exitButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		menuTable.row();
		
		Table socialTable = new Table(skin);
		menuTable.add(socialTable).size(width-6, 70).spaceBottom(3).spaceTop(33);
		
		final TextureRegion clonesFacebookLogoRegion = new TextureRegion(new Texture(Gdx.files.internal("data/facebook_small.png")), 0, 0, 128, 128);
		final TextureRegion clonesFacebookLogoAltRegion = new TextureRegion(new Texture(Gdx.files.internal("data/facebook_small_alt.png")), 0, 0, 128, 128);
		Image clonesFacebookLogoImage = new Image(clonesFacebookLogoRegion);
		clonesFacebookLogoImage.setAlign(Align.center);
		socialTable.add(clonesFacebookLogoImage).size(64,64).spaceRight(15);
		
		final TextureRegion clonesTwitterLogoRegion = new TextureRegion(new Texture(Gdx.files.internal("data/twitter_small.png")), 0, 0, 128, 128);
		final TextureRegion clonesTwitterLogoAltRegion = new TextureRegion(new Texture(Gdx.files.internal("data/twitter_small_alt.png")), 0, 0, 128, 128);
		Image clonesTwitterLogoImage = new Image(clonesTwitterLogoRegion);
		clonesTwitterLogoImage.setAlign(Align.center);
		socialTable.add(clonesTwitterLogoImage).size(60,60).spaceLeft(15).spaceRight(15);
		
		final TextureRegion clonesPlayLogoRegion = new TextureRegion(new Texture(Gdx.files.internal("data/play_small.png")), 0, 0, 128, 128);
		final TextureRegion clonesPlayLogoAltRegion = new TextureRegion(new Texture(Gdx.files.internal("data/play_small_alt.png")), 0, 0, 128, 128);
		Image clonesPlayLogoImage = new Image(clonesPlayLogoRegion);
		clonesPlayLogoImage.setAlign(Align.center);
		socialTable.add(clonesPlayLogoImage).size(56,56).spaceLeft(15);
		
		onlineMultiButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				startScreen.setToOnlineServerSelection();
			}
		});
		
		localMultiButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				startScreen.setToLocalMultiplayer();
			}
		});
		
		howToPlayButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				startScreen.setToHowToPlay();
			}
		});
		
		aboutButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				startScreen.setToAbout();
			}
		});
		
//		rateBetaButton.addListener(new ClickListener(){
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				SoundManager.INSTANCE.play(SoundType.CLICK);
//				startScreen.setToReviewBeta();
//			}
//		});
		
		reportBugButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				startScreen.setToReportBug();
			}
		});
		
		settingsButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				startScreen.setToSettings();
			}
		});
		
		exitButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				exitApp();
			}
		});
		
		clonesFacebookLogoImage.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(clonesFacebookLogoAltRegion));
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(clonesFacebookLogoRegion));
				startScreen.getAndroidNativeHandler().openUrl("www.facebook.com/clonesonline");
			}
		});
		
		clonesPlayLogoImage.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(clonesPlayLogoAltRegion));
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(clonesPlayLogoRegion));
				startScreen.getAndroidNativeHandler().openMarketApp(Config.appPackageForGooglePlayButton);
			}
		});
		
		clonesTwitterLogoImage.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(clonesTwitterLogoAltRegion));
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(clonesTwitterLogoRegion));
				startScreen.getAndroidNativeHandler().openUrl("www.twitter.com/clonesonline");
			}
		});
		
	}
	
	private void exitApp(){
		startScreen.getMusicManager().dispose();
		System.exit(0);
	}

	@Override
	public void resize(int width, int height) {
		if(viewport!=null) viewport.update(width, height, true);
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
	
	@Override
	public Stage getStageForQuickMessage() {
		return stage;
	}

}
