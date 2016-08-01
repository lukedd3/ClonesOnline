package com.luke.clones.messages;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.luke.clones.config.Config;
import com.luke.clones.model.TimeMeasure;
import com.luke.clones.screen.StartScreen;

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

public class QuickMessage {
	private final static int IMAGE_SIZE = 88;
	private final static float ANIMATION_DURATION = 0.15f;
	
	private String text ="";
	private String optionalTitle="";
	private String optionalAdditionalText="";
	private boolean autoHide = true;
	private int autoHideTimeInSecods = 5;
	private boolean showConfirmButton = true;
	private String confirmButtonText = "ok";
	private boolean showRateContent = false;
	private QuickMessageType quickMessageType = QuickMessageType.INFO;
	private StartScreen startScreen;
	
	private Skin skin;
	private Table messageTable;
	private volatile TimeMeasure timeMeasure;
	private Timer messageTimer;
	private volatile Label timeTextLabel;
	
	private Skin smallSkin;

	public QuickMessage(String text, Skin skin, Skin smallSkin) {
		this.text = text;
		this.skin = skin;
		this.smallSkin = smallSkin;
		init();
	}
	
	public QuickMessage(String text, Skin skin) {
		this.text = text;
		this.skin = skin;
		this.smallSkin = skin;
		init();
	}
	
	private void init(){
		messageTable = new Table(skin);
		messageTable.setBackground(skin.getDrawable("message-round-large-dark"));
		messageTable.setSize(Gdx.graphics.getWidth(), 150);
		messageTable.setPosition(Gdx.graphics.getWidth()/2-messageTable.getWidth()/2, Gdx.graphics.getHeight()+messageTable.getHeight());
		
		timeMeasure = new TimeMeasure();
	}
	
	public synchronized void prepareToShow(){
		prepareAutoHide();
		prepareContent();
		inAnimation();
	}
	
	private synchronized void prepareAutoHide(){
		if(autoHide){
			timeMeasure.start();
			
			messageTimer = new Timer(true);

			TimerTask hideMessageTask = new TimerTask() {
				@Override
				public void run() {
					Gdx.app.postRunnable(new Runnable() {
				        public void run() {
				        	updateTimeText();
				        }
					});
					if(timeMeasure.getMeasuredTimeInMs()>=autoHideTimeInSecods*1000){
						Gdx.app.postRunnable(new Runnable() {
					        public void run() {
					        	hideMessage();
					        }
						});
					}
				}
			};
			
			messageTimer.scheduleAtFixedRate(hideMessageTask, 0, 1000);
		}
	}
	
	private synchronized void prepareContent(){
		messageTable.clear();
		messageTable.align(Align.top);
//		messageTable.debug();
		
		Label spaceLabel = new Label("_", skin);
		
		Table twoColumnTable = new Table();
		twoColumnTable.setWidth(messageTable.getWidth());
		Table leftColumn = new Table();
		leftColumn.setWidth(IMAGE_SIZE);
		Table rightColumn = new Table();
		rightColumn.setWidth(twoColumnTable.getWidth()-IMAGE_SIZE);
		
		leftColumn.add(prepareImage()).size(IMAGE_SIZE, IMAGE_SIZE).left().top();
		
		if(optionalTitle.length()>0){
			rightColumn.add(prepareOptionalTitle()).width(twoColumnTable.getWidth()-IMAGE_SIZE).spaceBottom(7);
			rightColumn.row();
		}
		rightColumn.add(prepareText()).width(twoColumnTable.getWidth()-IMAGE_SIZE);
		rightColumn.row();
		if(optionalAdditionalText.length()>0){
			rightColumn.add(prepareOptionalAdditionalText()).width(twoColumnTable.getWidth()-IMAGE_SIZE);
			rightColumn.row();
		}
		
		twoColumnTable.add(leftColumn);
		twoColumnTable.add(rightColumn);
		messageTable.add(twoColumnTable);
		
		messageTable.row();
		
		if(showRateContent){
			messageTable.setHeight(messageTable.getHeight()+60);
			messageTable.add(prepareRateContent()).spaceTop(15).spaceBottom(10);
			messageTable.row();
		}
		
		if(autoHide){
			messageTable.setHeight(messageTable.getHeight()+spaceLabel.getHeight());
			messageTable.add(prepareTimeText()).colspan(2);
			messageTable.row();
		}
		if(showConfirmButton){
			messageTable.add(prepareConfirmButton()).colspan(2).spaceTop(5).spaceBottom(3).minSize(Config.width*1/5-6, 35);
			messageTable.row();
		}
		
	}
	
	private Image prepareImage(){
		String texturePath = "data/messages/info.png";
		if(quickMessageType==QuickMessageType.INFO){
			texturePath = "data/messages/info.png";
		}
		else if(quickMessageType==QuickMessageType.ALERT){
			texturePath = "data/messages/alert.png";
		}
		else if(quickMessageType==QuickMessageType.ACHIEVEMENT_GAMES_WON){
			texturePath = "data/messages/achievement_won_games.png";
		}
		else if(quickMessageType==QuickMessageType.ACHIEVEMENT_GAMES_PLAYED){
			texturePath = "data/messages/achievement_played_games.png";
		}
		else if(quickMessageType==QuickMessageType.ACHIEVEMENT_PLAY_TIME){
			texturePath = "data/messages/achievement_time.png";
		}
		else if(quickMessageType==QuickMessageType.ACHIEVEMENT_GAMES_WON_IN_ROW){
			texturePath = "data/messages/achievement_won_in_row.png";
		}
		
		Texture imageTexture = new Texture(Gdx.files.internal(texturePath));
		imageTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Image image = new Image(imageTexture);
		image.setColor(Config.textColor);
		return image;
	}
	
