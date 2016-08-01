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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.luke.clones.config.Config;
import com.luke.clones.form.validation.FieldError;
import com.luke.clones.form.validation.FieldErrorReasonType;
import com.luke.clones.form.validation.FieldType;
import com.luke.clones.form.validation.FieldTypeReviewBeta;
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

public class ScreenReviewBeta implements Screen{
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
	
	private TextureRegion starEmptyRegion;
	private TextureRegion starFullRegion;
	
	private List<Image> stars;
	private int starsGrade = -1;

	public ScreenReviewBeta(StartScreen startScreen) {
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
		
		starsGrade = -1;
		
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
		addForm(new ArrayList<FieldError<FieldTypeReviewBeta>>(), new HashMap<FieldTypeReviewBeta, String>());
	}
	
	private synchronized <T extends Enum<T> & FieldType> void addForm(List<FieldError<T>> fieldErrors, Map<T, String> fieldValuesOld){
		addBackToMenuButton();
		
		TextButton gradeLabel = new TextButton("Grade:", skin);
		gradeLabel.setDisabled(true);
		mainTable.add(gradeLabel).minSize(width-6, 35).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		Table starsTable = new Table();
		
		starEmptyRegion = new TextureRegion(new Texture(Gdx.files.internal("data/images/star_empty.png")), 0, 0, 64, 64);
		starFullRegion = new TextureRegion(new Texture(Gdx.files.internal("data/images/star_full.png")), 0, 0, 64, 64);
		
		stars = new ArrayList<Image>(5);
		
		for(int i=0; i<5; i++){
			stars.add( new Image(starEmptyRegion));
			stars.get(i).setAlign(Align.center);
			stars.get(i).addListener(new StarClickListener(i));
			starsTable.add(stars.get(i)).size(64,64).spaceRight(15);
		}
		
		mainTable.add(starsTable);
		
		setStarsByStarsGrade();
		
		mainTable.row();
		
		T fieldErrorType = (T) FieldTypeReviewBeta.STARS;
		
		if(FieldError.containsFieldErrorType(fieldErrors, fieldErrorType)){
			FieldError<T> fieldError = FieldError.getByFieldErrorType(fieldErrors, fieldErrorType);
			String errorMessage = "Wrong field data";
			if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.EMPTY){
				errorMessage = "You have to choose at least one star";
			}
			
			Label errorLabel = new Label(errorMessage, smallSkin);
			errorLabel.setColor(Color.RED);
			mainTable.add(errorLabel).spaceBottom(3).spaceTop(3);
			mainTable.row();
		}
		
		TextButton positivesLabel = new TextButton("*Positives:", skin);
		positivesLabel.setDisabled(true);
		mainTable.add(positivesLabel).minSize(width-6, 35).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextArea positivesTextArea = new TextArea("", skin);
		positivesTextArea.setMaxLength(200);//1024
		mainTable.add(positivesTextArea).minSize(width-6, 120).spaceBottom(3).spaceTop(3); //240
		mainTable.row();
		
		fieldErrorType = (T) FieldTypeReviewBeta.POSITIVES;
		
