package network;

import gnu.getopt.Getopt;

public class Main {
	public static void main(String[] args) {
		MyTCPServer server = new MyTCPServer();
		Getopt g = new Getopt("RemoteStatsServer", args, "c:d:sr"); // -c NAME create database NAME, -d NAME drop database NAME, -s setup GPUStats, -r run server
		String arg;
		int c;

		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 'c': {
				arg = g.getOptarg();
				server.createDatabase(arg);
				
				break;
			}
			case 'd': {
				arg = g.getOptarg();
				server.dropDatabase(arg);
				
				break;
			}
			case 's': {
				server.createDatabase("Stats");
				server.createGPUStatsTable("Stats");
				server.createIndexOnGPUStatsTable("Stats");
				
				break;
			}
			case 'r': {
				server.useDatabase("Stats");
				server.tcpServer();
				
				break;
			}
			default:
				System.out.print("getopt() returned unknow param " + c + "\n");
			}
		}
	}
}
