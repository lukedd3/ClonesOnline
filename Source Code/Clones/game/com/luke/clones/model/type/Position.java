package com.luke.clones.model.type;

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

public class Position {
	private int x;
	private int y;
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	public Position(float x, float y){
		this(Math.round(x), Math.round(y));
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	/**
	 * For serialization
	 */
	public Position(){
		
	}
	
	public boolean equals(Object obj){
		if(obj == this) return true;
		if((obj == null) || (obj.getClass() != this.getClass())) return false;

		Position pos = (Position) obj;
		if(x == pos.getX() && y == pos.getY()) return true;
		else return false;

	}
	
	public String toString(){
		return "posX: "+x+" posY: "+y;
	}
	
	public int hashCode(){
		String hashS = Integer.toString(x)+Integer.toString(y);
		return hashS.hashCode();
	}
}
