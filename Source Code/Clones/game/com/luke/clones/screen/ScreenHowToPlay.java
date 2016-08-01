package com.luke.clones.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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

public class ScreenHowToPlay implements Screen{
	private static final int IMAGE_SPACE_TOP = 18;
	private static final int IMAGE_SPACE_BOTTOM = 18;
	
	private StartScreen startScreen;
	private int width, height;
	private Viewport viewport;
	private Stage stage;
	private Skin skin;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;
	
	private ScrollPane mainPane;
	private Table mainTable;
	
	public ScreenHowToPlay(StartScreen startScreen){
		this.startScreen = startScreen;
	}
	
	@Override
	public void show() {
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
		
		buildContent();
	}
	
	private synchronized void buildContent(){
		prepareMainTable();
		prepareContent();
	}
	
	private void prepareMainTable() {
		mainTable = new Table(skin);
		mainPane = new ScrollPane(mainTable, skin);
		mainPane.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainPane.setSize(width-Config.widthOffset/2, height-(Config.menuBottomHeightOffset));
		mainPane.setScrollingDisabled(true, false);
		mainPane.setupFadeScrollBars(0.5f, 0);
		
//		mainTable.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainTable.setSize(width-Config.widthOffset/2, height-(Config.menuBottomHeightOffset+Config.menuTopHeightOffset));
		mainTable.setBackground(skin.getDrawable("default-round-down"));
		mainTable.align(Align.top);
		
		stage.addActor(mainPane);
	}

