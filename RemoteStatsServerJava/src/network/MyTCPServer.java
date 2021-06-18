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
	public MyTCPServer() {
		
	}

	public void tcpServer() {
		Database database = new Database();
		ServerSocket server;
		Socket client = null;
		PrintWriter out;
		BufferedReader in;
		String message;
		int messageSwitch;
		
		database.dropDatabase("Stats");
		database.createDatabase("Stats");
		database.createGpuTable("Stats", "GPUStats");

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
						createTokenFolder(message);
						addStatsToFile(message, "temps");
					} else if (messageSwitch == 2) {
						createTokenFolder(message);
						addStatsToFile(message, "power");
					} else if (messageSwitch == 3) {
						createTokenFolder(message);
						addStatsToFile(message, "fans");
					} else if (messageSwitch == -1) {
						clearStatsFile(message, "temps");
					} else if (messageSwitch == -2) {
						clearStatsFile(message, "power");
					} else if (messageSwitch == -3) {
						clearStatsFile(message, "fans");
					} else if (messageSwitch == -10) {
						clearStatsFolder(message);
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
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * switches the message to check which operation to perform
	 * 
	 * @param message
	 * @return 0: remove stats file, 1: update stats data
	 */
	private int switchMessage(String message) {
		if (message.contains("remove")) {
			if (message.contains("temps")) {
				return -1;
			} else if (message.contains("power")) {
				return -2;
			} else if (message.contains("fans")) {
				return -3;
			} else if (message.contains("token_folder")) {
				return -10;
			}
		} else if (message.contains("update")) {
			if (message.contains("temps")) {
				return 1;
			} else if (message.contains("power")) {
				return 2;
			} else if (message.contains("fans")) {
				return 3;
			}
		}

		return 0;
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