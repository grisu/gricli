package grisu.gricli;

import grisu.gricli.parser.GricliTokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketGricli {

	public static void main(String[] args) throws IOException {

		GricliEnvironment env = new GricliEnvironment();

		ServerSocket myService;
		myService = new ServerSocket(Integer.parseInt(args[0]));

		while (true) {
			try {
				Socket clientSocket = null;
				clientSocket = myService.accept();
				System.setOut(new PrintStream(clientSocket.getOutputStream()));
				BufferedReader input = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
				String command;
				while (true) {
					command = input.readLine();
					if (command == null) {
						break;
					}
					try {
						Gricli.SINGLETON_COMMANDFACTORY.create(
								GricliTokenizer.tokenize(command))
						.execute(env);
						System.out.println(command + " executed ");
					} catch (GricliException ex) {
						ex.printStackTrace();
					}
				}
			} catch (IOException ex) {
				// next client
				continue;
			}
		}

	}
}
