package com.luke.clones.map.creator;

import com.badlogic.gdx.graphics.Color;
import com.luke.clones.model.type.FieldType;

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

public enum DrawFieldButtonType {
	SOLID(FieldType.SOLID, null),
	TAKEN_RED(FieldType.TAKEN, Color.RED),
	TAKEN_BLUE(FieldType.TAKEN, Color.BLUE),
	TAKEN_GREEN(FieldType.TAKEN, Color.GREEN),
	TAKEN_ORANGE(FieldType.TAKEN, Color.ORANGE);
	
	private FieldType drawFieldType = FieldType.TAKEN;
	private Color drawFieldColor = Color.RED;
	
	private DrawFieldButtonType(FieldType drawFieldType, Color drawFieldColor) {
		this.drawFieldType = drawFieldType;
		this.drawFieldColor = drawFieldColor;
	}

	public FieldType getDrawFieldType() {
		return drawFieldType;
	}

	public Color getDrawFieldColor() {
		return drawFieldColor;
	}
	
}
