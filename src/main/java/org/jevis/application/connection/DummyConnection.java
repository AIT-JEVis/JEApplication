package org.jevis.application.connection;

import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.sql.JEVisDataSourceSQL;

public class DummyConnection {
	public void connect() {
		JEVisDataSourceSQL ds = null;
		try {
			ds = new JEVisDataSourceSQL("openjevis.org", "3306", "jevis", "jevis", "jevistest", null, null);
		}
		catch (JEVisException e) {
			System.out.println("DEFINE DATASOURCE FAILURE");
			e.printStackTrace();
		}

		if (ds != null) {
			if (ds.connectToDB()) {
				System.out.println("DB CONNECTION ESTABLISHED");
			}
		}

		if (ds != null) {
			try {
				ds.connect("Sys Admin", "jevis");
				System.out.println("LOG IN SUCCESSFUL");
			}
			catch (JEVisException e) {
				System.out.println("USER LOGIN FAILURE");
				e.printStackTrace();
			}
		}
	}
}
