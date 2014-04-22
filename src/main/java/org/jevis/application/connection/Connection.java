package org.jevis.application.connection;

import org.jevis.jeapi.JEVisDataSource;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.sql.JEVisDataSourceSQL;

public class Connection {

	private JEVisDataSourceSQL ds = null;
	public ConnectionData data = new ConnectionData();

	
	/**
	 * Connect to JEVis
	 */
	public void connect() {

		// Set status
		data.setStatus(ConnectionData.NOT_DEFINED);
		//TODO log
		
		// Define DS
		try {
			ds = new JEVisDataSourceSQL(
					data.getAddress().getValueSafe(),
					data.getPort().getValueSafe(),
					data.getDb().getValueSafe(),
					data.getDbUser().getValueSafe(),
					data.getDbPass().getValueSafe(),
					null,
					null );
			data.setStatus(ConnectionData.DEFINED);
		}
		catch (JEVisException e) {
			//TODO log
			data.setStatus(ConnectionData.DEFINITION_FAIL);
			e.printStackTrace();
			return;
		}

		if (data.getStatus() < ConnectionData.DEFINED) {
			//TODO log
		}
		else {
			// Connect to DB
			System.out.println("CONNECT DB");
			
	//		System.out.println(ds.ge);
			
			data.setStatus(ConnectionData.CONNECTING);
			if (ds.connectToDB()) {
				data.setStatus(ConnectionData.CONNECTED);
				System.out.println("CONNECTED TO DB");
			}
			else {
				System.out.println("CONNECTION TO DB FAILED");
				data.setStatus(ConnectionData.CONNECTION_FAIL);
				return;
			}
		}
	}

	public void logIn(String user, String pass) {
		
		// Check connection
		if (data.getStatus() < ConnectionData.CONNECTED) {
			System.out.println("Can not log in while not connected!");
		}
		else {
			data.setStatus(ConnectionData.LOGGING_IN);
			System.out.println("LOGGING IN");

			try {
				if (ds.connect(user, pass)) {
					data.setStatus(ConnectionData.LOGGED_IN);
					System.out.println("LOGGED IN");
				}
				else {
					data.setStatus(ConnectionData.LOGIN_FAIL);
					System.out.println("LOGIN FAIL");
				}
			}
			catch (JEVisException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				data.setStatus(ConnectionData.LOGIN_FAIL);
				System.out.println("LOGIN FAIL 2");
			}
		}
	}

	public String getUserName() {
		try {
			return ds.getCurrentUser().getName();
		}
		catch (JEVisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public JEVisDataSource getDS(){
		return ds;
	}
}