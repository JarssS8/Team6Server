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
    //Connect with the Database
    private Connection con = PoolDB.getConnection();
    private PreparedStatement stmt;
    private String message;
    
    
    
    /**
     * Disconnect with the database
     */
    private void disconnect(){
        try{
            if(stmt != null)
                stmt.close();
        } catch(SQLException e){
            this.message = "dberror";
        } finally{
            PoolDB.returnConnection(con);
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
        String logaux=null;
        try{
            String sqlExist = "Select login from user where login=?";
            stmt=con.prepareStatement(sqlExist);
            stmt.setString(1, user.getLogin());
             ResultSet rs = stmt.executeQuery(sqlExist);
            while(rs.next()){
                logaux=rs.getString(1);
            }
            rs.close();
            if(logaux==null){
                String sql = "insert into user(login,email,fullName,status,"
                        + "privilege,password,lastPasswordChange) "
                        + "values(?,?,?,?,?,?,?)";
                stmt=con.prepareStatement(sql);
                stmt.setString(1, user.getLogin());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getFullName());
                stmt.setInt(4, 1);
                stmt.setInt(5, 1);
                stmt.setString(6, user.getPassword());
                LocalDate ldate =LocalDate.now();
                Timestamp date=Timestamp.valueOf(ldate.atTime(LocalTime.now()));
                stmt.setTimestamp(7, date);
            }
            else
                this.message = "signupexist";
        }catch(Exception e){
            this.message = "dberror";
        }finally{
            this.disconnect();
            return user;
        }
    }
    
    /**
     * Method to SignOut the application
     * @param user 
     */
    public void signOut(User user){
        
    }
    
}