	private void prepareContent() {
		mainTable.add().minSize(Config.menuTopHeightOffset);
		mainTable.row();
		
		TextButton backToMenuButton = new TextButton("Back to menu", skin);
		mainTable.add(backToMenuButton).minSize(width*3/4-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		Label label1 = new Label("When you wait for opponent's turn" +
				" it is shown on the top bar.", skin);
		label1.setAlignment(Align.center);
		label1.setWrap(true);
		mainTable.add(label1).minWidth(width-6).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextureRegion image1Region = new TextureRegion(new Texture(
				Gdx.files.internal("data/how_to_play_images/image1.png")));
		Image image1 = new Image(image1Region);
		image1.setAlign(Align.center);
		mainTable.add(image1).minSize(image1.getWidth(), image1.getHeight())
		.spaceBottom(IMAGE_SPACE_BOTTOM).spaceTop(IMAGE_SPACE_TOP);
		mainTable.row();
		
		Label label2 = new Label("When it is your turn all your balls are flashing" +
				" but you can also see it on the top bar.", skin);
		label2.setAlignment(Align.center);
		label2.setWrap(true);
		mainTable.add(label2).minWidth(width-6).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextureRegion image2Region = new TextureRegion(new Texture(
				Gdx.files.internal("data/how_to_play_images/image2.png")));
		Image image2 = new Image(image2Region);
		image2.setAlign(Align.center);
		mainTable.add(image2).minSize(image2.getWidth(), image2.getHeight())
		.spaceBottom(IMAGE_SPACE_BOTTOM).spaceTop(IMAGE_SPACE_TOP);
		mainTable.row();
		
		Label label3 = new Label("Every player has limited time to move," +
				" when he exceeds that limit his turn is skipped." +
				" Remaining time is shown on the top.", skin);
		label3.setAlignment(Align.center);
		label3.setWrap(true);
		mainTable.add(label3).minWidth(width-6).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextureRegion image3Region = new TextureRegion(new Texture(
				Gdx.files.internal("data/how_to_play_images/image3.png")));
		Image image3 = new Image(image3Region);
		image3.setAlign(Align.center);
		mainTable.add(image3).minSize(image3.getWidth(), image3.getHeight())
		.spaceBottom(IMAGE_SPACE_BOTTOM).spaceTop(IMAGE_SPACE_TOP);
		mainTable.row();
		
		Label label4 = new Label("To move one of your balls you have to select it." +
				" After that you have 2 possibilities: to CLONE it or to JUMP." +
				" Possible moves are shown by backlight on certain fields.", skin);
		label4.setAlignment(Align.center);
		label4.setWrap(true);
		mainTable.add(label4).minWidth(width-6).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextureRegion image4p1Region = new TextureRegion(new Texture(
				Gdx.files.internal("data/how_to_play_images/image4_p1.png")));
		Image image4p1 = new Image(image4p1Region);
		image4p1.setAlign(Align.center);
		mainTable.add(image4p1).minSize(image4p1.getWidth(), image4p1.getHeight())
		.spaceBottom(IMAGE_SPACE_BOTTOM).spaceTop(IMAGE_SPACE_TOP);
		mainTable.row();
		
		final TextureRegion image4p2Region = new TextureRegion(new Texture(
				Gdx.files.internal("data/how_to_play_images/image4_p2.png")));
		Image image4p2 = new Image(image4p2Region);
		image4p2.setAlign(Align.center);
		mainTable.add(image4p2).minSize(image4p2.getWidth(), image4p2.getHeight())
		.spaceBottom(IMAGE_SPACE_BOTTOM).spaceTop(IMAGE_SPACE_TOP);
		mainTable.row();
		
		final TextureRegion image4p3Region = new TextureRegion(new Texture(
				Gdx.files.internal("data/how_to_play_images/image4_p3.png")));
		Image image4p3 = new Image(image4p3Region);
		image4p3.setAlign(Align.center);
		mainTable.add(image4p3).minSize(image4p3.getWidth(), image4p3.getHeight())
		.spaceBottom(IMAGE_SPACE_BOTTOM).spaceTop(IMAGE_SPACE_TOP);
		mainTable.row();
		
		Label label5 = new Label("After each move you infect opponents balls with your colour." +
				" Infection range may be shown as a square around your ball.", skin);
		label5.setAlignment(Align.center);
		label5.setWrap(true);
		mainTable.add(label5).minWidth(width-6).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextureRegion image5p1Region = new TextureRegion(new Texture(
				Gdx.files.internal("data/how_to_play_images/image5_p1.png")));
		Image image5p1 = new Image(image5p1Region);
		image5p1.setAlign(Align.center);
		mainTable.add(image5p1).minSize(image5p1.getWidth(), image5p1.getHeight())
		.spaceBottom(IMAGE_SPACE_BOTTOM).spaceTop(IMAGE_SPACE_TOP);
		mainTable.row();
		
		final TextureRegion image5p2Region = new TextureRegion(new Texture(
				Gdx.files.internal("data/how_to_play_images/image5_p2.png")));
		Image image5p2 = new Image(image5p2Region);
		image5p2.setAlign(Align.center);
		mainTable.add(image5p2).minSize(image5p2.getWidth(), image5p2.getHeight())
		.spaceBottom(IMAGE_SPACE_BOTTOM).spaceTop(IMAGE_SPACE_TOP);
		mainTable.row();
		
		Label label6 = new Label("Player who has the biggest number of balls at the end of round wins.", skin);
		label6.setAlignment(Align.center);
		label6.setWrap(true);
		mainTable.add(label6).minWidth(width-6).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextureRegion image6Region = new TextureRegion(new Texture(
				Gdx.files.internal("data/how_to_play_images/image6.png")));
		Image image6 = new Image(image6Region);
		image6.setAlign(Align.center);
		mainTable.add(image6).minSize(image6.getWidth(), image6.getHeight())
		.spaceBottom(IMAGE_SPACE_BOTTOM).spaceTop(IMAGE_SPACE_TOP);
		mainTable.row();
		
		Label label7 = new Label("HAVE FUN!", skin);
		label7.setAlignment(Align.center);
		label7.setWrap(true);
		mainTable.add(label7).minWidth(width-6).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		TextButton backToMenuButton2 = new TextButton("Back to menu", skin);
		mainTable.add(backToMenuButton2).minSize(width*3/4-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		mainTable.add().minSize(Config.menuTopHeightOffset);

		backToMenuButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				backToMenuAction();
			}
		});
		
		backToMenuButton2.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				backToMenuAction();
			}
		});
	}
	
	private void backToMenuAction() {
		startScreen.setToMainMenu();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(54f/255f, 47f/255f, 45f/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
//		if(Config.debug) Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		if(viewport!=null) viewport.update(width, height, true);
	}

	@Override
	public void hide() {
		stage = null;
		skin = null;
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
