/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.exception.ConnectionException;
import server.logic.ServerWorkerThread;

/**
 *
 * @author Adrian
 */
public class ApplicationServer {

    /**
     * Declaration of logger for use it on different methods of the class
     */
    private static final Logger LOGGER = Logger.getLogger("server.ApplicationServer");

    /**
     * Declaration of the port for the connection
     */
    private static final int PORT = Integer.parseInt(ResourceBundle.getBundle("server.PropertiesServer").getString("connectionPort"));
    
    /**
     * Count of the threads that I'm using and is static because it could be
     * modify by the ServerWorkerThreadClass
     */
    private static int currentThreadCount = 0;
    
    /**
     * @param args the command line arguments
     */
 
    public static void main(String[] args) throws ConnectionException {
        threadsListener();
    }
    
    /**
     * This method listen for new client connection and count how many active threads
     * the server got and decide if it can create a new thread
     * @throws ConnectionException A specific exception class that control if there are some problem with the server socket communication
     */
    public static void threadsListener() throws ConnectionException {

        LOGGER.info("Open the properties file the maximum number of threads");
        int maxThreads = Integer.parseInt(ResourceBundle.getBundle("server.PropertiesServer").getString("maxThreads"));
        LOGGER.info("Max threads get it.\nTry to use server socket for listen the petitions");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            LOGGER.info("Waiting users that conects to the server");
            while (true) {
                if (getCurrentThreadCount() < maxThreads) {
                    //This maybe could go in the bottom of the if
                    setCurrentThreadCount(getCurrentThreadCount() + 1);
                    LOGGER.info("New Thread added. Number: "+getCurrentThreadCount());
                    ServerWorkerThread thread = new ServerWorkerThread();
                    thread.setSocket(serverSocket.accept());
                    thread.start();
                }
                LOGGER.info("Exceded max number of threads");
            }

        } catch (IOException ex) {
            throw new ConnectionException("Connection error in server socket\n" + ex.getMessage());

        }
    }

    /**
     * @return the currentThreadCount
     */
    public static synchronized int getCurrentThreadCount() {
        return currentThreadCount;
    }

    /**
     * @param aCurrentThreadCount the currentThreadCount to set
     */
    public static synchronized void setCurrentThreadCount(int aCurrentThreadCount) {
        currentThreadCount = aCurrentThreadCount;
    }

}