	private Label prepareOptionalTitle(){
		Label textLabel = new Label(optionalTitle, smallSkin);
		textLabel.setWrap(true);
		textLabel.setAlignment(Align.center);
		return textLabel;
	}
	
	private Label prepareText(){
		Label textLabel = new Label(text, skin);
		textLabel.setWrap(true);
		textLabel.setAlignment(Align.center);
		return textLabel;
	}
	
	private Label prepareOptionalAdditionalText(){
		Label textLabel = new Label(optionalAdditionalText, smallSkin);
		textLabel.setWrap(true);
		textLabel.setAlignment(Align.center);
		return textLabel;
	}
	
	private Table prepareRateContent(){
		Label textLabel = new Label("If you have a moment please rate Clones on the Play Store", smallSkin);
		textLabel.setWrap(true);
		textLabel.setAlignment(Align.center);
		
		//179x47
		Texture clonesPlayLogoTexture = new Texture(Gdx.files.internal("data/clonesplaylogo.png"));
		clonesPlayLogoTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Texture clonesPlayLogoAltTexture = new Texture(Gdx.files.internal("data/clonesplaylogoalt.png"));
		clonesPlayLogoAltTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		final TextureRegion clonesPlayLogoRegion = new TextureRegion(clonesPlayLogoTexture, 0, 94, 256, 161-94);
		final TextureRegion clonesPlayLogoAltRegion = new TextureRegion(clonesPlayLogoAltTexture, 0, 94, 256, 161-94);
		Image rateImage = new Image(clonesPlayLogoRegion);
		rateImage.setAlign(Align.center);
		
		rateImage.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
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
		
		Table table = new Table();
		table.add(textLabel).width(messageTable.getWidth()/2).minHeight(47);
		
		Table tempTable = new Table();
		tempTable.add(rateImage).maxWidth(179).maxHeight(47);
		table.add(tempTable).width(messageTable.getWidth()/2).minHeight(47);
		
		return table;
	}
	
	private TextButton prepareConfirmButton(){
			TextButton confirmButton = new TextButton(confirmButtonText, skin);
			confirmButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					hideMessage();
				}
			});
			return confirmButton;
	}
	
	private Label prepareTimeText(){
			timeTextLabel = new Label(""+autoHideTimeInSecods+"s", skin);
			return timeTextLabel;
	}
	
	private synchronized void inAnimation(){
		messageTable.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2-messageTable.getWidth()/2, Gdx.graphics.getHeight()/2-messageTable.getHeight()/2, ANIMATION_DURATION));
	}
	
	private synchronized void outAnimation(){
		SequenceAction sequenceAction = new SequenceAction();
		sequenceAction.addAction(Actions.moveTo(Gdx.graphics.getWidth()/2-messageTable.getWidth()/2, 0-messageTable.getHeight(), ANIMATION_DURATION));
		sequenceAction.addAction(new HideMessageAction());
		messageTable.addAction(sequenceAction);
	}
	
	private class HideMessageAction extends Action{
		@Override
		public boolean act(float delta) {
			messageTable.remove();
			return true;
		}
	}
	
	private void hideMessage(){
		if(autoHide){
			messageTimer.cancel();
		}
		outAnimation();
	}
	
	private void updateTimeText(){
		if(autoHide){
			int measuredTimeInSeconds = Math.round(timeMeasure.getMeasuredTimeInMs()/1000f);
			int timeToClose = autoHideTimeInSecods - measuredTimeInSeconds;
			timeTextLabel.setText(timeToClose+"s");
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isAutoHide() {
		return autoHide;
	}

	public void setAutoHide(boolean autoHide) {
		this.autoHide = autoHide;
	}

	public int getAutoHideTimeInSecods() {
		return autoHideTimeInSecods;
	}

	public void setAutoHideTimeInSecods(int autoHideTimeInSecods) {
		this.autoHideTimeInSecods = autoHideTimeInSecods;
	}

	public boolean isShowConfirmButton() {
		return showConfirmButton;
	}

	public void setShowConfirmButton(boolean showConfirmButton) {
		this.showConfirmButton = showConfirmButton;
	}

	public String getConfirmButtonText() {
		return confirmButtonText;
	}

	public void setConfirmButtonText(String confirmButtonText) {
		this.confirmButtonText = confirmButtonText;
	}

	public Table getTable() {
		return messageTable;
	}

	public void setTable(Table table) {
		this.messageTable = table;
	}

	public QuickMessageType getQuickMessageType() {
		return quickMessageType;
	}

	public void setQuickMessageType(QuickMessageType quickMessageType) {
		this.quickMessageType = quickMessageType;
	}

	public String getOptionalTitle() {
		return optionalTitle;
	}

	public void setOptionalTitle(String optionalTitle) {
		this.optionalTitle = optionalTitle;
	}

	public String getOptionalAdditionalText() {
		return optionalAdditionalText;
	}

	public void setOptionalAdditionalText(String optionalAdditionalText) {
		this.optionalAdditionalText = optionalAdditionalText;
	}

	public boolean isShowRateContent() {
		return showRateContent;
	}

	public void setShowRateContent(boolean showRateContent, StartScreen startScreen) {
		this.showRateContent = showRateContent;
		this.startScreen = startScreen;
	}
	
}
