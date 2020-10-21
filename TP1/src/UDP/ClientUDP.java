/***
 * ClientUDP
 * Client of the UDP chat
 * 
 * Date: 21/10/20
 * Authors: OUVRARD/GIRARD
 */
 
 package UDP;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientUDP
{	
  /**
  *  main method
  *  Open the UDP client and manage the receiving of the messages
  *  @param ClientUDP multicastAddress, port
  **/
	public static void main(String[] args) throws IOException
			{
				MulticastSocket clientSocket = null;
				int port=9999;
				InetAddress group = null;
				String pseudo = null;
				SendThread st = null;
				
				if (args.length != 2)
				{
					println("Usage java ClientUDP <Group address> <Port>");
					System.exit(0);
				}
				
				try
				{
					port = new Integer(args[1]).intValue();
					clientSocket = new MulticastSocket(port);
					group = InetAddress.getByName(args[0]);
					clientSocket.joinGroup(group);
				}
				
				catch(Exception e)
				{
					e.printStackTrace();
				}
												
				print("Votre pseudo: ");
				Scanner scanner = new Scanner(System.in);
				pseudo = scanner.nextLine();
				
				String message ="--- " + pseudo + " joined the group." + " ---";
				DatagramPacket joinPacket = new DatagramPacket(message.getBytes(),message.length(), 
				group, port);
				clientSocket.send(joinPacket);
				
				     /**
					 * SendThread is a thread that manage to send the
					 * messages to all client connected
					 * 
					 * @see SendThread
					 */
				
				try
				{
					st = new SendThread(group ,port, pseudo, clientSocket);
					st.start();			
				}
				catch(Exception e)
				{
					System.err.println(e);
				}
				
				String receivedMessage;
				String leaveMessage = pseudo + " has left the group.";
				while(true)
				{
					byte[] readBuffer = new byte[8196];
					DatagramPacket readPacket = new DatagramPacket(readBuffer, readBuffer.length);
					clientSocket.receive(readPacket);
					receivedMessage = new String(readPacket.getData());
					
					if(!receivedMessage.substring(0, pseudo.length()).equals(pseudo))
					{	
						println(receivedMessage);
					}
					
					else if(receivedMessage.substring(0, leaveMessage.length()).equals(leaveMessage))
					{
						println("Goodbye");
						st.stop();
						clientSocket.close();
						System.exit(0);
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