		if(FieldError.containsFieldErrorType(fieldErrors, fieldErrorType)){
			FieldError<T> fieldError = FieldError.getByFieldErrorType(fieldErrors, fieldErrorType);
			String errorMessage = "Wrong field data";
			if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.EMPTY){
				errorMessage = "This field cannot be empty";
			}
			else if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.NOT_ALPHANUMERIC){
				errorMessage ="This field contains prohibited characters like \"\\\"";
			}
			
			Label errorLabel = new Label(errorMessage, smallSkin);
			errorLabel.setColor(Color.RED);
			mainTable.add(errorLabel).spaceBottom(3).spaceTop(3);
			mainTable.row();
		}
		
		TextButton negativesLabel = new TextButton("*Negatives:", skin);
		negativesLabel.setDisabled(true);
		mainTable.add(negativesLabel).minSize(width-6, 35).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextArea negativesTextArea = new TextArea("", skin);
		negativesTextArea.setMaxLength(200);//1024
		mainTable.add(negativesTextArea).minSize(width-6, 120).spaceBottom(3).spaceTop(3); //240
		mainTable.row();
		
		fieldErrorType = (T) FieldTypeReviewBeta.NEGATIVES;
		
		if(FieldError.containsFieldErrorType(fieldErrors, fieldErrorType)){
			FieldError<T> fieldError = FieldError.getByFieldErrorType(fieldErrors, fieldErrorType);
			String errorMessage = "Wrong field data";
			if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.EMPTY){
				errorMessage = "This field cannot be empty";
			}
			else if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.NOT_ALPHANUMERIC){
				errorMessage ="This field contains prohibited characters like \"\\\"";
			}
			
			Label errorLabel = new Label(errorMessage, smallSkin);
			errorLabel.setColor(Color.RED);
			mainTable.add(errorLabel).spaceBottom(3).spaceTop(3);
			mainTable.row();
		}
		
		TextButton generalOpinionLabel = new TextButton("*General opinion:", skin);
		generalOpinionLabel.setDisabled(true);
		mainTable.add(generalOpinionLabel).minSize(width-6, 35).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextArea generalOpinionTextArea = new TextArea("", skin);
		generalOpinionTextArea.setMaxLength(424);//1024
		mainTable.add(generalOpinionTextArea).minSize(width-6, 180).spaceBottom(3).spaceTop(3); //240
		mainTable.row();
		
		fieldErrorType = (T) FieldTypeReviewBeta.GENERAL_OPINION;
		
		if(FieldError.containsFieldErrorType(fieldErrors, fieldErrorType)){
			FieldError<T> fieldError = FieldError.getByFieldErrorType(fieldErrors, fieldErrorType);
			String errorMessage = "Wrong field data";
			if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.EMPTY){
				errorMessage = "This field cannot be empty";
			}
			else if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.NOT_ALPHANUMERIC){
				errorMessage ="This field contains prohibited characters like \"\\\"";
			}
			
			Label errorLabel = new Label(errorMessage, smallSkin);
			errorLabel.setColor(Color.RED);
			mainTable.add(errorLabel).spaceBottom(3).spaceTop(3);
			mainTable.row();
		}
		
		TextButton likedMapsLabel = new TextButton("Liked maps:", skin);
		likedMapsLabel.setDisabled(true);
		mainTable.add(likedMapsLabel).minSize(width-6, 35).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextField likedMapsTextArea = new TextArea("", skin);
		likedMapsTextArea.setMaxLength(100);//1024
		mainTable.add(likedMapsTextArea).minSize(width-6, 80).spaceBottom(3).spaceTop(3); //240
		mainTable.row();
		
		fieldErrorType = (T) FieldTypeReviewBeta.LIKED_MAPS;
		if(FieldError.containsFieldErrorType(fieldErrors, fieldErrorType)){
			FieldError fieldError = FieldError.getByFieldErrorType(fieldErrors, fieldErrorType);
			String errorMessage = "Wrong field data";
			if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.NOT_ALPHANUMERIC){
				errorMessage ="This field contains prohibited characters like \"\\\"";
			}
			
			Label errorLabel = new Label(errorMessage, smallSkin);
			errorLabel.setColor(Color.RED);
			mainTable.add(errorLabel).spaceBottom(3).spaceTop(3);
			mainTable.row();
		}
		
		TextButton dislikedMapsLabel = new TextButton("Disliked maps:", skin);
		dislikedMapsLabel.setDisabled(true);
		mainTable.add(dislikedMapsLabel).minSize(width-6, 35).spaceBottom(3).spaceTop(3);
		mainTable.row();
		
		final TextArea dislikedMapsTextArea = new TextArea("", skin);
		dislikedMapsTextArea.setMaxLength(100);//1024
		mainTable.add(dislikedMapsTextArea).minSize(width-6, 80).spaceBottom(3).spaceTop(3); //240
		mainTable.row();
		
		fieldErrorType = (T) FieldTypeReviewBeta.DISLIKED_MAPS;
		if(FieldError.containsFieldErrorType(fieldErrors, fieldErrorType)){
			FieldError fieldError = FieldError.getByFieldErrorType(fieldErrors, fieldErrorType);
			String errorMessage = "Wrong field data";
			if(fieldError.getFieldErrorReasonType()==FieldErrorReasonType.NOT_ALPHANUMERIC){
				errorMessage ="This field contains prohibited characters like \"\\\"";
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
		
		fieldErrorType = (T) FieldTypeReviewBeta.NAME;
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
		
		fieldErrorType = (T) FieldTypeReviewBeta.EMAIL;
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
		
		FieldTypeReviewBeta fieldToSetType = FieldTypeReviewBeta.POSITIVES;
		if(fieldValuesOld.containsKey(fieldToSetType)){
			positivesTextArea.setText(fieldValuesOld.get(fieldToSetType));
		}
		fieldToSetType = FieldTypeReviewBeta.NEGATIVES;
		if(fieldValuesOld.containsKey(fieldToSetType)){
			negativesTextArea.setText(fieldValuesOld.get(fieldToSetType));
		}
		fieldToSetType = FieldTypeReviewBeta.GENERAL_OPINION;
		if(fieldValuesOld.containsKey(fieldToSetType)){
			generalOpinionTextArea.setText(fieldValuesOld.get(fieldToSetType));
		}
		fieldToSetType = FieldTypeReviewBeta.LIKED_MAPS;
		if(fieldValuesOld.containsKey(fieldToSetType)){
			likedMapsTextArea.setText(fieldValuesOld.get(fieldToSetType));
		}
		fieldToSetType = FieldTypeReviewBeta.DISLIKED_MAPS;
		if(fieldValuesOld.containsKey(fieldToSetType)){
			dislikedMapsTextArea.setText(fieldValuesOld.get(fieldToSetType));
		}
		fieldToSetType = FieldTypeReviewBeta.NAME;
		if(fieldValuesOld.containsKey(fieldToSetType)){
			nameTextField.setText(fieldValuesOld.get(fieldToSetType));
		}
		fieldToSetType = FieldTypeReviewBeta.EMAIL;
		if(fieldValuesOld.containsKey(fieldToSetType)){
			emailTextField.setText(fieldValuesOld.get(fieldToSetType));
		}
		
		sendButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				List<FieldError<FieldTypeReviewBeta>> fieldErrors = new ArrayList<FieldError<FieldTypeReviewBeta>>();

				if(starsGrade==-1){
					fieldErrors.add(new FieldError(FieldTypeReviewBeta.STARS, FieldErrorReasonType.EMPTY));
				}
				if(FormValidationUtil.isFieldEmpty(positivesTextArea.getText())){
					fieldErrors.add(new FieldError(FieldTypeReviewBeta.POSITIVES, FieldErrorReasonType.EMPTY));
				}
				else if(!FormValidationUtil.isFieldOnlyAlfanumeric(positivesTextArea.getText())){
					fieldErrors.add(new FieldError(FieldTypeReviewBeta.POSITIVES, FieldErrorReasonType.NOT_ALPHANUMERIC));
				}
				
				if(FormValidationUtil.isFieldEmpty(negativesTextArea.getText())){
					fieldErrors.add(new FieldError(FieldTypeReviewBeta.NEGATIVES, FieldErrorReasonType.EMPTY));
				}
				else if(!FormValidationUtil.isFieldOnlyAlfanumeric(negativesTextArea.getText())){
					fieldErrors.add(new FieldError(FieldTypeReviewBeta.NEGATIVES, FieldErrorReasonType.NOT_ALPHANUMERIC));
				}
				
				if(FormValidationUtil.isFieldEmpty(generalOpinionTextArea.getText())){
					fieldErrors.add(new FieldError(FieldTypeReviewBeta.GENERAL_OPINION, FieldErrorReasonType.EMPTY));
				}
				else if(!FormValidationUtil.isFieldOnlyAlfanumeric(generalOpinionTextArea.getText())){
					fieldErrors.add(new FieldError(FieldTypeReviewBeta.GENERAL_OPINION, FieldErrorReasonType.NOT_ALPHANUMERIC));
				}
				
				if(!FormValidationUtil.isFieldEmpty(likedMapsTextArea.getText())){
					if(!FormValidationUtil.isFieldOnlyAlfanumeric(likedMapsTextArea.getText())){
						fieldErrors.add(new FieldError(FieldTypeReviewBeta.LIKED_MAPS, FieldErrorReasonType.NOT_ALPHANUMERIC));
					}
				}
				
				if(!FormValidationUtil.isFieldEmpty(dislikedMapsTextArea.getText())){
					if(!FormValidationUtil.isFieldOnlyAlfanumeric(dislikedMapsTextArea.getText())){
						fieldErrors.add(new FieldError(FieldTypeReviewBeta.DISLIKED_MAPS, FieldErrorReasonType.NOT_ALPHANUMERIC));
					}
				}
				
				if(!FormValidationUtil.isFieldEmpty(nameTextField.getText())){
					if(!FormValidationUtil.isFieldOnlyAlfanumeric(nameTextField.getText())){
						fieldErrors.add(new FieldError(FieldTypeReviewBeta.NAME, FieldErrorReasonType.NOT_ALPHANUMERIC));
					}
				}
				
				if(!FormValidationUtil.isFieldEmpty(emailTextField.getText())){
					if(!FormValidationUtil.isFieldEmail(emailTextField.getText())){
						fieldErrors.add(new FieldError(FieldTypeReviewBeta.EMAIL, FieldErrorReasonType.NOT_EMAIL));
					}
				}
				
				Map<FieldTypeReviewBeta, String> fieldValuesOld = new HashMap<FieldTypeReviewBeta, String>();
				fieldValuesOld.put(FieldTypeReviewBeta.POSITIVES, positivesTextArea.getText());
				fieldValuesOld.put(FieldTypeReviewBeta.NEGATIVES, negativesTextArea.getText());
				fieldValuesOld.put(FieldTypeReviewBeta.GENERAL_OPINION, generalOpinionTextArea.getText());
				fieldValuesOld.put(FieldTypeReviewBeta.LIKED_MAPS, likedMapsTextArea.getText());
				fieldValuesOld.put(FieldTypeReviewBeta.DISLIKED_MAPS, likedMapsTextArea.getText());
				fieldValuesOld.put(FieldTypeReviewBeta.NAME, nameTextField.getText());
				fieldValuesOld.put(FieldTypeReviewBeta.EMAIL, emailTextField.getText());
				
				if(fieldErrors.size()>0){
					clearTable();
					addForm(fieldErrors, fieldValuesOld);
					System.out.println(fieldErrors);
					return;
				}
				
				try {
					
					String title = "Beta review";
					String description = "";
					description+="Grade: "+starsGradeToVisualStars()+" <"+starsGrade+" star(s)>";
					description+="\\n\\n";
					description+="Positives:\\n";
					description+=positivesTextArea.getText().replace("\n", "\\n");
					description+="\\n\\n";
					description+="Negatives:\\n";
					description+=negativesTextArea.getText().replace("\n", "\\n");
					description+="\\n\\n";
					description+="General opinion:\\n";
					description+=generalOpinionTextArea.getText().replace("\n", "\\n");
					description+="\\n\\n";
					description+="Liked maps:\\n";
					description+=likedMapsTextArea.getText().replace("\n", "\\n");
					description+="\\n\\n";
					description+="Disliked maps:\\n";
					description+=dislikedMapsTextArea.getText().replace("\n", "\\n");
					description+="\\n\\n";
					description+="Author name: "+nameTextField.getText();
					description+="\\n";
					description+="Author email: "+emailTextField.getText();
					githubConnector.sendIssue(title, description);
					
					QuickMessage quickMessage = new QuickMessage("Review sent. Thanks for your opinion!", skin);
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
	
	private String starsGradeToVisualStars(){
		String visualStars = "";
		for(int i=0; i<starsGrade; i++){
			visualStars+="*";
		}
		return visualStars;
	}
	
	private class StarClickListener extends ClickListener{
		private int starIndex;
		
		protected StarClickListener(int starIndex) {
			super();
			this.starIndex = starIndex;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			starsGrade = starIndexToStarsGrade(starIndex);
			setStarsByStarsGrade();
		}
	}
	
	private int starIndexToStarsGrade(int starIndex){
		starIndex++;
		return starIndex;
	}
	
	private void setStarsByStarsGrade(){
		for(int i=0; i<5; i++){
			if(i<starsGrade) stars.get(i).setDrawable(new TextureRegionDrawable(starFullRegion));
			else stars.get(i).setDrawable(new TextureRegionDrawable(starEmptyRegion));
		}
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
