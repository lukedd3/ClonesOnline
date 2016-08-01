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

public enum ActionTypeUpdateLogin {
	SUCCESS,
	/**
	 * Player can't login with that name because it is already taken
	 */
//	NAME_ALREADY_TAKEN,
	/**
	 * Player can't login with than name because this name is forbidden (not matching regex)
	 */
//	FORBIDDEN_NAME
	/**
	 * Authentication token sent by player is invalid
	 */
	INVALID_AUTH_TOKEN,
	/**
	 * Some unexpected error occured - most probably server was unable to check if token is valid because of IOException
	 */
	UNEXPECTED_ERROR,
	/**
	 * Current number of players is greater or equal maximum number of players so server doesn't allow new connections
	 */
	SERVER_FULL
}
