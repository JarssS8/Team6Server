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
 * This class reads objects from the socket, interpretate them and builds an
 * answer that will be sent via socket to the client.
 *
 * @author aimar
 */
public class ServerWorkerThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger("server.logic.ServerWorkerThread");
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;
    private User user = null;
    private Message messageIn = null;
    private Message messageOut = null;
    private String type;
    private Connectable dao = ConnectableFactory.getDAO();
    private Socket socket;

    public ServerWorkerThread(Socket socket) {
        this.socket = socket;
    }

    /**
     * This method executes the thread.
     */
    @Override
    public void run() {
        try {

            readMessage();
            messageOut = interpreteMessage(messageIn);
            sendMessage();

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
        } catch (IOException ex) {
            LOGGER.warning("ServerWorkerThread: Error connecting to the server..." + ex.getMessage());
        } finally {
            ApplicationServer.setCurrentThreadCount(ApplicationServer.getCurrentThreadCount() - 1);
            LOGGER.info("Decreasing current thread number by one...");
            LOGGER.info("Current thread number: " + ApplicationServer.getCurrentThreadCount());
        }

    }

    /**
     * This method reads a Message object from the socket and divides it in a
     * user object and a type string.
     */
    public void readMessage() {
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            LOGGER.info("Starting to read the message...");
            messageIn = (Message) objectInputStream.readObject();
            user = messageIn.getUser();
            type = messageIn.getType();
            LOGGER.info("User requests: " + type);
        } catch (IOException ex) {
            LOGGER.warning("ServerWorkerThread: IO exception" + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            LOGGER.warning("ServerWorkerThread: Class not found exception: " + ex.getMessage());
        }
    }

    /**
     * This method decides what to do depending on the type received in the
     * Message. Changes the type string depending on what exception is throwed.
     *
     * @param message A Message that contains a user and a type received from
     * the socket.
     * @return retMessage A Message object
     */
    public Message interpreteMessage(Message message) {
        LOGGER.info("Starting to decide...");
        Message retMessage = new Message();
        try {
            switch (message.getType()) {
                case Message.LOGIN_MESSAGE: {
                    retMessage.setUser(dao.logIn(message.getUser()));
                    LOGGER.info("Initiating login...");
                    break;
                }
                case Message.SIGNUP_MESSAGE: {
                    retMessage.setUser(dao.signUp(user));
                    LOGGER.info("Initiating sign up...");
                    break;
                }
                case Message.LOGOUT_MESSAGE: {
                    dao.logOut(user);
                    LOGGER.info("Initiating log out...");
                    break;
                }
            }

            if (retMessage.getUser() != null) { //All OK
                retMessage.setType("OK");
            }
            //The exception change the message type we send to the client
        } catch (LoginNotFoundException ex) {
            LOGGER.warning("ServerWorkerThread: Login not found: " + ex.getMessage());
            retMessage.setType("LoginError");
        } catch (WrongPasswordException ex) {
            LOGGER.warning("ServerWorkerThread: Password not found: " + ex.getMessage());
            retMessage.setType("PasswordError");
        } catch (LoginAlreadyTakenException ex) {
            LOGGER.warning("ServerWorkerThread: Login already exists: " + ex.getMessage());
            retMessage.setType("LoginTaken");
        } catch (ServerConnectionErrorException ex) {
            LOGGER.warning("ServerWorkerThread: Error connecting to the server: " + ex.getMessage());
            retMessage.setType("ServerError");
        }
        return retMessage;
    }

    /**
     * This method sends the message to the client via socket.
     */
    public void sendMessage() {
        try {
            LOGGER.info("Message loaded to return: ");
            objectOutputStream.writeObject(messageOut);
            LOGGER.info("Message sent...");
        } catch (IOException ex) {
            LOGGER.warning("ServerWorkerThread: Error while sending message to client: " + ex.getMessage());

        }
    }
}
