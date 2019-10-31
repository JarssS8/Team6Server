/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.beans.Message;
import utilities.beans.User;
import utilities.exception.DBException;
import utilities.exception.LogicException;
import utilities.exception.LoginAlreadyTakenException;
import utilities.exception.LoginNotFoundException;
import utilities.exception.WrongPasswordException;
import utilities.interfaces.Connectable;

/**
 * This class will create threads, read objects from the socket, interpretate them
 * and buld an answer that will be sent via socket.
 * @author aimar
 */
public class ServerWorkerThread extends Thread {
    
    private static final Logger LOGGER=Logger.getLogger("server.logic.ServerWorkerThread");
    private ObjectInputStream receive = null;
    private ObjectOutputStream send = null;
    private User user = null;
    private Message message = null;
    private String type;
    private Connectable dao = ConnectableFactory.getDAO();
    private Socket socket;
    
    /**
     * This method receives a socket from ApplicationServer and assigns it to
     * a local socket object.
     * @param socket A socket that contains the message from client side.
     */
    public void setSocket(Socket socket) {
        this.socket=socket;
    }
 
    /**
     * This method executes the thread.
     */
    @Override
    public void run() {
        try {
            //Reading from the socket
            receive = new ObjectInputStream(socket.getInputStream());
            send = new ObjectOutputStream(socket.getOutputStream());
            LOGGER.info("Starting to read the message...");
            message = (Message) receive.readObject();
            user = message.getUser();
            type = message.getType();
            LOGGER.info("User wants to "+type);
            LOGGER.info("Starting to decide...");
            //Interpreting clients request
            switch(type) {
                case Message.LOGIN_MESSAGE: {
                    this.user=dao.logIn(user);
                    LOGGER.info("Initiating login...");
                    break;
                }
                case Message.SIGNUP_MESSAGE: {
                    this.user=dao.signUp(user);
                    LOGGER.info("Initiating sign up...");
                    break;
                }
                case Message.LOGOUT_MESSAGE: {
                    dao.logOut(user);
                    LOGGER.info("Initiating log out...");
                    break;
                }
            }
            //Sending answer to the client
            message.setUser(this.user);
            message.setType(dao.getMessage());
            LOGGER.info("Message loaded to return: "+dao.getMessage());
            send.writeObject(message);
            LOGGER.info("Message sent...");
            
            
        } catch (IOException e1) {
            LOGGER.severe("ERROR"+e1.getMessage());
        } catch (ClassNotFoundException e2) {
            LOGGER.severe("ERROR en la lectura de objetos"+e2.getMessage());
        } catch (LoginNotFoundException ex) {
            LOGGER.severe(type);
        } catch (WrongPasswordException ex) {
            LOGGER.severe(type);
        } catch (LogicException ex) {
            LOGGER.severe(type);
        } catch (LoginAlreadyTakenException ex) {
            LOGGER.severe(type);
        } catch (DBException ex) {
            LOGGER.severe(type);
        } finally {
            try {
                //Closing Streams and the socket
                if (send != null) {
                    send.close();
                    LOGGER.info("ObjectImputStream closed...");
                }
                    
                if (receive != null) {
                    receive.close();
                    LOGGER.info("ObjectOutputStream closed...");
                }
                    
                if (socket != null) {
                    socket.close();
                    LOGGER.info("Socket closed...");
                }
                    
            } catch (IOException ex) {
                LOGGER.severe(type);
            }
            
        }
          
    }
    
}