/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import utilities.beans.User;



/**
 *
 * @author Diego Urraca
 */
public class DAO {
    private Connection con = null;
    private PreparedStatement stmt;
    private String message;
    
    /**
     * Connect with the Database
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
     * Disconnect with the database
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
     * Method for User/Password comprobation and log in the application
     * @param user
     * @return user with the result
     */
    public User logIn(User user){
        User usr = null;
        try{
            this.connect();
            String sql="Select * from user where id=?";
            stmt=con.prepareStatement(sql);
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                usr.setId(rs.getInt(1));
                usr.setLogin(rs.getString(2));
                usr.setEmail(rs.getString(3));
                usr.setFullName(rs.getString(4));
                usr.setStatus(rs.getInt(5));
                usr.setPrivilege(rs.getInt(6));
                usr.setPassword(rs.getString(7));
                usr.setLastAccess(rs.getTimestamp(8));
                usr.setLastPasswordChange(rs.getTimestamp(9));
            }
            rs.close();
            //Result data comprobation to generate needed messages
            if(usr == null)//Cannot found the user
                this.message = "loginnotfound";
            else if(user.getPassword()!=usr.getPassword())//Invalid password
                this.message = "loginbadpass";
            else{//All OK
                user=usr;
                //We don't need the password in the client, so erase it before
                //send the user data is more secure
                user.setPassword(null);
                //Update of the last log in
                LocalDate ldate =LocalDate.now();
                Timestamp date=Timestamp.valueOf(ldate.atTime(LocalTime.now()));
                String sqlFecha="Update user set lastAccess=?";
                stmt=con.prepareStatement(sqlFecha);
                stmt.setTimestamp(1, date);
                stmt.executeUpdate(sqlFecha);
                this.message="loginok";
            }
        }catch(SQLException e){
            this.message = "dberror";
        }finally{
            this.disconnect();
            return user;
        }
    }
    
    /**
     * Method to register a new user
     * @param user
     * @return user
     */
    public User signUp(User user){
        try{
            this.connect();
            String sql = "insert into user(login,email,fullName,status,"
                    + "privilege,password,lastPasswordChange) "
                    + "values(?,?,?,?,?,?,?,?,?)";
            stmt=con.prepareStatement(sql);
            
        }catch(Exception e){
            //TODO exception
        }finally{
            
        }
        return user;
    }
    
    /**
     * Method to SignOut the application
     * @param user 
     */
    public void signOut(User user){
        
    }
    
}
