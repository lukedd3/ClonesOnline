package com.luke.clones.exceptions;

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

public class BuildingFinishedException extends RuntimeException{
	private static final long serialVersionUID = -6117350005836843514L;
	private boolean finished;
	
	public BuildingFinishedException(boolean finished){
		super();
		this.finished = finished;
	}
	
	@Override
	public String getLocalizedMessage() {
		String msg="";
		if(finished) msg = super.getLocalizedMessage() + "Can't make this kind of change when building is finished";
		else if (!finished) msg = super.getLocalizedMessage() + "Finish building to use this function";
		return msg;
	}

	@Override
	public String getMessage() {
		String msg="";
		if(finished) msg = super.getMessage() + "Can't make this kind of change when building is finished";
		else if (!finished) msg = super.getMessage() + "Finish building to use this function";
		return msg;
	}

	@Override
	public String toString() {
		String msg="";
		if(finished) msg = super.getLocalizedMessage() + "Can't make this kind of change when building is finished";
		else if (!finished) msg = super.getLocalizedMessage() + "Finish building to use this function";
		return super.toString() + msg;
	} 
		
}
