/***
 * SendThread
 * Thread that manage the sending of messages of ClientUDP
 * Attribute:
 * int port
 * InetAdress group
 * String name
 * MulticastSocket client socket
 * Date: 21/10/20
 * Authors: OUVRARD/GIRARD
 */
 
package UDP;

import java.io.*;
import java.net.*;
import java.util.*;



public class SendThread extends Thread
	{
		private InetAddress group;
		private int port;
		private String name;
		private MulticastSocket clientSocket;
		
		SendThread(InetAddress address, int port, String name, MulticastSocket clientSocket)
		{
			this.port = port;
			this.group = address;
			this.name = name;
			this.clientSocket = clientSocket;
			
		}
		
		/**
		*  run method
		*  manage the sending of messages of ClientUDP
		**/
		
		public void run()
		{
			BufferedReader stdIn;
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			String line;
					
					while(true)
					{
						try
						{
							String message = null;
							line = stdIn.readLine();
							
							if(line.equals("leave"))
							{
								message = name + " has left the group.";
							}
							
							else
							{
								message = name + ": " + line;
							}
							
							byte[] buffer = message.getBytes();
							
							
							
							try
							{
								DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
								clientSocket.send(packet);
							}
							
							catch(UnknownHostException e)
								{
									e.printStackTrace();
								}
						}
						catch(IOException e)
							{
								e.printStackTrace();
								println("IO error line :26");
							}
					}
		}
		
		/**
		*  print method
		*  print the string in parameter
		*  @param print String
		**/
		
		public static synchronized void print(String str)
			{
				System.out.print(str);
			}
		
		/**
			*  println method
			*  print the string in parameter and return to line
			*  @param println String
			**/
		
		public static synchronized void println(String str)
		{
			System.out.println(str);
		}
	}
	
