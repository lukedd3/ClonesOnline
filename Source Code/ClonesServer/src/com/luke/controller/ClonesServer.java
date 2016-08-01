package com.luke.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.luke.backstage.ServerWrapper;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.clones.network.interfaces.RegisterClasses;
import com.luke.network.controller.RegisterClassesImpl;

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

public class ClonesServer {
	ServerWrapper server;
	ListenerService serverService;
	
	public ClonesServer() throws IOException, SQLException{
		serverService = new ServerService();
		server = new ServerWrapper();
		RegisterClasses register = new RegisterClassesImpl();
		server.addListener(serverService);
		server.start(register);
	}
	
	public static void main(String args[]){
		try {
			new ClonesServer();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
