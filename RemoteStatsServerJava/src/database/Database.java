package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
	
	public void updateGPUStats(String databaseName, String token, String statsType, String gpuIds, String gpuNames, String gpuStats,
			String timestamp) {
		String sql1 = "USE " + databaseName + ";";
		String sql2 = "UPDATE GPUStats "
					+ "SET gpuIds = '" + gpuIds + "', gpuNames = '" + gpuNames + "', gpuStats = '" + gpuStats + "', timestamp = '" + timestamp + "'"
					+ "WHERE token = '" + token + "' AND statsType = '" + statsType + "'; ";
		
		try {
			stmt.executeQuery(sql1);
			stmt.executeUpdate(sql2);
			System.out.println(
					"Updated statement " + sql2 + " in GPUStats in database " + databaseName + " successfully.");
		} catch (SQLException e) {
			System.out.println("Error in query \"" + sql1 + "\" or \"" + sql2 + "\"");
			e.printStackTrace();
		}
	}

	public void insertIntoGPUStats(String databaseName, String token, String statsType, String gpuIds, String gpuNames, String gpuStats,
			String timestamp) {
		String sql1 = "USE " + databaseName + ";";
		String sql2 = "INSERT INTO GPUStats (token, statsType, gpuIds, gpuNames, gpuStats, timestamp) " + "VALUES ('" + token + "', '" + statsType
				+ "', '" + gpuIds + "', '" + gpuNames + "', '" + gpuStats + "', '" + timestamp + "');";

		try {
			stmt.executeQuery(sql1);
			stmt.executeUpdate(sql2);
			System.out.println(
					"Inserted statement " + sql2 + " into GPUStats in database " + databaseName + " successfully.");
		} catch (SQLException e) {
			System.out.println("Error in query \"" + sql1 + "\" or \"" + sql2 + "\"");
			e.printStackTrace();
		}
	}
	
	public boolean checkTokenStatsTypePairExists(String databaseName, String token, String statsType) {
		String sql1 = "USE " + databaseName + ";";
		String sql2 = "SELECT 1 FROM GPUStats WHERE token = '" + token + "' AND statsType = '" + statsType + "';";
		
		try {
			stmt.executeQuery(sql1);
			ResultSet rs = stmt.executeQuery(sql2);
			
			return rs.next();
		} catch (SQLException e) {
			System.out.println("Error in query \"" + sql1 + "\" or \"" + sql2 + "\"");
			e.printStackTrace();
		}
		
		return false;
	}

	public void createGpuTable(String databaseName, String tableName) {
		String sql1 = "USE " + databaseName + ";";
		String sql2 = "DROP TABLE IF EXISTS GPUStats;";
		String sql3 = "CREATE TABLE IF NOT EXISTS " + tableName
				+ " (token VARCHAR(24), statsType VARCHAR(12), gpuIds VARCHAR(512), gpuNames VARCHAR(1024), gpuStats VARCHAR(2048), timestamp TIMESTAMP)"
				+ "	DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;";

		try {
			stmt.executeQuery(sql1);
			stmt.executeUpdate(sql2);
			stmt.executeUpdate(sql3);
			System.out.println("Created table " + tableName + " in database " + databaseName + " successfully.");
		} catch (SQLException e) {
			System.out.println("Error in query \"" + sql1 + "\" or \"" + sql2 + "\" or \"" + sql2 + "\"");
			e.printStackTrace();
		}
	}

	public void createGpuTableTemps(String databaseName, String tableName) {
		String sql1 = "USE " + databaseName + ";";
		String sql2 = "DROP TABLE GPUStats;";
		String sql3 = "CREATE TABLE IF NOT EXISTS " + tableName
				+ " (token VARCHAR(24), gpuId INT, gpuName VARCHAR(32), gpuTemp INT, timestamp TIMESTAMP) "
				+ "DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;";

		try {
			stmt.executeQuery(sql1);
			stmt.executeUpdate(sql2);
			stmt.executeUpdate(sql3);
			System.out.println("Created table " + tableName + " in database " + databaseName + " successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createGpuTablePower(String databaseName, String tableName) {
		String sql1 = "USE " + databaseName + ";";
		String sql2 = "DROP TABLE GPUStats;";
		String sql3 = "CREATE TABLE IF NOT EXISTS " + tableName
				+ " (token VARCHAR(24), gpuId INT, gpuName VARCHAR(32), gpuPower INT, timestamp TIMESTAMP) "
				+ "DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;";

		try {
			stmt.executeQuery(sql1);
			stmt.executeUpdate(sql2);
			stmt.executeUpdate(sql3);
			System.out.println("Created table " + tableName + " in database " + databaseName + " successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createGpuTableFans(String databaseName, String tableName) {
		String sql1 = "USE " + databaseName + ";";
		String sql2 = "DROP TABLE GPUStats;";
		String sql3 = "CREATE TABLE IF NOT EXISTS " + tableName
				+ " (token VARCHAR(24), gpuId INT, gpuName VARCHAR(32), gpuFansSpeed INT, timestamp TIMESTAMP) "
				+ "DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_unicode_ci;";

		try {
			stmt.executeQuery(sql1);
			stmt.executeUpdate(sql2);
			stmt.executeUpdate(sql3);
			System.out.println("Created table " + tableName + " in database " + databaseName + " successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clearTable(String databaseName, String tableName) {
		String sql1 = "USE " + databaseName + ";";
		String sql2 = "DELETE FROM " + tableName + ";";

		try {
			stmt.executeQuery(sql1);
			stmt.executeUpdate(sql2);
			System.out.println(
					"Deleted all content form table " + tableName + " in database " + databaseName + " successfully.");
		} catch (SQLException e) {
			System.out.println("Erro in query \"" + sql2 + "\"");
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

	public void printGPUStats() {
		ResultSet rs;
		ResultSetMetaData rsmd;
		int columns;

		try {
			rs = stmt.executeQuery("SELECT * FROM GPUStats");
			rsmd = rs.getMetaData();
			columns = rsmd.getColumnCount();

			System.out.println("colums number: " + columns);

			while (rs.next()) {
				for (int i = 1; i < columns; i++) {
					System.out.print("'" + rs.getString(i) + "'; ");
				}
				System.out.print("'" + rs.getString(columns) + "'; ");
				System.out.println();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
