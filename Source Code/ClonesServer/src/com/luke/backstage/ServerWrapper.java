package com.luke.backstage;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.luke.clones.network.config.NetworkConfig;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.clones.network.interfaces.RegisterClasses;
import com.luke.config.Config;

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

public class ServerWrapper {
	final static Logger log = Logger.getLogger(ServerWrapper.class);
	private Server server;
	private RegisterClasses register;
	
	public ServerWrapper(){
		server = new Server(NetworkConfig.SERVER_WRITE_BUFFER_SIZE_IN_BYTES, NetworkConfig.SERVER_READ_BUFFER_SIZE_IN_BYTES);
	}
	
	public void start(RegisterClasses registerClasses) throws IOException{
		Kryo kryo = server.getKryo();
		register = registerClasses;
		register.registerClasses(kryo);
		
		server.start();
		int port = Config.port;
		server.bind(port);
		log.info("Server started on port "+port);
	}
	
	public void stop(){
		server.stop();
		log.info("Server stopped");
	}
	
	public void restart() throws IOException{
		stop();
		start(register);
		log.info("Server restarted");
	}
	
	public void addListener(final ListenerService serverService){
		Listener listener = new Listener(){

			@Override
			public void connected(Connection connection) {
				connection.setKeepAliveTCP(NetworkConfig.defaultKeepAliveTcp);
				connection.setTimeout(NetworkConfig.defaultConnectionTimeout);
				serverService.connected(connection);
			}

			@Override
			public void disconnected(Connection connection) {
				serverService.disconnected(connection);
			}

			@Override
			public void idle(Connection connection) {
				serverService.idle(connection);
			}

			@Override
			public void received(Connection connection, Object obj) {
				serverService.received(connection, obj);
			}
			
		};
		
		server.addListener(listener);
	}

}
