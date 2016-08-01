package com.luke.clones.screen;

import java.util.ArrayList;

import org.apache.commons.lang3.RandomUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.luke.clones.config.Config;
import com.luke.clones.map.model.AvailableMapModelsType;
import com.luke.clones.map.model.MapModel;
import com.luke.clones.map.model.MapModelUtils;
import com.luke.clones.model.Map;
import com.luke.clones.model.MapAutoCreator;
import com.luke.clones.model.SpecialKeyHandler;
import com.luke.clones.model.MapAutoCreator.MapCreatorArgumentException;
import com.luke.clones.model.type.PlayerMoveTimeLimitType;
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

public class ScreenLocalMultiplayer implements Screen{
	private StartScreen startScreen;
	private int width, height;
	private Viewport viewport;
	private Stage stage;
	private Skin skin;
	private ScrollPane mainPane;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;

	public ScreenLocalMultiplayer(StartScreen startScreen){
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
		
		buildGameOptions();
	}

	@Override
	public void hide() {
		stage = null;
		skin = null;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(54f/255f, 47f/255f, 45f/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
	}

	private void buildGameOptions(){
		Table mainTable = new Table(skin);
		mainPane = new ScrollPane(mainTable, skin);
		mainPane.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainPane.setSize(width-Config.widthOffset/2, height-(Config.menuTopHeightOffset+Config.menuBottomHeightOffset));
		
		mainTable.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainTable.setSize(width-Config.widthOffset/2, height-(Config.menuTopHeightOffset+Config.menuBottomHeightOffset));
		mainTable.setBackground(skin.getDrawable("default-scroll"));
		mainTable.align(Align.top);
		
		stage.addActor(mainPane);
		
		//Back button
		TextButton backButton = new TextButton("Back to menu", skin);
		mainTable.add(backButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		mainTable.row();
		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				 Gdx.app.postRunnable(new Runnable() {
			         public void run() {
			        	 SoundManager.INSTANCE.play(SoundType.CLICK);
			        	 startScreen.setToMainMenu();
			         }});
			}
		});
		
		//Players Number Label
		TextButton playersNumLabel = new TextButton("Players Number", skin);
		playersNumLabel.setDisabled(true);
		mainTable.add(playersNumLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		
		//Players Number SelectBox
		final SelectBox<Integer> playersNumSelectBox = new SelectBox<Integer>(skin);
		playersNumSelectBox.setItems(new Integer[]{2,4});
		playersNumSelectBox.setSelectedIndex(0);
//		final SelectBox playersNumSelectBox = new SelectBox(new Integer[]{2,4}, skin);
//		playersNumSelectBox.setSelection(0);
		mainTable.add(playersNumSelectBox).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		TextButton playerMoveTimeLimitLabel = new TextButton("Player move time limit", skin);
		playerMoveTimeLimitLabel.setDisabled(true);
		mainTable.add(playerMoveTimeLimitLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		final PlayerMoveTimeLimitType[] playerMoveTimeLimitSelectBoxItems = new PlayerMoveTimeLimitType[]{
				PlayerMoveTimeLimitType.T5S,
				PlayerMoveTimeLimitType.T10S,
				PlayerMoveTimeLimitType.T15S,
				PlayerMoveTimeLimitType.T30S,
				PlayerMoveTimeLimitType.T45S,
				PlayerMoveTimeLimitType.T60S,
				PlayerMoveTimeLimitType.NO_LIMIT};
		
		final SelectBox<PlayerMoveTimeLimitType> playerMoveTimeLimitSelectBox = new SelectBox<PlayerMoveTimeLimitType>(skin);
		playerMoveTimeLimitSelectBox.setItems(playerMoveTimeLimitSelectBoxItems);
		playerMoveTimeLimitSelectBox.setSelectedIndex(2);
//		final SelectBox playerMoveTimeLimitSelectBox = new SelectBox(playerMoveTimeLimitSelectBoxItems, skin);
//		playerMoveTimeLimitSelectBox.setSelection(2);
		
		mainTable.add(playerMoveTimeLimitSelectBox).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		MapModel[] twoPlayerMaps = selectMapsWithSpecifiedNumberOfPlayers(2);
		MapModel[] fourPlayerMaps = selectMapsWithSpecifiedNumberOfPlayers(4);
		final String[] twoPlayerMapNames = MapModelUtils.resolveMapsToNames(twoPlayerMaps);
		final String[] fourPlayerMapNames = MapModelUtils.resolveMapsToNames(fourPlayerMaps);

		//Map Size Label
		TextButton mapLabel = new TextButton("Map", skin);
		mapLabel.setDisabled(true);
		mainTable.add(mapLabel).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		//Map Size SelectBox
		final SelectBox<String> mapSelectBox = new SelectBox<String>(skin);
		mapSelectBox.setItems(twoPlayerMapNames);
		mapSelectBox.setSelectedIndex(RandomUtils.nextInt(0, mapSelectBox.getItems().size));
//		final SelectBox mapSelectBox = new SelectBox(twoPlayerMapNames, skin);
//		mapSelectBox.setSelection(0);
		mainTable.add(mapSelectBox).minSize(width-6, 50).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		playersNumSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
//				int playersNumber = Integer.valueOf(playersNumSelectBox.getSelection());
				int playersNumber = Integer.valueOf(playersNumSelectBox.getSelected());
				if(playersNumber==2){
					mapSelectBox.setItems(twoPlayerMapNames);
				}
				else{
					mapSelectBox.setItems(fourPlayerMapNames);
				}
			}
		});
		
		//Start Game Button
		TextButton startGameButton = new TextButton("Start Game", skin);
		startGameButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				try {
//					String mapName = mapSelectBox.getSelection();
					SoundManager.INSTANCE.play(SoundType.CLICK);
					String mapName = mapSelectBox.getSelected();
					Map map = MapAutoCreator.createMap(MapModelUtils.resolveNameToMap(mapName));
//					PlayerMoveTimeLimitType playerMoveTimeLimitType = playerMoveTimeLimitSelectBoxItems[playerMoveTimeLimitSelectBox.getSelectionIndex()];
					PlayerMoveTimeLimitType playerMoveTimeLimitType = playerMoveTimeLimitSelectBoxItems[playerMoveTimeLimitSelectBox.getSelectedIndex()];
					startScreen.setToLocalMultiplayerGame(map, playerMoveTimeLimitType);
					startScreen.getAndroidNativeHandler().hideBannerAds();
				} catch (MapCreatorArgumentException e) {
					e.printStackTrace();
				}
			}
		});
		mainTable.add(startGameButton).minSize(width-6, 70).spaceBottom(3).spaceTop(3);
	}
	
	private MapModel[] selectMapsWithSpecifiedNumberOfPlayers(int numberOfPlayers){
		ArrayList<MapModel> selectedMaps = new ArrayList<MapModel>();
		for(AvailableMapModelsType availableMapModelsType: AvailableMapModelsType.values()){
			if(availableMapModelsType.getMap().getNumberOfPlayers()==numberOfPlayers){
				selectedMaps.add(availableMapModelsType.getMap());
			}
		}
		return selectedMaps.toArray(new MapModel[0]);
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
	

}
