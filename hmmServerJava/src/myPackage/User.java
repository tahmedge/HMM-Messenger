/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myPackage;

/**
 *
 * @author XN
 */
public class User {

    private String username;
    private String message;
    private User userToSendMessage;
    private int userID;

    public User(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public User() {

    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUserToSendMessage() {
        return userToSendMessage;
    }

    public void setUserToSendMessage(User userToSendMessage) {
        this.userToSendMessage = userToSendMessage;
    }
}

