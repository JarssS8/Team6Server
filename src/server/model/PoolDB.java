/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import utilities.exception.DBException;

/**
 *
 * @author Adrian
 */
public class PoolDB {
    /**
     * Declaration of logger for use it on different methods of the class
     */
    private static final Logger LOGGER = Logger.getLogger("server.testing.PoolDB");
   

    /**
     * Declaration of BasicDataSource that is an implementation of javax.sql.DataSource
     */
    private static BasicDataSource basicDataSource = null;

    /**
     * Assign to a final variable the url of or DataBase
     */
    private static final String URL = ResourceBundle.getBundle("server.testing.PropertiesServer")
            .getString("urlDataBase");

    /**
     * Assign to a final variable the driver of or DataBase
     */
    private static final String DRIVER = ResourceBundle.getBundle("server.testing.PropertiesServer")
            .getString("driverClassName");

    /**
     * Assign to a final variable the user of or DataBase
     */
    private static final String USER = ResourceBundle.getBundle("server.testing.PropertiesServer")
            .getString("userDataBase");

    /**
     * Assign to a final variable the password of or DataBase 
     */
    private static final String PASSWORD = ResourceBundle.getBundle("server.testing.PropertiesServer")
            .getString("passwordDataBase");

    /**
     * Assign to a final variable the maximum threads  possible
     */
    private static final int MAX_THREADS = Integer.parseInt(ResourceBundle.getBundle("server.testing.PropertiesServer")
            .getString("maxThreads"));

    /**
     * This method assign one connection to everyone who call him, while he have
     * possible connections to assign
     */
    //Necesito dos arraylist y cuando le asigno una conexion a un cliente se me 
    //va del array de posibles conexiones a conexiones en uso
    public static DataSource getDataSource() {

        LOGGER.info("Getting DataSource of the connection");
        if (basicDataSource == null) {
            
            LOGGER.info("DataSource don't exist.\nCreating new DataSource");
            basicDataSource = new BasicDataSource();
            //Parameters of the connection
            basicDataSource.setDriverClassName(DRIVER);
            basicDataSource.setUsername(USER);
            basicDataSource.setPassword(PASSWORD);
            basicDataSource.setUrl(URL);
            //If i dont use the connection pool in 30 mins automatically removes
            basicDataSource.setRemoveAbandonedTimeout(60);

            //Initial size of the pool is our max thread 
            basicDataSource.setInitialSize(MAX_THREADS);
            //The user got 8 seconds that he can be waiting for access
            basicDataSource.setMaxWaitMillis(8000);
            //Abandoned pool connections can be remove
            basicDataSource.setRemoveAbandonedOnBorrow(true);
            //Set the min inactive connections that can be on my pool
            basicDataSource.setMinIdle(MAX_THREADS);

        }
        LOGGER.info("Return the DataBase");
        return basicDataSource;
    }

    /**
     * This method remove the unused connections and add them again to the
     * connection ArrayList
     *
     * @return A connection for one thread
     * @throws server.exception.DataBaseConnectionException Because can't
     * connect correctly with the DataBase
     */
    public synchronized static Connection getConnection() throws DBException {

        LOGGER.info("Entre in getConnection method");
        Connection con = null;
        try {
           con = getDataSource().getConnection();
           LOGGER.info("Assign a new connection");
        } catch (SQLException e) {
            throw new DBException("Can't get the connection with the DataBase " + e.getMessage());
        }
        LOGGER.info("Return");
         return con;
    }

    /**
     * This method get the connection of one client and returns to the pool
     * @throws DataBaseConnectionException Because can't
     * connect correctly with the DataBase
     */
    public synchronized static void returnConnection(Connection onUseCon) throws DBException {
        if (onUseCon != null) {//If connection is done
            try {
                onUseCon.close();
                onUseCon = null;

            } catch (SQLException e) {
                throw new DBException("Can't get the connection with the DataBase " + e.getMessage());
            }
        }
    }
}
