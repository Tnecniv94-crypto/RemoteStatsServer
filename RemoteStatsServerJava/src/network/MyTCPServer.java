package network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import database.Database;

public class MyTCPServer {
	private Database database = new Database();

	public MyTCPServer() {

	}

	public void tcpServer() {
		ServerSocket server;
		Socket client = null;
		PrintWriter out;
		BufferedReader in;
		String message;
		int messageSwitch;

		//database.createDatabase("Stats");
		//database.createGpuTable("Stats", "GPUStats");
		//database.createIndexOnGPUStatsTable("Stats");
		//database.clearTable("Stats", "GPUStats");
		/*database.insertIntoGPUStats("Stats", "test", "testType", "1,2", "NVIDIA RTX 3060 12GB, NVIDIA RTX 3070", "60, 50",
				"2000-01-01 00:00:00");*/
		database.printGPUStats();

		try {
			server = new ServerSocket(Constants.port);

			while (true) {
				try {
					client = server.accept();
					out = new PrintWriter(client.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					message = in.readLine();

					System.out.println("received: " + message + " from: " + client.getInetAddress().toString()
							+ " port: " + client.getLocalPort());
					out.println("@received: " + message + " @from: " + client.getInetAddress().toString() + " @port: "
							+ client.getLocalPort());

					messageSwitch = switchMessage(message);

					if (messageSwitch == 1) {
						addStatsToDatabase(message, "temps");
					} else if (messageSwitch == 2) {
						addStatsToDatabase(message, "power");
					} else if (messageSwitch == 3) {
						addStatsToDatabase(message, "fans");
					} else if (messageSwitch == -1) {
						deleteStatsFromDatabase(message, "temps");
					} else if (messageSwitch == -2) {
						deleteStatsFromDatabase(message, "power");
					} else if (messageSwitch == -3) {
						deleteStatsFromDatabase(message, "fans");
					} else if (messageSwitch == -10) {
						// clearStatsFolder(message);
					} else {
						System.out.println("Can't read message.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (client != null) {
						client.close();
					}
				}

				database.printGPUStats();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void createDatabase(String databaseName) {
		database.createDatabase(databaseName);
	}
	
	public void createGPUStatsTable(String databaseName) {
		database.createTable(databaseName, "GPUStats");
	}
	
	public void createIndexOnGPUStatsTable(String databaseName) {
		database.createIndexOnGPUStatsTable(databaseName);
	}
	
	public void dropDatabase(String databaseName) {
		database.dropDatabase(databaseName);
	}
	
	public void useDatabase(String databaseName) {
		database.useDatabase(databaseName);
	}

	/**
	 * switches the message to check which operation to perform
	 * 
	 * @param message
	 * @return 0: remove stats file, 1: update stats data
	 */
	private int switchMessage(String message) {
		if (message.contains("remove")) {
			if (message.contains("_temps")) {
				return -1;
			} else if (message.contains("_power")) {
				return -2;
			} else if (message.contains("_fans")) {
				return -3;
			} else if (message.contains("_token_entry")) {
				return -10;
			}
		} else if (message.contains("update")) {
			if (message.contains("_temps")) {
				return 1;
			} else if (message.contains("_power")) {
				return 2;
			} else if (message.contains("_fans")) {
				return 3;
			}
		}

		return 0;
	}

	/*
	 * MySQL as Database
	 */

	private boolean addStatsToDatabase(String message, String stats) {
		String token = message.split("token=")[1].split(",")[0], gpuIds, gpuNames, data, timestamp;

		if (!message.contains("update _" + stats)) {
			System.out.println("Message doesn't contain \"update _" + stats + "\" command.");

			return false;
		}

		gpuIds = message.split("ids: ")[1].split(";")[0];
		gpuNames = message.split("names: ")[1].split(";")[0];

		switch (stats) {
		case "temps": {
			data = message.split("temps: ")[1].split(";")[0];
			break;
		}
		case "power": {
			data = message.split("power: ")[1].split(";")[0];
			break;
		}
		case "fans": {
			data = message.split("fans: ")[1].split(";")[0];
			break;
		}
		default: {
			System.out.println("Unknown param stats for data: " + stats);

			return false;
		}
		}

		timestamp = message.split("time=")[1].split(";")[0];

		database.updateGPUStats("Stats", token, stats, gpuIds, gpuNames, data, timestamp);

		return false;
	}
	
	private boolean deleteStatsFromDatabase(String message, String stats) {
		String token = message.split("token=")[1].split(",")[0];

		System.out.println(message);
		if (!message.contains("remove _" + stats)) {
			System.out.println("Message doesn't contain \"remove _" + stats + "\" command.");

			return false;
		}

		switch (stats) {
		case "temps": {
			database.deleteGPUStats("Stats", token, stats);
			break;
		}
		case "power": {
			database.deleteGPUStats("Stats", token, stats);
			break;
		}
		case "fans": {
			database.deleteGPUStats("Stats", token, stats);
			break;
		}
		default: {
			System.out.println("Unknown param stats for data: " + stats);

			return false;
		}
		}

		database.deleteGPUStats("Stats", token, stats);

		return true;
	}

	/*
	 * Database as folder directory
	 */

	private void createTokenFolder(String message) {
		File dir = new File("./database/" + message.split("token=")[1].split(",")[0]);

		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	private boolean addStatsToFile(String message, String stats) {
		PrintWriter writer;
		String token = message.split("token=")[1].split(",")[0], data;

		if (message.split(stats + ":").length < 2) {
			return false;
		}

		data = message.split(stats + ":")[1].split(";")[0];

		try {
			writer = new PrintWriter("./database/" + token + "/" + stats + ".txt", "UTF-8");
			writer.println(data);
			writer.close();

			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return false;
	}

	private boolean clearStatsFolder(String message) {
		File file;
		String token;

		if (message.split("token=").length > 1) {
			token = message.split("token=")[1].split(",")[0];
			file = new File("./database/" + token + "/");

			if (file.exists()) {
				file.delete();
			}

			return true;
		}

		return false;
	}

	private boolean clearStatsFile(String message, String stats) {
		File file;
		String token;

		if (message.split(stats + ":").length < 2) {
			token = message.split("token=")[1].split(",")[0];
			file = new File("./database/" + token + "/" + stats + ".txt");

			if (file.exists()) {
				file.delete();
			}

			return true;
		}

		return false;
	}
}