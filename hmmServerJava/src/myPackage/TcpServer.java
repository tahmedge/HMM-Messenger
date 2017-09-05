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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JFrame;



/**
 * The class extends the Thread class so we can receive and send messages at the same time
 *
 * @author Catalin Prata
 *         Date: 2/12/13
 */
public class TcpServer extends Thread {

    public static final int SERVERPORT = 4444;
    // while this is true the server will run
    private boolean running = false;
    // callback used to notify new messages received
    private OnMessageReceived messageListener;
    private ServerSocket serverSocket;
    private ArrayList<UserManager> connectedUsers;
    public static Hashtable<String, Socket> clientTable = new Hashtable<String, Socket>();
    
    

    
    
    MainScreen main;
    /**
     * Constructor of the class
     *
     * @param messageListener listens for the messages
     */
    Db dataBase;
    public TcpServer(OnMessageReceived messageListener , MainScreen x) {
        this.messageListener = messageListener;
        connectedUsers = new ArrayList<UserManager>();
        this.main=x;
    }

    public static void main(String[] args) {

        //opens the window where the messages will be received and sent
        MainScreen frame = new MainScreen();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    /**
     * Close the server
     */
    public void close() {

        running = false;

        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("S: Done.");
        serverSocket = null;

        //todo close all user connections

    }

    /**
     * Method to send the messages from server to all clients
     *
     * @param user a user object containing the message to be sent and also its username
     */
    public void sendMessage(User user) {

        if (connectedUsers != null) {

            for (UserManager userManager : connectedUsers) {
                if (userManager.getUser().getUserID() != user.getUserID()) {
                    userManager.sendMessage(user.getUsername() + " says: " + user.getMessage());
                }
            }

        }

    }

    /**
     * Builds a new server connection
     */
    private void runServer() {
        running = true;

        try {
            //create a server socket. A server socket waits for requests to come in over the network.
            serverSocket = new ServerSocket(SERVERPORT);
            //main.appendText("\n\nServer Started .. Waiting for clients ... \n");
            System.out.println("Server Started.... ");
            dataBase = new Db();
            while (running) {
                // create a loop and get all the incoming connections and create users with them
                
                //System.out.println("S: Waiting for a client ...");

                //create client socket... the method accept() listens for a connection to be made to this socket and accepts it.
                Socket client = serverSocket.accept();

                //System.out.println("Remote IP :"+client.getInetAddress());

                    //System.out.println("Remote Port :"+client.getPort());  

                // start reading messages from the client
               
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String incoming = in.readLine();
                
                if(incoming.equals("*#hmmlogin#*")==true)
                {
                    String userName = null;
                    String pass = null;
                    userName = in.readLine();
                    System.out.println(userName);
                    pass = in.readLine();
                    System.out.println(pass);

                    UserManager userManager = new UserManager(client, this, userName);
                    // add the new user to the stack of users
                    int valid = dataBase.checkValidity(userName, pass);
                    System.out.println("checking validity");
                    if(valid == 1)
                    {
                        connectedUsers.add(userManager);
                        clientTable.put(userName, client);
                        //MainScreen.messagesArea.append("\n"+message+" has joined");
                        main.appendText("\n"+userName+" has joined");
                        userManager.sendValidation("valid");
                        //main.appendText("\nValidation sent to "+userName);
                        userManager.start();
                        dataBase.setOnline(userName);
                        System.out.println("S: "+userName+" connected ...");
                    }

                    /*if(message.equals("xn")==true || message.equals("lee")==true)
                    {
                            connectedUsers.add(userManager);
                            clientTable.put(message, client);
                            //MainScreen.messagesArea.append("\n"+message+" has joined");
                            main.appendText("\n"+message+" has joined");
                            userManager.sendValidation("valid");
                             main.appendText("\nValidation sent to "+message);
                            userManager.start();
                            System.out.println("S: New client connected ...");

                    }*/
                    else
                    {
                            userManager.sendValidation("invalid");
                            main.appendText("\n"+userName+" is invalid user");
                            System.out.println("S: "+userName+" failed to connect ...");
                            //userManager.destroy();
                    }
                }
                
                else if(incoming.equals("*#hmmSignUp#*")==true)
                {
                    String userName = null;
                    String pass = null;
                    String phone = null;
                    String fullName;
                    fullName = in.readLine();
                    userName = in.readLine();
                    //System.out.println(userName);
                    phone = in.readLine();
                    pass = in.readLine();
                    UserManager userManager = new UserManager(client, this, userName);
                    int valid = dataBase.checkSignUpValidity(userName, phone);
                    if(valid==1)
                    {
                        userManager.sendValidation("invalid1");
                        //userManager.destroy();
                    }
                    else if(valid==2)
                    {
                        userManager.sendValidation("invalid2");
                        //userManager.destroy();
                    }
                    else if(valid==3)
                    {
                        userManager.sendValidation("invalid3");
                        //userManager.destroy();
                    }
                    else
                    {
                        dataBase.addUser(fullName, userName, phone, pass);
                        userManager.sendValidation("valid");
                        //userManager.destroy();
                    }
                }
                

                
            }

        } catch (Exception e) {
            System.out.println("S: Error");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        runServer();

    }
    public void removeUser(UserManager UM)
    {
        connectedUsers.remove(UM);
    }

    public void userConnected(User connectedUser) {

        messageListener.messageReceived("User " + connectedUser.getUsername() + "is now connected.");

    }

    public void userDisconnected(UserManager userManager, String uName) throws ClassNotFoundException, SQLException {

        // remove the user from the list of connected users
        connectedUsers.remove(userManager);
        clientTable.remove(uName);
        main.appendText("\n"+uName+" has logged out");
        System.out.println(uName+" has logged out");
        dataBase.setOffline(uName);

    }

    public void messageReceived(User fromUser, User toUser) {

        messageListener.messageReceived("User " + fromUser.getUsername() + " says: " + fromUser.getMessage() + " to user: " + (toUser == null ? "ALL" : toUser.getUsername()));
        // send the message to the other clients
        sendMessage(fromUser);

    }

    public void forwardTo(String to, String newMsg) throws IOException {
        Socket sock = clientTable.get(to);
        if(sock!=null)
        {
            PrintWriter bufferSend = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
            bufferSend.println(newMsg);
            bufferSend.flush();
            System.out.println("Message forwarded to " +to+"  MSG: "+newMsg);
        }
        else
        {
            System.out.println("Message forward ... client not found");
        }
    }
    
    public void forwardFileTo(String to, String type, byte[] file) throws IOException {
        Socket sock = clientTable.get(to);
        if(sock!=null)
        {
            PrintWriter bufferSend = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
            bufferSend.println("server#file_incoming");
            bufferSend.flush();
            /*OutputStream outToClient;
            //System.out.println("Message forwarded to " +to+"  MSG: "+newMsg);
            outToClient = new BufferedOutputStream(sock.getOutputStream());
            outToClient.write(file, 0, file.length);
            outToClient.flush();*/
        }
        else
        {
            System.out.println("Message forward ... client not found");
        }
    }

    //Declare the interface. The method messageReceived(String message) will/must be implemented in the ServerBoard
    //class at on startServer button click
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}
