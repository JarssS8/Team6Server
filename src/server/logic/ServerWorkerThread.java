/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;
import server.ApplicationServer;
import utilities.beans.Message;
import utilities.beans.User;
import utilities.exception.LoginAlreadyTakenException;
import utilities.exception.LoginNotFoundException;
import utilities.exception.ServerConnectionErrorException;
import utilities.exception.WrongPasswordException;
import utilities.interfaces.Connectable;

/**
 * This class will create threads, read objects from the socket, interpretate them
 * and buld an answer that will be sent via socket.
 * @author aimar
 */
public class ServerWorkerThread extends Thread {
    
    private static final Logger LOGGER=Logger.getLogger("server.logic.ServerWorkerThread");
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;
    private User user = null;
    private Message messageIn = null;
    private Message messageOut = null;
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
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            LOGGER.info("Starting to read the message...");
            messageIn = (Message) objectInputStream.readObject();
            user = messageIn.getUser();
            type = messageIn.getType();
            LOGGER.info("User wants to "+type);
            LOGGER.info("Starting to decide...");
            
            //Interpreting clients request
            messageOut=interpreteMessage(messageIn);
            //Sending answer to the client
            
            messageOut.setUser(this.user);
            LOGGER.info("Message loaded to return: ");
            objectOutputStream.writeObject(messageOut);
            LOGGER.info("Message sent...");
            //Closing Streams and the socket
            if (objectOutputStream != null) {
                objectOutputStream.close();
                LOGGER.info("ObjectImputStream closed...");
            }

            if (objectInputStream != null) {
                objectInputStream.close();
                LOGGER.info("ObjectOutputStream closed...");
            }

            if (socket != null) {
                socket.close();
                LOGGER.info("Socket closed...");
            }

            ApplicationServer.setCurrentThreadCount(ApplicationServer.getCurrentThreadCount()-1);
            LOGGER.info("Decreasing current thread number by one...");
            LOGGER.info("Current thread number: "+ApplicationServer.getCurrentThreadCount());
                
            } catch (Exception ex) {
                LOGGER.warning("Error connecting to the server...");
        }
            
        }
    
    public Message interpreteMessage(Message message) {
        Message retMessage = message;
        try {
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
        } catch (LoginNotFoundException e3) {
            LOGGER.warning("Login not found");
            retMessage.setType("LoginError");
        } catch (WrongPasswordException ex) {
            LOGGER.warning("Password not found");
            retMessage.setType("PasswordError");
        } catch (LoginAlreadyTakenException ex) {
            LOGGER.warning("Login already exists");
            retMessage.setType("LoginTaken");
        } catch (ServerConnectionErrorException ex) {
            LOGGER.warning("Error connecting to the server");
            retMessage.setType("ServerError");
        } catch (Exception ex) {
            LOGGER.warning(type);
        }
        return retMessage;
        
          
    }
    
}