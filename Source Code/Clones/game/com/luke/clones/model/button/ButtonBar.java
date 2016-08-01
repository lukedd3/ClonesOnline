package com.luke.clones.model.button;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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

public class ButtonBar {
	protected ImgButton[] imgButton;
	private int x, y, buttonSpacing, yOffset;
	private int buttonWidth, buttonHeight;
	
	public ButtonBar(Stage stage, Texture[] up, Texture[] down){
		x=0;
		y=0;
		buttonWidth = 50;
		buttonHeight = 50;
		buttonSpacing = 0;
		yOffset = 0;
		
		ButtonListener buttonListener = new ButtonListener();
		imgButton = new ImgButton[up.length];
		for(int i=0; i<up.length; i++){
			imgButton[i] = new ImgButton(up[i],down[i]);
			imgButton[i].setPosition(0, 0);
			imgButton[i].setSize(50, 50);
			imgButton[i].addListener(buttonListener);
			stage.addActor(imgButton[i]);
			imgButton[i].toFront();
		}
	}
	
	protected void click(InputEvent event, float x, float y){
		
	}
	
	public void removeFromStage(){
		for(int i=0; i<imgButton.length; i++){
			imgButton[i].remove();
		}
	}
	
	private class ButtonListener extends ClickListener{
		@Override
		public void clicked(InputEvent event, float x, float y) {
			click(event, x, y);
		}
	}
	
	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
		calculateSize();
	}
	
	public void setButtonSize(int width, int height){
		this.buttonWidth = width;
		this.buttonHeight = height;
		for(int i=0; i<imgButton.length; i++){
			imgButton[i].setSize(width, height);
		}
		calculateSize();
	}
	
	public void setSpacing(int buttonSpacing, int yOffset){
		this.buttonSpacing = buttonSpacing;
		this.yOffset = yOffset;
		calculateSize();
	}
	
	private void calculateSize(){
		for(int i=0; i<imgButton.length; i++){
			imgButton[i].setPosition(x + (imgButton[i].getWidth() + buttonSpacing)*i, y + yOffset);
		}
	}
}
