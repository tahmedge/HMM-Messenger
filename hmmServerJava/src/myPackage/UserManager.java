/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPackage;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages a single user(client) read/write operations
 * <p/>
 * Created by catalin on 12/30/13.
 */
public class UserManager extends Thread {

    // contains information about the current user
    private User user;
    // the socket that links the user(client) to this server
    public Socket socket;
    private PrintWriter bufferSender;
    private static FileOutputStream fileOutputStream;
    private static BufferedOutputStream bufferedOutputStream;
    
    private static final int filesize = 10000000; // filesize temporary hardcoded 
    private static int bytesRead;
    private static int current = 0;
    // flag used to stop the read operation
    private boolean running;
    // used to notify certain user actions like receiving a message or disconnect
    private TcpServer tcpServer;
    public String uName;
    private static InputStream inputStream;

    public UserManager(Socket socket, TcpServer tcp, String uNm) {
        this.user = new User();
        this.socket = socket;
        this.tcpServer = tcp;
        this.uName=uNm;
        running = true;
    }

    public User getUser() {
        return user;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        super.run();

        System.out.println("S: Receiving...");

        try {

            //sends the message to the client
            bufferSender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            //read the message received from client
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //in this while we wait to receive messages from client (it's an infinite loop)
            //this while it's like a listener for messages
            while (running) {

                String message = null;
                //System.out.println("Loop Iteration");
                try {
                    message = in.readLine();
                    //System.out.println(message);
                } catch (IOException e) {
                    System.out.println("Error reading message from: " + uName);
                    tcpServer.userDisconnected(this, uName);
                    //managerDelegate.
                    break;
                }

                if (message != null /*&& managerDelegate != null*/) {
                    //user.setMessage(message);
                    //System.out.println(uName+" says: "+message);
                    System.out.println("incoming");
                    if(message.equals("out$5719$")==true)
                    {
                        tcpServer.userDisconnected(this, uName);
                        
                        break;
                        
                    }
                    else{
                        if(message.equals("xn#lee$hello")==true)
                        {
                            
                            sendMessage("lee#hi there!!");
                            System.out.println("hi there xn");
                        }
                        else if(message.equals("load#online#users")==true)
                        {
                            Db z = new Db();
                            z.getOnlineUser();
                            int i=0;
                            while(i<Db.totalOnlineUSer)
                            {
                                if(uName.equals(Db.onlineUser[i])==false){
                                    sendMessage("onlineUsersFromServer#"+Db.onlineUser[i]);
                                }
                                i++;
                            }
                        }
                        else if(message.equals("##sendimage##")==true)
                        {
                            String to = in.readLine();
                            System.out.println(to + "  Image is Comming");
                            byte[] mybytearray = new byte[filesize];
                            inputStream = socket.getInputStream();
                            DateFormat dateFormat = new SimpleDateFormat("yyyy1MM1ddHH2mm2ss");
                            Date date = new Date();
                            System.out.println(dateFormat.format(date));
                            fileOutputStream = new FileOutputStream("G:\\"+dateFormat.format(date)+".jpg");
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
                            //bufferedOutputStream.close(); 
                            //inputStream.close();
                            System.out.println("Image Recieved");
                        }
                        else
                        {
                           int x = message.indexOf("#");
                           int y = message.indexOf("$");
                           String from = message.substring(0, x);
                           String to = message.substring(x+1, y);
                           String msg = message.substring(y+1);
                           
                           String newMsg = from+"#"+msg;
                           tcpServer.forwardTo(to, newMsg);
                           System.out.println(newMsg);
                        }
                        
                        /*int x = message.indexOf("#");
                        int y = message.indexOf("$");
                        String from = message.substring(0, x);
                        String to = message.substring(x+1, y);
                        String msg = message.substring(y+1);
                        String newMsg = from+"#"+msg;
                        sendMessage("lee#"+msg);*/
                    }
                    
                    
                    // notify message received action
                    //managerDelegate.messageReceived(user, null);
                }
            }

        } catch (Exception e) {
            System.out.println("S: Error");
            try {
                tcpServer.userDisconnected(this, uName);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            e.printStackTrace();
        }

    }

    /**
     * Close the server
     */
    public void close() {

        running = false;

        if (bufferSender != null) {
            bufferSender.flush();
            bufferSender.close();
            bufferSender = null;
        }

        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("S: User " + user.getUsername() + " leaved the room.");
        socket = null;

        //todo close all user connections

    }

    /**
     * Method to send the messages from server to client
     *
     * @param message the message sent by the server
     */
    public void sendMessage(String message) {
    	//System.out.println("validation ready to go");
    	
        if (bufferSender != null && !bufferSender.checkError()) {
            bufferSender.println(message);
            bufferSender.flush();
            //System.out.println("validation sent");
        }
    }
    public void sendValidation(String v) throws IOException {
    	//System.out.println("validation ready to go");
    	
    	PrintWriter bufferSend = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        bufferSend.println(v);
        bufferSend.flush();
        System.out.println("validation sent " +v);
        //MainScreen.messagesArea.append("\n" + "Validation Sent " +v);
        
    }

}
