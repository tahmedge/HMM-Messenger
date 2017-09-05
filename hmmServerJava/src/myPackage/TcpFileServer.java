/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myPackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import static myPackage.TcpServer.clientTable;

/**
 *
 * @author XN
 */
public class TcpFileServer extends Thread {

    private static ServerSocket fileServerSocket, fileSocket;
    private static Socket clientSocket, clientSocket2;
    private static InputStream inputStream;
    private static FileOutputStream fileOutputStream;
    private static BufferedOutputStream bufferedOutputStream;
    private static final int filesize = 10000000; // filesize temporary hardcoded 
    private static int bytesRead;
    private static int current = 0;
    private boolean running = true;

    private static ServerSocket serverSocket;
    private static Socket clientSocket3;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;

    public TcpFileServer() {

    }

    public static void main(String[] args) {

    }

    private void runFileServer() throws IOException {
        fileServerSocket = new ServerSocket(4445);  //Server socket
        fileSocket = new ServerSocket(4446);
        System.out.println("File Server started. Listening to the port 4445");

        while (running) {

            clientSocket = fileServerSocket.accept();

            byte[] mybytearray = new byte[filesize];    //create byte array to buffer the file
            if (clientSocket != null) {
                System.out.println("Client joined!  " + clientSocket);
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String from = in.readLine();
            String to = in.readLine();
            String fileName = in.readLine();
            String extension = in.readLine();
            //int st = fileName.lastIndexOf(".");
            //String extension = fileName.substring(st);
            //System.out.println(extension);
            //System.out.println(from);
            //in.close();

            clientSocket2 = fileSocket.accept();

            inputStream = clientSocket2.getInputStream();

            DateFormat dateFormat = new SimpleDateFormat("ddMMyy-ss");
            Date date = new Date();
            System.out.println(dateFormat.format(date));
            fileOutputStream = new FileOutputStream("G:\\" +fileName+"  "+ dateFormat.format(date) + extension);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            System.out.println("Receiving...");

            //following lines read the input slide file byte by byte
            bytesRead = inputStream.read(mybytearray, 0, mybytearray.length);
            current = bytesRead;

            do {
                bytesRead = inputStream.read(mybytearray, current, (mybytearray.length - current));
                if (bytesRead >= 0) {
                    current += bytesRead;
                }
            } while (bytesRead > -1);

            bufferedOutputStream.write(mybytearray, 0, current);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

            forwardFileTo(from,to, fileName+"  "+dateFormat.format(date) + extension, mybytearray);
            //inputStream.close();
            //clientSocket.close();
            //serverSocket.close();
            System.out.println("Server recieved the file");
        }
    }

    @Override
    public void run() {
        super.run();

        try {
            runFileServer();
        } catch (IOException ex) {
            Logger.getLogger(TcpFileServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void close() {

        running = false;

        try {
            fileServerSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("S: Done.");
        fileServerSocket = null;

        //todo close all user connections
    }

    public void forwardFileTo(String from, String to, String name, byte[] file) throws IOException {
        Socket sock = TcpServer.clientTable.get(from);
        if (sock != null) {
            PrintWriter bufferSend = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
            bufferSend.println(to+"#file_incoming");
            bufferSend.flush();
            bufferSend.println(name);
            bufferSend.flush();
            /*OutputStream outToClient;
             //System.out.println("Message forwarded to " +to+"  MSG: "+newMsg);
             outToClient = new BufferedOutputStream(sock.getOutputStream());
             outToClient.write(file, 0, file.length);
             outToClient.flush();*/
            //outToClient.close();
            try {
                serverSocket = new ServerSocket(4448); // Server socket

            } catch (IOException e) {
                System.out.println("Could not listen on port: 4448");
            }

            System.out.println("Server started. Listening to the port 4448");

            try {

                clientSocket = serverSocket.accept(); // accept the client connection
                if (clientSocket != null) {
                    System.out.println("Cli   Systement joined!  " + clientSocket);
                                    //

                    File myFile = new File("G:\\" + name);
                    byte[] mybytearray = new byte[(int) myFile.length()];
                    OutputStream outToClient = null;
                    FileInputStream fis = null;
                    outToClient = new BufferedOutputStream(clientSocket.getOutputStream());
                    try {
                        fis = new FileInputStream(myFile);
                    } catch (FileNotFoundException ex) {
                        // Do exception handling
                    }
                    BufferedInputStream bis = new BufferedInputStream(fis);

                    try {
                        bis.read(mybytearray, 0, mybytearray.length);
                        outToClient.write(mybytearray, 0, mybytearray.length);
                        outToClient.flush();
                        clientSocket.close();
                        serverSocket.close();
                        fis.close();
                        bis.close();
                        boolean delete;
                        // File sent, exit the main method
                        //return;
                        delete = myFile.delete();
                    } catch (IOException ex) {
                        // Do exception handling
                    }
                }
                /*inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                 bufferedReader = new BufferedReader(inputStreamReader); // get the client message
                 message = bufferedReader.readLine();
 
                 System.out.println(message);
                 inputStreamReader.close();
                 clientSocket.close(); */

            } catch (IOException ex) {
                System.out.println("Problem in message reading");
            }
        } else {
            System.out.println("Message forward ... client not found");
        }

    }

}
