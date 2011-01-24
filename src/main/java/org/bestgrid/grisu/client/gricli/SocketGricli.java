package org.bestgrid.grisu.client.gricli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.bestgrid.grisu.client.gricli.command.GricliCommand;
import org.bestgrid.grisu.client.gricli.command.GricliCommandFactory;
import org.bestgrid.grisu.client.gricli.util.CommandlineTokenizer;

public class SocketGricli {
	private GricliEnvironment env;
	private GricliCommand command;

	public static void main(String[] args) throws IOException {

		GricliEnvironment env = new GricliEnvironment();

		GricliCommandFactory f = new GricliCommandFactory();

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
					if (command == null)
						break;
					try {
						f.create(CommandlineTokenizer.tokenize(command))
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
