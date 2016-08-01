package com.luke.clones.screen;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.luke.clones.config.Config;
import com.luke.clones.map.creator.DrawFieldButtonTypeChanger;
import com.luke.clones.model.LogicBoard;
import com.luke.clones.model.SpecialKeyHandler;
import com.luke.clones.model.actor.FieldActor2;
import com.luke.clones.model.type.FieldType;
import com.luke.clones.model.type.PlayerPosition;
import com.luke.clones.model.type.PlayerType;
import com.luke.clones.model.type.Position;
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

public class ScreenMapCreator implements Screen{
	private StartScreen startScreen;
	private int width, height;
	private Viewport mainViewport;
	private Stage mainStage;
	private Skin skin;
	private Table mainTable;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;
	
	private int mapHeight;
	private int mapWidth;
	
	private int fieldSize;
	private int unusedSpace;
	FieldListener fieldListener;
	private HashMap<Position,FieldActor2> fieldMap;
	
	private static final FieldType ERASE_FIELD_TYPE = FieldType.EMPTY;
	private static final Color ERASE_FIELD_COLOR = null;
	
	public ScreenMapCreator(StartScreen startScreen){
		this.startScreen = startScreen;
	}

	@Override
	public synchronized void show() {
		width = Config.width;
		height = Config.height;
//		mainStage = new Stage(width, height, false);
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
		
		loadSettingsFromMemory();
		
		buildInterface();
	}

	@Override
	public synchronized void hide() {
		mainStage = null;
		skin = null;
	}

