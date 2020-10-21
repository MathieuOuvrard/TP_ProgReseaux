/***
 * EchoClient
 * Client of the TCP chat
 * The tchat will save messages in a file
 * Date: 21/10/20
 * Authors: OUVRARD/GIRARD
 */
package stream;

import java.io.*;
import java.net.*;
import java.util.*;



public class EchoClient {

 
  /**
  *  main method
  *  Open the TCP client 
  *  @param EchoClient address, port
  **/
    public static void main(String[] args) throws IOException {

        Socket echoSocket;
        PrintStream socOut;
        BufferedReader stdIn;
        BufferedReader socIn;

        if (args.length != 2) {
          System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
          System.exit(1);
        }

        Scanner scanner = new Scanner( System.in );
        System.out.print("Votre pseudo: ");
        String pseudo = scanner.nextLine();


        try {
      	    // creation socket ==> connexion
      	    echoSocket = new Socket(args[0],new Integer(args[1]).intValue());
	    socIn = new BufferedReader(
	    		          new InputStreamReader(echoSocket.getInputStream()));    
	    socOut= new PrintStream(echoSocket.getOutputStream());
	    stdIn = new BufferedReader(new InputStreamReader(System.in));


            Thread receive = new Thread(new Runnable() {
                String line;
                @Override
                public void run() {
                    try {
                        while(true)
                        {
                            line = socIn.readLine();
                            System.out.println(line);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receive.start();

        while (true) {
            try {
                String line = stdIn.readLine();
                if (line.equals("leave"))
                {
                    socOut.println(line);
                    break;
                }
                socOut.println(pseudo + ": " + line);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("cette erreur");
            }
        }

        socOut.close();
        socIn.close();
        stdIn.close();
        echoSocket.close();
        System.exit(0);
        }

        catch (UnknownHostException e)
        {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        }

        catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:"+ args[0]);
            System.exit(1);
        }

    }
}


