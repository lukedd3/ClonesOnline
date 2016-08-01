package com.luke.clones.screen;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.luke.clones.config.Config;
import com.luke.clones.form.validation.FieldError;
import com.luke.clones.form.validation.FieldErrorReasonType;
import com.luke.clones.form.validation.FieldType;
import com.luke.clones.form.validation.FieldTypeReportBug;
import com.luke.clones.github.connector.BadResponseCodeException;
import com.luke.clones.github.connector.GithubConnector;
import com.luke.clones.messages.QuickMessage;
import com.luke.clones.messages.QuickMessageManager;
import com.luke.clones.messages.QuickMessageType;
import com.luke.clones.model.SpecialKeyHandler;
import com.luke.clones.sound.SoundManager;
import com.luke.clones.sound.SoundType;
import com.luke.clones.util.FormValidationUtil;

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

public class ScreenReportBug implements Screen{
	private StartScreen startScreen;
	private int width, height;
	private Viewport viewport;
	private Stage stage;
	private Skin skin, smallSkin;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;
	
	private ScrollPane mainPane;
	private Table mainTable;
	
	private GithubConnector githubConnector;
	
	public ScreenReportBug(StartScreen startScreen) {
		this.startScreen = startScreen;
		githubConnector = new GithubConnector();
	}

	@Override
	public void show() {
		width = Config.width;
		height = Config.height;
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
		smallSkin = new Skin(Gdx.files.internal("data/skins/skin_small.json"));	
		
		buildContent();
	}
	
	private synchronized void buildContent(){
		prepareMainTable();
		clearTable();
		addForm();
	}
	
	private synchronized void prepareMainTable(){
		mainTable = new Table(skin);
		mainPane = new ScrollPane(mainTable, skin);
		mainPane.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainPane.setSize(width-Config.widthOffset/2, height-(Config.menuTopHeightOffset+Config.menuBottomHeightOffset));
		
		mainTable.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainTable.setSize(width-Config.widthOffset/2, height-(Config.menuBottomHeightOffset+Config.menuTopHeightOffset));
		mainTable.setBackground(skin.getDrawable("default-scroll"));
		
		stage.addActor(mainPane);
	}
	
