/***
 * ClientThread
 * The server create one of this thread for each client who connects 
 * The tchat will save messages in a file
 * Date: 21/10/20
 * Authors: OUVRARD/GIRARD
 */

package stream;

import java.io.*;
import java.net.*;

public class ClientThread
	extends Thread {
	
	private Socket clientSocket;
	
	ClientThread(Socket s) {
		this.clientSocket = s;
	}

 	/**
  	* receives the messages from client 
  	* and send it to all other connected clients.
  	* Write the historic of the conversation too
  	* @param clientSocket the client socket
  	**/
	public void run() {
    	  try
		  {
		  	BufferedReader socIn = null;
			BufferedReader feedReader = null;
			String feedLine;

			socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			PrintStream socOut = new PrintStream(clientSocket.getOutputStream());

			try
			{
			  feedReader = new BufferedReader
						  (new FileReader(EchoServerMultiThreaded.historicFile));
			  System.out.println("Ouverture historique");
			}
			catch(FileNotFoundException exc)
			{
			  socOut.println("Erreur d'ouverture historique");
			  System.exit(2);
			}

			try
			{
				System.out.println("Lecture historique");
			  while ((feedLine = feedReader.readLine()) != null)
			  {
				  socOut.println(feedLine);
			  }

			  feedReader.close();
			  System.out.println("Fermeture historique");
			}

			catch (IOException ioException)
			{
			  System.out.println("Erreur ecriture historique");
			}


			PrintWriter feedWriter;

    		while (true) {
				String line = socIn.readLine();
				System.out.println("Ouverture writer");
				feedWriter = new PrintWriter(new BufferedWriter
						(new FileWriter(EchoServerMultiThreaded.historicFile, true)));

				if(line.equals("leave"))
				{

					break;
				}

    			for(int i=0; i<EchoServerMultiThreaded.connectedClients.size(); i++)
				{
					if(EchoServerMultiThreaded.connectedClients.get(i)!=this.clientSocket)
					{
						socOut = new PrintStream(EchoServerMultiThreaded.connectedClients.get(i).getOutputStream());
						socOut.println(line);

					}
				}

    		 	System.out.println(line);
    			feedWriter.println(line);
    			feedWriter.close();
				System.out.println("Fermeture writer");
    		}
    		feedWriter.close();
    		System.out.println("Fermeture du socket client suite à leave");

    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
       }
  
  }

  
