package com.luke.clones.model.actor;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.luke.clones.model.type.Position;

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

public class HighlightActorAnimatedColorChange extends HighlightActor {
	private static final Color DEFAULT_COLOR = Color.WHITE;
	private static final float DEFAULT_COLOR_DURATION = 1/20f;
	
	List<Color> colors;
	int currentColorNumber = 0;
	float singleColorDuration = DEFAULT_COLOR_DURATION;
	
	float elapsedTime = 0f;
	
	public HighlightActorAnimatedColorChange(TextureRegion fieldTextureRegion, List<Color> colors,
			Position position) {
		super(fieldTextureRegion, choiceStartColor(colors), position);
		this.colors = colors;
	}
	
	public HighlightActorAnimatedColorChange(Texture fieldTexture, List<Color> colors,
			Position position) {
		this(new TextureRegion(fieldTexture), colors, position);
	}
	
	public void setSingleColorDuration(float singleColorDuration) {
		this.singleColorDuration = singleColorDuration;
	}
	
	public float getSingleColorDuration() {
		return singleColorDuration;
	}

	private static Color choiceStartColor(List<Color> colors){
		Color startColor;
		if(colors!=null && colors.size()>0){
			startColor=colors.get(0);
		}
		else{
			startColor = DEFAULT_COLOR;
		}
		return startColor;
	}
	
//	@Override
//	public void draw(SpriteBatch sb, float parentAlpha) {
	@Override
	public void draw(Batch sb, float parentAlpha) {
		elapsedTime += Gdx.graphics.getDeltaTime();
		if(elapsedTime>=singleColorDuration){
			super.fieldColor = choiceNextColor();
			elapsedTime-=singleColorDuration;
		}
		super.draw(sb, parentAlpha);
	}
	
	private Color choiceNextColor(){
		currentColorNumber++;
		
		Color nextColor;
		if(colors==null || colors.size()==0){
			return DEFAULT_COLOR;
		}
		if(currentColorNumber>colors.size()-1){
			currentColorNumber=0;
		}
		nextColor=colors.get(currentColorNumber);
		
		return nextColor;
	}

}
