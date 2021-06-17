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

public class MyTCPServer {
	public void tcpServer() {
		ServerSocket server;
		Socket client = null;
		PrintWriter out;
		BufferedReader in;
		String message;

		try {
			server = new ServerSocket(Constants.port);
			
			while (true) {
				try {					
					client = server.accept();
					out = new PrintWriter(client.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					message = in.readLine();
					out.println("@received: " + message + " @from: " + client.getInetAddress().toString() + " @port: " + client.getLocalPort());
					
					createTokenFolder(message);
					
					//if(!clearStatsFile(message, "temps")) {
						addStatsToFile(message, "temps");
					//}
					
					//if(!clearStatsFile(message, "power")) {
						addStatsToFile(message, "power");
					//}
					
					//if(!clearStatsFile(message, "fans")) {
						addStatsToFile(message, "fans");
					//}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(client != null) {
						try {
							client.close();
						} catch (IOException e) {
						}
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void createTokenFolder(String message) {
		File dir = new File("./database/" + message.split("token=")[1].split(",")[0]);
		
		if (!dir.exists()){
		    dir.mkdirs();
		}
	}
	
	private boolean addStatsToFile(String message, String stats) {
		PrintWriter writer;
		String token = message.split("token=")[1].split(",")[0], data;
		
		if(message.split(stats + ":").length < 2) {
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
	
	private boolean clearStatsFile(String message, String stats) {
		File file;
		String token;
		
		if(message.split(stats + ":").length < 2) {
			token = message.split("token=")[1].split(",")[0];
			file = new File("./database/" + token + "/" + stats + ".txt");
			
			if(file.exists()) {
				file.delete();
			}
			
			return true;
		}
				
		return false;
	}
}