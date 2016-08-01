package com.luke.clones.messages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.luke.clones.config.Config;

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

public class SoundMessage {
	private final static int IMAGE_SIZE = 96;
	
	private Button messageButton;
	
	private Skin skin;
	private String text;
	
	public SoundMessage(String text, Skin skin){
		this.text = text;
		this.skin = skin;
		init();
	}
	
	private void init(){
		setUpButton();
		messageButton.add(prepareImage()).size(IMAGE_SIZE).space(3);
		messageButton.add(prepareText()).width(Gdx.graphics.getWidth()-6-IMAGE_SIZE-3);
	}
	
	private void setUpButton(){
		messageButton = new Button(skin);
	}
	
	private Image prepareImage(){
		String texturePath = "data/messages/speaker.png";
		Texture imageTexture = new Texture(Gdx.files.internal(texturePath));
		imageTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Image image = new Image(imageTexture);
		image.setColor(Config.textColor);
		return image;
	}
	
	private Label prepareText(){
		Label textLabel = new Label(text, skin);
		textLabel.setWrap(true);
		textLabel.setAlignment(Align.center);
		return textLabel;
	}

	public Button getMessageButton() {
		return messageButton;
	}

	public Skin getSkin() {
		return skin;
	}

	public String getText() {
		return text;
	}
	
}
