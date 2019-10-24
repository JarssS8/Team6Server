/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import utilities.beans.User;



/**
 *
 * @author adria
 */
public class DAO {
    private Connection con = null;
    private Statement stmt;
    private String message;
    
    /**
     * Connection with the Database
     */
    private void connect(){
        try {
            con = PoolDB.getConnection();
            stmt = con.createStatement();
        } catch (SQLException ex) {
            //TODO EXCEPTION
        }
    }
    
    /**
     * Disconnect whit the database
     */
    private void disconnect(){
        try{
            if(stmt != null)
                stmt.close();
            if(con != null)
                con.close();
        } catch(SQLException e){
            //TODO EXCEPTION
        } finally{
            PoolDB.liberarConexion();
        }
    }
    
    
    //ClASS METHODS
    
    public String getMessage(){
        //Method that return the final result of the methods of DAO
        return this.message;
    }
    
   //DB METHODS
    
    /**
     * Method to LogIn the application
     * @param user
     * @return user with the result
     */
    public User logIn(User user){
        int result = 0;
        this.connect();
        
        String exist=null;
        
        
        
        return user;
    }
    
    /**
     * Method to register a new user
     * @param user
     * @return user
     */
    public User signUp(User user){
        
        return user;
    }
    
    /**
     * Method to SignOut the application
     * @param user 
     */
    public void signOut(User user){
        
    }
    
}
