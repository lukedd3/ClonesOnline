package com.luke.clones.model.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

public class BallActor extends FieldActor{
	private Texture backgroundTexture;
		
	public BallActor(Texture fieldTexture, Texture backgroundTexture, Color color, Position position) {
		super(fieldTexture, color, position);
		this.backgroundTexture = backgroundTexture;
	}
	
//	@Override
//	public void draw(SpriteBatch sb, float parentAlpha) {
	@Override
	public void draw(Batch sb, float parentAlpha) {
		sb.setColor(Color.WHITE);
		sb.draw(backgroundTexture, getX(), getY(), getWidth(), getHeight());
		if(fieldColor!=null) sb.setColor(fieldColor);
		sb.draw(fieldTexture, getX(), getY(), getWidth(), getHeight());
	}
	
}
