package com.luke.backstage;

import com.esotericsoftware.kryonet.Connection;
import com.luke.authentication.model.GoogleUserData;

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

public class Player {
	private String name;
	private GoogleUserData googleUserData;
	private Connection connection;
	
	public Player(String name, GoogleUserData googleUserData, Connection connection){
		this.connection = connection;
		this.name = name;
		this.googleUserData = googleUserData;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public GoogleUserData getGoogleUserData() {
		return googleUserData;
	}
	
	public int getConnectionID(){
		return connection.getID();
	}
	
	public Connection getConnection(){
		return connection;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
		if(!(obj.getClass() == this.getClass())) return false;
		
		Player player = (Player) obj;
		if(!player.name.equals(this.name)) return false;
		if(player.connection!=this.connection) return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		return (name.toString()+connection.toString()).hashCode();
	}
	
	
}
