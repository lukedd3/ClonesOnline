package com.luke.clones.screen;

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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.luke.clones.config.Config;
import com.luke.clones.model.SpecialKeyHandler;
import com.luke.clones.sound.SoundManager;
import com.luke.clones.sound.SoundType;

public class ScreenAbout implements Screen{
	private StartScreen startScreen;
	private int width, height;
	private Viewport viewport;
	private Stage stage;
	private Skin skin, smallSkin;
	private ScrollPane mainPane;
	
	private InputMultiplexer inputMultiplexer;
	private SpecialKeyHandler specialKeyHandler;
	
	public ScreenAbout(StartScreen startScreen){
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
		smallSkin = new Skin(Gdx.files.internal("data/skins/skin_small.json"));	
		
		buildContent();
	}
	
	private synchronized void buildContent(){
		Table mainTable = new Table(skin);
		mainPane = new ScrollPane(mainTable, skin);
		mainPane.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainPane.setSize(width-Config.widthOffset/2, height-(Config.menuTopHeightOffset+Config.menuBottomHeightOffset));
		
		mainTable.setPosition(0+Config.widthOffset/2, Config.menuBottomHeightOffset);
		mainTable.setSize(width-Config.widthOffset/2, height-(Config.menuBottomHeightOffset+Config.menuTopHeightOffset));
		mainTable.setBackground(skin.getDrawable("default-scroll"));;
		mainTable.align(Align.top);
		
		stage.addActor(mainPane);
		
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
		
		Label appNameLabel = new Label("Clones", skin);
		appNameLabel.setAlignment(Align.center);
		mainTable.add(appNameLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		Label appVersionLabel = new Label("version "+startScreen.getAndroidNativeHandler().getAppVersionName(), skin);
		appVersionLabel.setAlignment(Align.center);
		mainTable.add(appVersionLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		final TextureRegion clonesFacebookLogoRegion = new TextureRegion(new Texture(Gdx.files.internal("data/clonesfacebooklogo.png")), 0, 95, 256, 162-95);
		final TextureRegion clonesFacebookLogoAltRegion = new TextureRegion(new Texture(Gdx.files.internal("data/clonesfacebooklogoalt.png")), 0, 95, 256, 162-95);
		Image clonesFacebookLogoImage = new Image(clonesFacebookLogoRegion);
		clonesFacebookLogoImage.setAlign(Align.center);
		mainTable.add(clonesFacebookLogoImage);
		
		mainTable.row();
		
		final TextureRegion clonesTwitterLogoRegion = new TextureRegion(new Texture(Gdx.files.internal("data/clonestwitterlogo.png")), 0, 94, 256, 161-94);
		final TextureRegion clonesTwitterLogoAltRegion = new TextureRegion(new Texture(Gdx.files.internal("data/clonestwitterlogoalt.png")), 0, 94, 256, 161-94);
		Image clonesTwitterLogoImage = new Image(clonesTwitterLogoRegion);
		clonesTwitterLogoImage.setAlign(Align.center);
		mainTable.add(clonesTwitterLogoImage);
		
		mainTable.row();
		
		final TextureRegion clonesPlayLogoRegion = new TextureRegion(new Texture(Gdx.files.internal("data/clonesplaylogo.png")), 0, 94, 256, 161-94);
		final TextureRegion clonesPlayLogoAltRegion = new TextureRegion(new Texture(Gdx.files.internal("data/clonesplaylogoalt.png")), 0, 94, 256, 161-94);
		Image clonesPlayLogoImage = new Image(clonesPlayLogoRegion);
		clonesPlayLogoImage.setAlign(Align.center);
		mainTable.add(clonesPlayLogoImage);
		
		mainTable.row();
		
		Label builtWithLabel = new Label("Built with:", skin);
		builtWithLabel.setAlignment(Align.center);
		mainTable.add(builtWithLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		final TextureRegion libgdxLogoRegion = new TextureRegion(new Texture(Gdx.files.internal("data/libgdxlogo.png")), 0, 102, 256, 152-102);
		final TextureRegion libgdxLogoAltRegion = new TextureRegion(new Texture(Gdx.files.internal("data/libgdxlogoalt.png")), 0, 102, 256, 152-102);
		Image libgdxLogoImage = new Image(libgdxLogoRegion);
		libgdxLogoImage.setAlign(Align.center);
		mainTable.add(libgdxLogoImage);
		
		mainTable.row();
		
		Label networkLayerBuiltWithLabel = new Label("Network layer built with:", skin);
		networkLayerBuiltWithLabel.setAlignment(Align.center);
		mainTable.add(networkLayerBuiltWithLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		final TextureRegion kryonetLogoRegion = new TextureRegion(new Texture(Gdx.files.internal("data/kryonetlogo.png")), 0, 93, 256, 155-93);
		final TextureRegion kryonetLogoAltRegion = new TextureRegion(new Texture(Gdx.files.internal("data/kryonetlogoalt.png")), 0, 93, 256, 155-93);
		Image kryonetLogoImage = new Image(kryonetLogoRegion);
		kryonetLogoImage.setAlign(Align.center);
		mainTable.add(kryonetLogoImage);
		
		mainTable.row();
		
		Label poweredByLabel = new Label("Server powered by:", skin);
		poweredByLabel.setAlignment(Align.center);
		mainTable.add(poweredByLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		final TextureRegion ubuntuLogoRegion = new TextureRegion(new Texture(Gdx.files.internal("data/ubuntulogo.png")), 0, 57, 256, 180-57);
		final TextureRegion ubuntuLogoAltRegion = new TextureRegion(new Texture(Gdx.files.internal("data/ubuntulogoalt.png")), 0, 57, 256, 180-57);
		Image ubuntuLogoImage = new Image(ubuntuLogoRegion);
		ubuntuLogoImage.setAlign(Align.center);
		mainTable.add(ubuntuLogoImage);
		
		mainTable.row();
		
		Label contributionsLabel = new Label("Contributions:", skin);
		contributionsLabel.setAlignment(Align.center);
		mainTable.add(contributionsLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		Label contributionsMusicTitleLabel = new Label("[Music]", smallSkin);
		contributionsMusicTitleLabel.setAlignment(Align.center);
		mainTable.add(contributionsMusicTitleLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		Label contributionsMusicLabel = new Label("Music is derivative of sound named \"Epic Orchestra - LOOP\" found in" +
				" Music Loops Pack by joshuaempyre, used under CC BY 3.0 http://creativecommons.org/licenses/by/3.0/" +
				" https://www.freesound.org/people/joshuaempyre/sounds/250856/", smallSkin);
		contributionsMusicLabel.setAlignment(Align.center);
		contributionsMusicLabel.setWrap(true);
		mainTable.add(contributionsMusicLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		Label contributionsMoveSoundsTitleLabel = new Label("[Move Sounds]", smallSkin);
		contributionsMoveSoundsTitleLabel.setAlignment(Align.center);
		mainTable.add(contributionsMoveSoundsTitleLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		Label contributionsMoveSoundsLabel = new Label("Player move sounds are derivatives of sounds found in Frequency" +
				" Impact Pack by RSilveira_88, used under CC BY 3.0 http://creativecommons.org/licenses/by/3.0/" +
				" http://www.freesound.org/people/RSilveira_88/packs/13702/", smallSkin);
		contributionsMoveSoundsLabel.setAlignment(Align.center);
		contributionsMoveSoundsLabel.setWrap(true);
		mainTable.add(contributionsMoveSoundsLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		Label contributionsWinLoseSoundsTitleLabel = new Label("[Win/Lose Sounds]", smallSkin);
		contributionsWinLoseSoundsTitleLabel.setAlignment(Align.center);
		mainTable.add(contributionsWinLoseSoundsTitleLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		Label contributionsWinLoseSoundsLabel = new Label("Win/Lose sounds are derivatives of sounds found in Horror Sound " +
				"Effects Library by LittleRobotSoundFactory, used under CC BY 3.0 http://creativecommons.org/licenses/by/3.0/" +
				" http://www.freesound.org/people/LittleRobotSoundFactory/packs/16688/", smallSkin);
		contributionsWinLoseSoundsLabel.setAlignment(Align.center);
		contributionsWinLoseSoundsLabel.setWrap(true);
		mainTable.add(contributionsWinLoseSoundsLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		Label contributionsSocialIconsTitleLabel = new Label("[Social Network Icons]", smallSkin);
		contributionsSocialIconsTitleLabel.setAlignment(Align.center);
		mainTable.add(contributionsSocialIconsTitleLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
		Label contributionsSocialIconsLabel = new Label("Some of social network icons were downloaded from www.iconfinder.com", smallSkin);
		contributionsSocialIconsLabel.setAlignment(Align.center);
		contributionsSocialIconsLabel.setWrap(true);
		mainTable.add(contributionsSocialIconsLabel).minWidth(width-6).spaceBottom(3).spaceTop(3);
		
		mainTable.row();
		
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
				startScreen.getAndroidNativeHandler().openMarketApp("com.luke.Clones");
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
		
		libgdxLogoImage.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(libgdxLogoAltRegion));
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(libgdxLogoRegion));
			}
		});
		
		kryonetLogoImage.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(kryonetLogoAltRegion));
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(kryonetLogoRegion));
			}
		});
		
		ubuntuLogoImage.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				SoundManager.INSTANCE.play(SoundType.CLICK);
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(ubuntuLogoAltRegion));
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				((Image) event.getTarget()).setDrawable(new TextureRegionDrawable(ubuntuLogoRegion));
			}
		});
		
		//test
//		AchievementType[] newAchievements = new AchievementType[1];
//		newAchievements[0] = AchievementType.GAMES_WON_IN_ROW_5;
//		List<QuickMessage> quickMessagesAboutAchievements = QuickMessageUtil.prepareQuickMessagesWithNewAchievements(newAchievements, skin, smallSkin, startScreen);
//		for(QuickMessage quickMessage : quickMessagesAboutAchievements){
//			QuickMessageManager.showMessageOnSpecifiedStage(quickMessage, stage);
//		}
		
		//test
//		startScreen.getAndroidNativeHandler().showInterstitial();
	}

	@Override
	public synchronized void render(float delta) {
		Gdx.gl.glClearColor(54f/255f, 47f/255f, 45f/255f, 1);
//		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		if(viewport!=null) viewport.update(width, height, true);
	}

	@Override
	public synchronized void hide() {
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
