package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	private final String DB_URL = "jdbc:mysql://localhost/";
	private final String USER = "admin";
	private final String PASS = "admin_mysql1994";
	
	private Connection conn;
	private Statement stmt;
	
	public Database() {
		setUp();
	}
	
	private void setUp() {
		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * MySQL database
	 */
	
	public void createGpuTable(String databaseName, String tableName) {
		String sql1 = "USE " + databaseName + ";";
		String sql2 = "CREATE TABLE IF NOT EXISTS " + tableName + " (token VARCHAR(24), gpuId INT, gpuName VARCHAR(32), gpuTemp INT, gpuPower INT, gpuFanSpeed INT)"
				+ "	DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;";
		
		try {
			stmt.executeQuery(sql1);
			stmt.executeUpdate(sql2);
			System.out.println("Created table " + tableName + " in database " + databaseName + " successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createDatabase(String name) {
		String sql = "CREATE DATABASE IF NOT EXISTS " + name + ";";
		
		try {
			stmt.executeUpdate(sql);
			System.out.println("Created database " + name + " successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void dropDatabase(String name) {
		String sql = "DROP DATABASE IF EXISTS " + name + ";";
		try {
			stmt.executeUpdate(sql);
			System.out.println("Dropped database " + name + " successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
