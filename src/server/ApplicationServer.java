/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.ServerSocket;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import server.logic.ServerWorkerThread;
import utilities.exception.ServerSocketConnectionException;

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
 
    public static void main(String[] args) throws ServerSocketConnectionException {
        threadsListener();
    }
    
    /**
     * This method listen for new client connection and count how many active threads
     * the server got and decide if it can create a new thread
     * @throws ConnectionException A specific exception class that control if there are some problem with the server socket communication
     */
    public static void threadsListener() throws ServerSocketConnectionException {

        //LOGGER.info("Open the properties file the maximum number of threads");
        LOGGER.info("Reading properties file to get the max thread number");
        int maxThreads = Integer.parseInt(ResourceBundle.getBundle("server.PropertiesServer").getString("maxThreads"));
        //LOGGER.info("Max threads get it.\nTry to use server socket for listen the petitions");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            LOGGER.info("Waiting for users to request connection");
            while (true) {
                if (getCurrentThreadCount() < maxThreads) {
                    //This maybe could go in the bottom of the if
                    setCurrentThreadCount(getCurrentThreadCount() + 1);
                    LOGGER.info("New Thread added. Number: "+getCurrentThreadCount());
                    ServerWorkerThread thread = new ServerWorkerThread(serverSocket.accept()); //new ServerWorkerThread(PORT,5);
                    thread.start();
                } else {
                    LOGGER.info("Exceded max number of threads");
                }
                
            }

        } catch (Exception ex) {
            throw new ServerSocketConnectionException("Connection error in server socket\n" + ex.getMessage());

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
