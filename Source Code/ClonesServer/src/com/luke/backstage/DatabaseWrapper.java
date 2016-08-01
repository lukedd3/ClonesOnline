package com.luke.backstage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.luke.config.Config;
import com.luke.service.DatabaseService;
import com.luke.util.DatabaseUtil;

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

public class DatabaseWrapper {
	private Connection databaseConnection;
	
	private Logger log = Logger.getLogger(DatabaseWrapper.class);

	public Connection getDatabaseConnection() {
		log.debug("Obtaining database connection");
		return databaseConnection;
	}

	public void connect() throws SQLException {
		log.debug("Connecting to database");
		String url = DatabaseUtil.generateDatabaseUrl(Config.databaseType,
				Config.databaseAddress, Config.databasePort,
				Config.databaseName);
		Properties props = new Properties();
		props.setProperty("user", Config.databaseUser);
		props.setProperty("password", Config.databasePassword);
		databaseConnection = DriverManager.getConnection(url, props);
	}
	
	public boolean isConnected() throws SQLException{
		if(databaseConnection == null) return false;
		return !databaseConnection.isClosed();
	}

}
