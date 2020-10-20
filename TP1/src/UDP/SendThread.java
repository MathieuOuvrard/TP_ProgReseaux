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
								
								//packet.setData(buffer);
								
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
		
		public static synchronized void print(String str)
			{
				System.out.print(str);
			}
	
		public static synchronized void println(String str)
		{
			System.out.println(str);
		}
	}
	