	private synchronized void addBackToMenuButton(){
		TextButton backToMenuButton = new TextButton("Back to menu", skin);
		mainTable.add(backToMenuButton).minSize(width*3/4-6, 70).spaceBottom(Config.menuTopHeightOffset+3).spaceTop(3);
		
		mainTable.row();
		
		backToMenuButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				startScreen.setToMainMenu();
			}
		}); 
	}
	
	private synchronized void addForm(){
		addForm(new ArrayList<FieldError<FieldTypeReportBug>>(), new HashMap<FieldTypeReportBug, String>());
	}
	
	private synchronized <T extends Enum<T> & FieldType> void addForm(List<FieldError<T>> fieldErrors, Map<T, String> fieldValuesOld){
		addBackToMenuButton();
		
		TextButton titleLabel = new TextButton("*Title:", skin);
		titleLabel.setDisabled(true);
		mainTable.add(titleLabel).minSize(width-6, 35).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextField titleTextField = new TextField("", skin);
		titleTextField.setMaxLength(100);
		mainTable.add(titleTextField).minSize(width-6, 45).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		
		T fieldErrorType = (T) FieldTypeReportBug.TITLE;
		if(FieldError.containsFieldErrorType(fieldErrors, fieldErrorType)){
			FieldError<T> fieldError = FieldError.getByFieldErrorType(fieldErrors, fieldErrorType);
			String errorMessage = "Wrong field data";
			if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.EMPTY){
				errorMessage = "This field cannot be empty";
			}
			else if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.NOT_ALPHANUMERIC){
				errorMessage ="Title contains prohibited characters like \"\\\"";
			}
			
			Label errorLabel = new Label(errorMessage, smallSkin);
			errorLabel.setColor(Color.RED);
			mainTable.add(errorLabel).spaceBottom(3).spaceTop(3);
			mainTable.row();
		}
		
		TextButton descriptionLabel = new TextButton("*Bug/suggestion description:", skin);
		descriptionLabel.setDisabled(true);
		mainTable.add(descriptionLabel).minSize(width-6, 35).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextArea descriptionTextArea = new TextArea("", skin);
		descriptionTextArea.setMaxLength(1024);
		mainTable.add(descriptionTextArea).minSize(width-6, 240).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		fieldErrorType = (T) FieldTypeReportBug.DESCRIPTION;
		if(FieldError.containsFieldErrorType(fieldErrors, fieldErrorType)){
			FieldError fieldError = FieldError.getByFieldErrorType(fieldErrors, fieldErrorType);
			String errorMessage = "Wrong field data";
			if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.EMPTY){
				errorMessage = "This field cannot be empty";
			}
			else if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.NOT_ALPHANUMERIC){
				errorMessage ="Description contains prohibited characters like \"\\\"";
			}
			
			Label errorLabel = new Label(errorMessage, smallSkin);
			errorLabel.setColor(Color.RED);
			mainTable.add(errorLabel).spaceBottom(3).spaceTop(3);
			mainTable.row();
		}
		
		TextButton nameLabel = new TextButton("Your name:", skin);
		nameLabel.setDisabled(true);
		mainTable.add(nameLabel).minSize(width-6, 35).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextField nameTextField = new TextField("", skin);
		nameTextField.setMaxLength(100);
		mainTable.add(nameTextField).minSize(width-6, 45).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		fieldErrorType = (T) FieldTypeReportBug.NAME;
		if(FieldError.containsFieldErrorType(fieldErrors, fieldErrorType)){
			FieldError fieldError = FieldError.getByFieldErrorType(fieldErrors, fieldErrorType);
			String errorMessage = "Wrong field data";
			if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.NOT_ALPHANUMERIC){
				errorMessage ="Name contains prohibited characters like \"\\\"";
			}
			
			Label errorLabel = new Label(errorMessage, smallSkin);
			errorLabel.setColor(Color.RED);
			mainTable.add(errorLabel).spaceBottom(3).spaceTop(3);
			mainTable.row();
		}
		
		TextButton emailLabel = new TextButton("Your email:", skin);
		emailLabel.setDisabled(true);
		mainTable.add(emailLabel).minSize(width-6, 35).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextField emailTextField = new TextField("", skin);
		emailTextField.setMaxLength(100);
		mainTable.add(emailTextField).minSize(width-6, 45).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		fieldErrorType = (T) FieldTypeReportBug.EMAIL;
		if(FieldError.containsFieldErrorType(fieldErrors, fieldErrorType)){
			System.out.println("email error");
			FieldError fieldError = FieldError.getByFieldErrorType(fieldErrors, fieldErrorType);
			String errorMessage = "Wrong field data";
			if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.NOT_EMAIL){
				errorMessage ="This is not a valid email address";
			}
			
			Label errorLabel = new Label(errorMessage, smallSkin);
			errorLabel.setColor(Color.RED);
			mainTable.add(errorLabel).spaceBottom(3).spaceTop(3);
			mainTable.row();
		}
		
		final TextButton sendButton = new TextButton("Send", skin);
		mainTable.add(sendButton).minSize(width-6, 60).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		Label explanationLabel = new Label("*Required Fields", skin);
		mainTable.add(explanationLabel);
		
		FieldTypeReportBug fieldToSetType = FieldTypeReportBug.TITLE;
		if(fieldValuesOld.containsKey(fieldToSetType)){
			titleTextField.setText(fieldValuesOld.get(fieldToSetType));
		}
		fieldToSetType = FieldTypeReportBug.DESCRIPTION;
		if(fieldValuesOld.containsKey(fieldToSetType)){
			descriptionTextArea.setText(fieldValuesOld.get(fieldToSetType));
		}
		fieldToSetType = FieldTypeReportBug.NAME;
		if(fieldValuesOld.containsKey(fieldToSetType)){
			nameTextField.setText(fieldValuesOld.get(fieldToSetType));
		}
		fieldToSetType = FieldTypeReportBug.EMAIL;
		if(fieldValuesOld.containsKey(fieldToSetType)){
			emailTextField.setText(fieldValuesOld.get(fieldToSetType));
		}
		
		sendButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				List<FieldError<FieldTypeReportBug>> fieldErrors = new ArrayList<FieldError<FieldTypeReportBug>>();

				if(FormValidationUtil.isFieldEmpty(titleTextField.getText())){
					fieldErrors.add(new FieldError(FieldTypeReportBug.TITLE, FieldErrorReasonType.EMPTY));
				}
				else if(!FormValidationUtil.isFieldOnlyAlfanumeric(titleTextField.getText())){
					fieldErrors.add(new FieldError(FieldTypeReportBug.TITLE, FieldErrorReasonType.NOT_ALPHANUMERIC));
				}
				
				if(FormValidationUtil.isFieldEmpty(descriptionTextArea.getText())){
					fieldErrors.add(new FieldError(FieldTypeReportBug.DESCRIPTION, FieldErrorReasonType.EMPTY));
				}
				else if(!FormValidationUtil.isFieldOnlyAlfanumeric(descriptionTextArea.getText())){
					fieldErrors.add(new FieldError(FieldTypeReportBug.DESCRIPTION, FieldErrorReasonType.NOT_ALPHANUMERIC));
				}
				
				if(!FormValidationUtil.isFieldEmpty(nameTextField.getText())){
					if(!FormValidationUtil.isFieldOnlyAlfanumeric(nameTextField.getText())){
						fieldErrors.add(new FieldError(FieldTypeReportBug.NAME, FieldErrorReasonType.NOT_ALPHANUMERIC));
					}
				}
				
				if(!FormValidationUtil.isFieldEmpty(emailTextField.getText())){
					if(!FormValidationUtil.isFieldEmail(emailTextField.getText())){
						fieldErrors.add(new FieldError(FieldTypeReportBug.EMAIL, FieldErrorReasonType.NOT_EMAIL));
					}
				}
				
				Map<FieldTypeReportBug, String> fieldValuesOld = new HashMap<FieldTypeReportBug, String>();
				fieldValuesOld.put(FieldTypeReportBug.TITLE, titleTextField.getText());
				fieldValuesOld.put(FieldTypeReportBug.DESCRIPTION, descriptionTextArea.getText());
				fieldValuesOld.put(FieldTypeReportBug.NAME, nameTextField.getText());
				fieldValuesOld.put(FieldTypeReportBug.EMAIL, emailTextField.getText());
				
				if(fieldErrors.size()>0){
					clearTable();
					addForm(fieldErrors, fieldValuesOld);
					System.out.println(fieldErrors);
					return;
				}
				
				try {
					
					String title = titleTextField.getText();
					String description = descriptionTextArea.getText().replace("\n", "\\n");
					description+="\\n\\n";
					description+="Author name: "+nameTextField.getText();
					description+="\\n";
					description+="Author email: "+emailTextField.getText();
					description+="\\n";
					description+="Game version: "+startScreen.getAndroidNativeHandler().getAppVersionName();
					description+="\\n";
					description+="Android version: "+startScreen.getAndroidNativeHandler().getAndroidVersionString();
					description+="\\n";
					description+="Device: "+startScreen.getAndroidNativeHandler().getDeviceVersionString();
					
					githubConnector.sendIssue(title, description);
					
					QuickMessage quickMessage = new QuickMessage("Bug/suggestion report sent. Thanks for your help!", skin);
					quickMessage.setQuickMessageType(QuickMessageType.INFO);
					quickMessage.setAutoHide(false);
					startScreen.setToMainMenuWithMessage(quickMessage);
					
				}catch (NoRouteToHostException | UnknownHostException e) {
					e.printStackTrace();
					clearTable();
					addForm(fieldErrors, fieldValuesOld);
					QuickMessage quickMessage = new QuickMessage("Cannot send report. Probably because you are not connected to the Internet.", skin);
					quickMessage.setAutoHide(false);
					QuickMessageManager.showMessageOnSpecifiedStage(quickMessage, stage);
				} catch (BadResponseCodeException | IOException e) {
					e.printStackTrace();
					clearTable();
					addForm(fieldErrors, fieldValuesOld);
					QuickMessage quickMessage = new QuickMessage("Cannot send report. Try again later.", skin);
					quickMessage.setAutoHide(false);
					QuickMessageManager.showMessageOnSpecifiedStage(quickMessage, stage);
				}
			}
		});
		
	}
	
	private void clearTable(){
		mainTable.clear();
		mainTable.align(Align.top);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(54f/255f, 47f/255f, 45f/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
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
	public void hide() {
		stage = null;
		skin = null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
