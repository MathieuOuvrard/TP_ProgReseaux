/***
 * EchoClient
 * Server of the TCP chat
 * The tchat will save messages in a file
 * Date: 21/10/20
 * Authors: OUVRARD/GIRARD
 */

package stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class EchoServerMultiThreaded  {
  	public static ArrayList<Socket> connectedClients;
  	public static String historicFile = "feed.txt";

 	/**
  	* main method
  	* Open the server
	* @param EchoServer port
  	* 
  	**/
       public static void main(String args[])
	   {
        	ServerSocket listenSocket;
        	connectedClients = new ArrayList<Socket>();


		   if (args.length != 1)
		   {
		   		System.out.println("Usage: java EchoServer <EchoServer port>");
          		System.exit(1);
		   }

			try
			{
				listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
				System.out.println("Server ready...");

				while (true)
				{
					Socket clientSocket = listenSocket.accept();
					System.out.println("Connexion from:" + clientSocket.getInetAddress());
					connectedClients.add(clientSocket);

					 /**
					 * ClientThread is a thread that manage to share the
					 * messages to all client connected except the sender 
					 * 
					 * @see ClientThread
					 */

					ClientThread ct = new ClientThread(clientSocket);
					ct.start();
				}
			}

			catch (Exception e)
			{
				System.err.println("Error in EchoServer:" + e);
			}
      }
  }

  
