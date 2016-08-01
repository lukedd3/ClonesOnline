package com.luke.clones.network.backstage;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.luke.clones.network.config.NetworkConfig;
import com.luke.clones.network.interfaces.ListenerService;
import com.luke.clones.network.interfaces.RegisterClasses;

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

public class ClientWrapper {
	private Client client;
	private RegisterClasses register;
	private Listener listener;
	private String host;
	private int port;
	private int timeout;
	private int keepAliveTcp;
	
	public ClientWrapper(String host, int port, int timeout, int keepAliveTcp){
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.keepAliveTcp = keepAliveTcp;
		client = new Client(NetworkConfig.CLIENT_WRITE_BUFFER_SIZE_IN_BYTES, NetworkConfig.CLIENT_READ_BUFFER_SIZE_IN_BYTES);
	}
	
	public void start(RegisterClasses registerClasses) throws IOException{
		Kryo kryo = client.getKryo();
		register = registerClasses;
		register.registerClasses(kryo);
		
		client.setKeepAliveTCP(keepAliveTcp);
		client.setTimeout(NetworkConfig.defaultConnectionTimeout);
		client.start();
		client.connect(timeout, host, port);

		NetworkConfig.log("Client started on port "+port);
	}
	
	public void stop(){
		client.stop();
		NetworkConfig.log("Server stopped");
	}
	
	public void restart() throws IOException{
		stop();
		start(register);
		NetworkConfig.log("Client restarted");
	}
	
	public void setListener(final ListenerService serverService){
		if(this.listener!=null) client.removeListener(this.listener);
		
		this.listener = new Listener(){

			@Override
			public void connected(Connection connection) {
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
//				System.out.println("Client side connection timeout received: ");
				serverService.received(connection, obj);
			}
			
		};
		
		client.addListener(this.listener);
	}
	
	public void send(Object object){
		client.sendTCP(object);
	}

}
