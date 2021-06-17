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
	
	private void addToFile(String message) {
		File dir;
		PrintWriter writer;
		String token = message.split("token=")[1].split(",")[0];
		
		dir = new File("./database/" + token);
		
		if (!dir.exists()){
		    dir.mkdirs();
		}
		
		try {
			writer = new PrintWriter("./database/" + token + "/temperatures.txt", "UTF-8");
			writer.println("The first line");
			writer.println("The second line");
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}