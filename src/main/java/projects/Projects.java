package projects;

import java.sql.Connection;

import projects.dao.DbConnection;

public class Projects {

	public static void main(String[] args) {
		DbConnection.getConnection();
	}

}