	@Override
	public synchronized void render(float delta) {
		Gdx.gl.glClearColor(54f/255f, 47f/255f, 45f/255f, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		mainStage.act();
		mainStage.draw();
	}
	
	private void loadSettingsFromMemory() {

	}
	
	private void buildInterface() {
		buildMainTable();
		
		TextButton backToMenuButton = new TextButton("Back to menu", skin);
		mainTable.add(backToMenuButton).minSize(width*1/4-6, 50).spaceBottom(3).spaceTop(3);
		
		final TextField xField = new TextField("10", skin);
		mainTable.add(xField).width(width*1/5-6);
		Label fieldSeparator = new Label("x", skin);
		mainTable.add(fieldSeparator);
		final TextField yField = new TextField("10", skin);
		mainTable.add(yField).width(width*1/5-6);
		
		TextButton createNewButton = new TextButton("CN", skin);
		mainTable.add(createNewButton);
		
		TextButton printMapButton = new TextButton("PM", skin);
		mainTable.add(printMapButton);
		
		TextButton changeDrawFieldButton = new TextButton("T", skin);
		mainTable.add(changeDrawFieldButton);
		
		mainTable.row();

		backToMenuButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				startScreen.setToMainMenu();
			}
		});
		
		createNewButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				
				readAndSaveMapSize(xField, yField);
				
				createBoard(mapWidth, mapHeight);
				buildStage(mapWidth, mapHeight);
			}
		});
		
		printMapButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				printMap();
			}
		});
		
		changeDrawFieldButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				DrawFieldButtonTypeChanger.getNext();
			}
		});
		
		readAndSaveMapSize(xField, yField);
		
		createBoard(mapWidth, mapHeight);
		
		buildStage(mapWidth, mapHeight);
	}
	
	private void readAndSaveMapSize(TextField xField, TextField yField){
		mapWidth = Integer.valueOf(xField.getText());
		mapHeight = Integer.valueOf(yField.getText());
	}

	private void buildMainTable(){
		mainTable = new Table(skin);
		mainTable.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainTable.setSize(width-Config.widthOffset/2, height-(Config.menuBottomHeightOffset+Config.menuTopHeightOffset));
		mainTable.setBackground(skin.getDrawable("default-scroll"));
		mainTable.align(Align.top | Align.left);
		
		mainStage.addActor(mainTable);
	}

	private void createBoard(int fieldsInWidth, int fieldsInHeight) {
		
		int screenWidth = Gdx.graphics.getWidth() - Config.widthOffset;
		int screenHeight = Gdx.graphics.getHeight() - Config.gameHeightOffset - Config.timeBarHeight;
		
		fieldListener = new FieldListener();
		
		int xFieldSize = screenWidth/fieldsInHeight;
		int yFieldSize = screenHeight/fieldsInWidth;
		
		if(xFieldSize < yFieldSize) fieldSize = xFieldSize;
		else fieldSize = yFieldSize;
		unusedSpace = (int)mainStage.getWidth() - (fieldSize * fieldsInHeight);
	}
	
	private class FieldListener extends ClickListener{
		boolean click = false;

		@Override
		public void enter(InputEvent event, float x, float y, int pointer,
				Actor fromActor) {
			if(click==true){
				FieldActor2 field = (FieldActor2) event.getListenerActor();
				if(field.getFieldType()!=DrawFieldButtonTypeChanger.getCurrent().getDrawFieldType()){
					field.setFieldType(DrawFieldButtonTypeChanger.getCurrent().getDrawFieldType());
					if(DrawFieldButtonTypeChanger.getCurrent().getDrawFieldType()==FieldType.TAKEN){
						field.setColor(DrawFieldButtonTypeChanger.getCurrent().getDrawFieldColor());
					}
					else{
						field.setColor(ERASE_FIELD_COLOR);
					}
				}
				else{
					field.setFieldType(ERASE_FIELD_TYPE);
					field.setColor(ERASE_FIELD_COLOR);
				}
				
			}
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			SoundManager.INSTANCE.play(SoundType.CLICK);
			click=true;
			return true;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			click=false;
		}
		
		

	}
	
	private void buildStage(int fieldsInWidth, int fieldsInHeight){
		if(fieldMap!=null){
			for(FieldActor2 fieldActor: fieldMap.values()){
				fieldActor.remove();
			}
		}
		
		fieldMap = new HashMap<Position, FieldActor2>();
		
		Texture emptyTexture = new Texture(Gdx.files.internal("data/models/blank.png"));
		Texture solidTexture = new Texture(Gdx.files.internal("data/models/solid.png"));
		Texture ballTexture = new Texture(Gdx.files.internal("data/models/ball.png"));
		
		for(int i=0; i<fieldsInWidth;i++){
			for(int j=0; j<fieldsInHeight;j++){
					FieldActor2 emptyField = new FieldActor2(emptyTexture, solidTexture, ballTexture, emptyTexture, null, new Position(i,j),FieldType.EMPTY);
					emptyField.setPosition((j*fieldSize)+unusedSpace/2, i*fieldSize);
					emptyField.setSize(fieldSize, fieldSize);
					emptyField.addListener(fieldListener);
					mainStage.addActor(emptyField);
					fieldMap.put(emptyField.getPosition(), emptyField);
			}
		}
	}
	
	private void printMap(){
		System.out.println("--- Print Map output ---");
		System.out.println("super.mapSize = new int[]{"+mapWidth+","+mapHeight+"};");
		System.out.println();
		System.out.println("super.solids = new Position[]{");
		for(Position fieldPosition: fieldMap.keySet()){
			FieldActor2 fieldActor = fieldMap.get(fieldPosition);
				if(fieldActor.getFieldType()==FieldType.SOLID){
					System.out.println("new Position("+fieldPosition.getX()+","+fieldPosition.getY()+"),");

			}
		}
		System.out.println("};");
		System.out.println();
		System.out.println("super.playerPositions = new PlayerPosition[]{");
		for(Position fieldPosition: fieldMap.keySet()){
			FieldActor2 fieldActor = fieldMap.get(fieldPosition);
				if(fieldActor.getFieldType()==FieldType.TAKEN){
					System.out.println("new PlayerPosition("+fieldPosition.getX()+", "+fieldPosition.getY()+", PlayerType."+fieldColorToPlayerType(fieldActor.getColor()).toString()+"),");
				}
		}
		System.out.println("};");
		
	}
	
	private PlayerType fieldColorToPlayerType(Color color){
			if(color.equals(Color.BLUE)) {
				return PlayerType.BLUE;
			}
			else if(color.equals(Color.GREEN)){
				return PlayerType.GREEN;
			}
			else if(color.equals(Color.RED)){
				return PlayerType.RED;
			}
			else if(color.equals(Color.ORANGE)){
				return PlayerType.ORANGE;
			}
			else{
				return null;
			}
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
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
