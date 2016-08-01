package com.luke.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.luke.backstage.DatabaseWrapper;

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

public class DatabaseService {
	public final static DatabaseService INSTANCE = new DatabaseService();
	
	private Logger log = Logger.getLogger(DatabaseService.class);
	private DatabaseWrapper databaseWrapper = new DatabaseWrapper();
	
	private DatabaseService(){

	}
	
	private void connect(){
		try {
			databaseWrapper.connect();
			log.error("Successfully connected to database.");
		} catch (SQLException e) {
			log.error("Cannot connect to database. Stats cannot be saved.");
			log.error("",e);
		}
	}
	
	private void connectToDatabaseIfNotConnected(){
		try {
			if(!databaseWrapper.isConnected()){
				log.error("There is no connection with database. Trying to connect...");
				connect();
			}
			else{
//				log.debug("There is connection with database already. Everything is fine. Stats are being saved.");
			}
		} catch (SQLException e) {
			log.fatal("Fatal error occured. Cannot check if there is connection or not. " +
					"Probably there is no connection and stats cannot be saved.");
			log.fatal("",e);
		}
	}
	
	private Connection getDatabaseConnection() throws SQLException {
		if(databaseWrapper.getDatabaseConnection()==null || !databaseWrapper.isConnected()){
			SQLException sqlException = new SQLException("No database connection - stats cannot be saved");
			log.error("No database connection - stats cannot be saved");
			log.error("",sqlException);
			throw sqlException;
		}
		return databaseWrapper.getDatabaseConnection();
	}
	
	public Connection connectToDatabaseIfNotConnectedAndReturnConnection() throws SQLException{
		connectToDatabaseIfNotConnected();
		return getDatabaseConnection();
	}
	
}
