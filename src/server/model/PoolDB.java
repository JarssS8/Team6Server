/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Adrian
 */
public class PoolDB {
    // Declaration of the connection that we go to use 
    private static Connection con=null;
    
    // Creates a new arrayList for the connections who are being used
    public static ArrayList connectionUsed=new ArrayList();
    
    // Creates a new int to get how many connections could the pool got
    private static final int MAX_POOL_SIZE=Integer.parseInt(ResourceBundle.getBundle("server.PropertiesServer").getString("maxThreads"));
    
    // Creates a new arrayList for all the possible connections
    public static ArrayList connectionList=null;
    
    //Assign to a final variable the url of or DataBase
    private static final String URL=ResourceBundle.getBundle("server.PropertiesServer")
            .getString("urlDataBase");
    
    //Assign to a final variable the user of or DataBase
    private static final String USER=ResourceBundle.getBundle("server.PropertiesServer")
            .getString("userDataBase");
    
    //Assign to a final variable the password of or DataBase    
    private static final String PASSWORD=ResourceBundle.getBundle("server.PropertiesServer")
            .getString("passwordDataBase");
    
    /**
     * This method assign one connection to everyone who call him, while he have 
     * possible connections to assign
     */
    //Necesito dos arraylist y cuando le asigno una conexion a un cliente se me 
    //va del array de posibles conexiones a conexiones en uso
    public static void openConnection(){
        if(connectionList.isEmpty()){
            try {
                con=DriverManager.getConnection(URL, USER, PASSWORD);
              
            } catch (SQLException ex) {
                Logger.getLogger(PoolDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            
        }
    }
    
    /**
     * This method remove the unused connections and add them again to the connection ArrayList
     */
    public static void closeConnection(){
        
    }
}
