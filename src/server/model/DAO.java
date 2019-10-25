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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Logger;
import utilities.beans.User;
import utilities.exception.*;
import utilities.interfaces.Connectable;



/**
 *
 * @author Diego Urraca
 */

public class DAO implements Connectable {
    //Connect with the Database
    private Connection con = null;
    private PreparedStatement stmt;
    private String message;
    private static final Logger LOGGER = Logger.getLogger("server.model.DAO");
    
    //ClASS METHODS
    
    /**
     * This method returns a message String
     * @return message A string that contains a message
     */
    @Override
    public String getMessage(){
        //Method that return the final result of the methods of DAO
        return this.message;
    }
    
   //DB METHODS
    
    /**
     * Method for User/Password comprobation and log in the application
     * @param user
     * @return user with the result
     * @throws utilities.exception.DBException
     */
    
    @Override
    public User logIn(User user) throws DBException{
        User usr = null;
        try{
            
            String sql="Select * from user where id=?";
            
            con = PoolDB.getConnection();
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
            if(usr == null){//Cannot found the user
                this.message = "loginnotfound";
                LOGGER.severe("Login not found on database");
                throw new LoginNotFoundException();
            }
            else if(user.getPassword()!=usr.getPassword()){//Invalid password
                this.message = "loginbadpass";
                LOGGER.severe("Wrong password to login");
                throw new WrongPasswordException();
            }
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
                stmt.close();
                this.message="loginok";
            }
        }catch(SQLException e){
            this.message = "dberror";
            LOGGER.severe("Error connecting with database");
            throw new DBException();
        }finally{
            PoolDB.returnConnection(con);
            return user;
        }
    }
    
    /**
     * Method to register a new user
     * @param user
     * @return user
     * @throws utilities.exception.DBException
     */
    
    @Override
    public User signUp(User user) throws DBException{
        String logaux=null;
        try{
            
            String sqlExist = "Select login from user where login=?";
            
            con = PoolDB.getConnection();
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
                stmt.close();
                this.message = "signupok";
            }
            else
                this.message = "signupexist";
        }catch(SQLException e){
            this.message = "dberror";
            LOGGER.severe("Error connecting with database");
            throw new DBException();
        }finally{
            PoolDB.returnConnection(con);
            return user;
        }
    }
    
    /**
     * Method to SignOut the application
     * @param user 
     * @throws utilities.exception.DBException 
     */
    
    @Override
    public void logOut(User user) throws DBException{
        try {
            
            String sql="update user set lastAccess=? where login=?";
            
            con = PoolDB.getConnection();
            stmt=con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, user.getLogin());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            LOGGER.severe("Error connecting with database");
            throw new DBException();
        }
        finally{
            PoolDB.returnConnection(con);
        }

    }
    
}
