///A Simple Web Server (WebServer.java)

package http.server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  protected static final String ROOT_PATH = "doc";
  protected static final String INDEX_FILE = "index.html";
  protected static final ArrayList<String> httpMethods = new ArrayList<>(Arrays.asList("GET", "DELETE", "HEAD", "POST", "PUT"));
  /**
   * WebServer constructor.
   */
  protected void start(int port) {
    ServerSocket s;

    System.out.println("Webserver starting up on port " +port);
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(port);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      BufferedReader in = null; PrintWriter out = null; 
      BufferedOutputStream dataOut = null; BufferedInputStream dataIn = null;
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        out = new PrintWriter(remote.getOutputStream());
        dataOut = new BufferedOutputStream(remote.getOutputStream());
        dataIn = new BufferedInputStream(remote.getInputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String str = ".";
        String request = "";
        HashMap<String, String> header = new HashMap<String, String>();
        int indexline = 0;
        
        //***************** Parsing of header ***************//
        while (str != null && !str.equals("")){
          str = in.readLine();
          if(indexline == 0){
            request += str;
            header.put("Request_methode", request.split(" ")[0]);
            header.put("Request_resource", request.split(" ")[1].substring(1));
            if(header.get("Request_resource").length()==0){
                header.put("Request_resource", INDEX_FILE);
            }
          }
          if(indexline > 0){
            String[] splittedLine = str.split(": ");
            if(splittedLine.length > 1)
              header.put(splittedLine[0], splittedLine[1]);
          }
          indexline++;
        }  
        //**************** End of header parsing *************//
        
        System.out.println("REQUEST METHOD: " + header.get("Request_methode"));
        System.out.println("REQUEST: " + request);
        System.out.println("REQUESTED RESSOURCE: \"" + header.get("Request_resource") + "\"\n");
        // Send the response
        // Send the headers
        if(!httpMethods.contains(header.get("Request_methode"))){
          printHeader(dataOut, headerStringifier("501", "Not Implemented")); 
        }
        else {
          switch (header.get("Request_methode")){
              case "GET":
                httpGetMethode(dataOut, header.get("Request_resource"));
                break;
              case "HEAD":
                httpHeadMethode(dataOut, header.get("Request_resource"));
                break;
              case "DELETE":
                httpDeleteMethode(dataOut, header.get("Request_resource"));
                break;
              case "PUT":
                httpPutMethode(dataIn, dataOut, header.get("Request_resource"));
                break;
              case "POST":
                httpPostMethode(dataIn, dataOut, header.get("Request_resource"));
                break;
          }
          
          /* out.println("HTTP/1.0 200 OK");
          out.println("Content-Type: text/html");
          out.println("Server: Bot");
          // this blank line signals the end of the headers
          out.println("");
          // Send the HTML page
          out.println("<H1>Welcome to the Ultra Mini-WebServer</H2>"); */
          dataOut.flush();
          out.flush();
          remote.close();
        }
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /**
   * This method returns de requested resource
   * 
   * filename correct gives 200 OK
   * @exception internalE gives 500 Internal Server Error
   * filename incorrect gives 404 Not Found
   *
   * @param dataOut is the output stream for client socket
   * @param filename is the name of the file requested
   */
  protected void httpGetMethode(BufferedOutputStream dataOut, String filename){
    try{
      File file = new File(ROOT_PATH, filename);
      if(!(file.exists() && file.isFile())){
        printHeader(dataOut, headerStringifier(filename, file.length(), "404", "Not Found"));
        dataOut.write(("<h1>File not found<h1>").getBytes());
      }
      else {
        printHeader(dataOut, headerStringifier(filename, file.length(), "200", "OK"));  
        byte[] bytes = Files.readAllBytes(file.toPath());
        dataOut.write(bytes);
      }
    }
    catch (Exception internalE){
      try{
        printHeader(dataOut, headerStringifier("500", "Internal Server Error"));
      }
      catch (Exception e){
      }
    }
  }
  
  
  /**
   * This method checks the resource existance
   * 
   * filename correct gives 200 OK
   * @exception internalE gives 500 Internal Error
   * filename incorrect gives 404 Not Found
   *
   * @param dataOut is the output stream for client socket
   * @param filename is the name of the file requested
   */
  protected void httpHeadMethode(BufferedOutputStream dataOut, String filename){
    try{
      File file = new File(ROOT_PATH, filename);
      if(!(file.exists() && file.isFile())){
        printHeader(dataOut, headerStringifier(filename, file.length(), "404", "Not Found"));
      }
      else {
        printHeader(dataOut, headerStringifier(filename, file.length(), "200", "OK"));
      }
    }
    catch (Exception internalE){
      try{
        printHeader(dataOut, headerStringifier("500", "Internal Server Error"));
      }
      catch (Exception e){
      }
    }
  }
 
  /**
   * This method deletes de requested resource on the server
   * 
   * filename correct and file deleted give 204 No Content
   * @exception internalE gives 500 Internal Error
   * filename incorrect gives 404 Not Found
   * file not deleted and missing rights give 403 Forbidden
   *
   * @param dataOut is the output stream for client socket
   * @param filename is the name of the file requested
   */
  protected void httpDeleteMethode(BufferedOutputStream dataOut, String filename){
    try{
      File file = new File(ROOT_PATH, filename);
      if(!(file.exists() && file.isFile())){
        printHeader(dataOut, headerStringifier(filename, file.length(), "404", "Not Found"));
      }
      else {
        if(file.delete()){
          printHeader(dataOut, headerStringifier(filename, file.length(), "204", "No Content")); 
        }
        else{
          printHeader(dataOut, headerStringifier(filename, file.length(), "403", "Forbidden")); 
        }
      }
    }
    catch (Exception internalE){
      printHeader(dataOut, headerStringifier("500", "Internal Server Error"));      
    }
  }
 
  /**
   * This method creates a file and adds the given ressource content to the new file
   * or overwrites on an existing file with same pathname
   * 
   * file overwritten gives 200 OK
   * @exception internalE gives 500 Internal Error
   * file created gives 201 Created
   *
   * @param dataIn is the input stream from client socket
   * @param dataOut is the output stream for client socket
   * @param filename is the name of the file requested
   */
  protected void httpPutMethode(BufferedInputStream dataIn, BufferedOutputStream dataOut, String filename){
    try{
      File file = new File(ROOT_PATH, filename);
      boolean overwritten = file.exists();
      FileOutputStream fileOut = new FileOutputStream(file);
      byte[] bytes = dataIn.readAllBytes();
      fileOut.write(bytes);
      fileOut.flush();
      fileOut.close();
      if(overwritten){
        printHeader(dataOut, headerStringifier(filename, file.length(), "200", "OK"));
      }
      else {
        printHeader(dataOut, headerStringifier(filename, file.length(), "201", "Created"));
      }
    }
    catch (Exception internalE){
      try{
        printHeader(dataOut, headerStringifier("500", "Internal Server Error"));
      }
      catch (Exception e){
      }
    }
  }
  
  /**
   * This method appends data on a file to the given pathname 
   * or creates the file if doesn't exist
   * 
   * data appened gives 200 OK
   * @exception internalE gives 500 Internal Error
   * file created gives 201 Created
   *
   * @param dataIn is the input stream from client socket
   * @param dataOut is the output stream for client socket
   * @param filename is the name of the file requested
   */
  protected void httpPostMethode(BufferedInputStream dataIn, BufferedOutputStream dataOut, String filename){
    try{
      File file = new File(ROOT_PATH, filename);
      boolean overwritten = file.exists();
      FileOutputStream fileOut = new FileOutputStream(ROOT_PATH+"/"+filename, true);
      byte[] bytes = dataIn.readAllBytes();
      fileOut.write(bytes);
      fileOut.flush();
      fileOut.close();
      if(overwritten){
        printHeader(dataOut, headerStringifier(filename, file.length(), "200", "OK"));
      }
      else {
        printHeader(dataOut, headerStringifier(filename, file.length(), "201", "Created"));
      }
    }
    catch (Exception internalE){
      try{
        printHeader(dataOut, headerStringifier("500", "Internal Server Error"));
      }
      catch (Exception e){
      }
    }
  }
  
  
  /**
   * This method creates a string for the header with content-type and length... 
   *
   * @param fileLength is the length of the file requested
   * @param filename is the name of the file to header stringify
   * @param status is the status code for the header
   * @param message is the message corresponding to the status
   * 
   * @return the string header
   */
  protected String headerStringifier(String filename, long fileLength, String status, String message){
    String fileExtension = filename.substring(filename.lastIndexOf(".")+1);
    String content_type = null;
    switch(fileExtension){
        case "html":
          content_type = "text/html";
          break;
        case "htm":
          content_type = "text/html";
          break;
        case "mp4":
          content_type = "video.mp4";
          break;
        case "avi":
          content_type = "video/x-msvideo";
          break;
        case "mp3":
          content_type = "audio/mp3";
          break;
        case "jpeg":
          content_type = "image/jpg";
          break;
        case "png":
          content_type = "image/png";
          break;
        case "css":
          content_type = "text/css";
          break;
        case "txt":
          content_type = "text/plain";
          break;
        case "pdf":
          content_type = "application/pdf";
          break;
        default:
          content_type = "undefined";
          break;
    }
    String header = "HTTP/1.0 ";
    header += status + " ";
    header += message + "\r\n";
    header += "content-type: " + content_type + "\r\n";
    header += "content-length: " + fileLength + "\r\n";
    header += "Server: Bot\r\n";
    header += "\r\n";
    return header;
  }  
  
  /**
   * This method creates a string for the header
   *
   * @param status is the status code for the header
   * @param message is the message corresponding to the status
   * 
   * @return the string header
   */
  protected String headerStringifier(String status, String message){
    String header = "HTTP/1.0 ";
    header += status + " ";
    header += message + "\r\n";
    header += "Server: Bot\r\n";
    header += "\r\n";
    return header;
  }

  protected void printHeader(BufferedOutputStream dataOut, String header){
    try{
      dataOut.write(header.getBytes());
      System.out.println(header); 
    }
    catch (Exception e){
      System.out.println("ERROR: " +e);
    }    
  }
  
  
  /**
   * Start the application.
   * 
   * @param args contains server port
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    
    if (args.length != 1) {
      System.out.println("Usage: java EchoServer <EchoServer port>");
      System.exit(1);
    }
    
    try{
      InetAddress ip = InetAddress.getLocalHost();
      String hostname = ip.getHostName();
      System.out.println("L'hostname du server est: " +hostname);
    }
    catch (UnknownHostException e) {
      e.printStackTrace();
    }
    
    ws.start(Integer.parseInt(args[0]));
  }
}
