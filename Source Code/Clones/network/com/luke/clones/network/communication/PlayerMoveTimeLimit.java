package com.luke.clones.network.communication;

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

public class PlayerMoveTimeLimit{
	private boolean unlimited;
	private int limitInSeconds;
	
	public PlayerMoveTimeLimit() {
		
	}
	
	public PlayerMoveTimeLimit(boolean unlimited, int limitInSeconds) {
		this.limitInSeconds = limitInSeconds;
		this.unlimited = unlimited;
	}

	public boolean isUnlimited() {
		return unlimited;
	}

	public void setUnlimited(boolean unlimited) {
		this.unlimited = unlimited;
	}

	public int getLimitInSeconds() {
		return limitInSeconds;
	}

	public void setLimitInSeconds(int limitInSeconds) {
		this.limitInSeconds = limitInSeconds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + limitInSeconds;
		result = prime * result + (unlimited ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerMoveTimeLimit other = (PlayerMoveTimeLimit) obj;
		if (limitInSeconds != other.limitInSeconds)
			return false;
		if (unlimited != other.unlimited)
			return false;
		return true;
	}

}
