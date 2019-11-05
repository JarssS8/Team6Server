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
import java.time.LocalDateTime;
import java.util.logging.Logger;
import utilities.beans.User;
import utilities.exception.*;
import utilities.interfaces.Connectable;



/**
 *
 * @author Diego Urraca
 */

public class DAO implements Connectable{
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
    
   //DB METHODS
    
    /**
     * Method for User/Password comprobation and log in the application
     * @param user
     * @return user with the result
     * @throws utilities.exception.LoginNotFoundException
     * @throws utilities.exception.WrongPasswordException
     * @throws utilities.exception.ServerConnectionErrorException
     */
    
   
    @Override
    public User logIn(User user) throws LoginNotFoundException, WrongPasswordException, ServerConnectionErrorException {
        User auxUser = new User();
        LOGGER.info("Enter in login method");
        try{
            
            String sql="Select * from user where login=?";
            
            con = PoolDB.getConnection();
            stmt=con.prepareStatement(sql);
            stmt.setString(1, user.getLogin());
            LOGGER.info("Execute query");
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()){
                LOGGER.info("Assign values to the user");
                auxUser.setId(rs.getInt(1));
                auxUser.setLogin(rs.getString(2));
                auxUser.setEmail(rs.getString(3));
                auxUser.setFullName(rs.getString(4));
                auxUser.setStatus(rs.getInt(5));
                auxUser.setPrivilege(rs.getInt(6));
                auxUser.setPassword(rs.getString(7));
                auxUser.setLastAccess(rs.getTimestamp(8));
                auxUser.setLastPasswordChange(rs.getTimestamp(9));
            }
            rs.close();
            //Result data comprobation to generate needed messages
            if(auxUser == null){//Cannot found the user
                this.message = "LoginError";
                LOGGER.severe("Login not found on database");
                throw new LoginNotFoundException();
            }
            else if(!user.getPassword().equals(auxUser.getPassword())){//Invalid password
                this.message = "PasswordError";
                LOGGER.severe("Wrong password to login");
                throw new WrongPasswordException();
            }
            else{//All OK
                LOGGER.info("User and password match");
                user=auxUser;
                //Update of the last log in
                String sqlFecha="Update user set lastAccess=?";
                stmt=con.prepareStatement(sqlFecha);
                stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                stmt.executeUpdate();
                stmt.close();
            }
        }catch(SQLException e){
            this.message = "ServerError";
            LOGGER.severe("Error connecting with database"+e.getMessage());
            throw new ServerConnectionErrorException();
        }finally{
            PoolDB.returnConnection(con);
        }
        return user;
    }
    
    /**
     * Method to register a new user
     * @param user
     * @return user
     * @throws utilities.exception.LoginAlreadyTakenException
     * @throws utilities.exception.ServerConnectionErrorException
     */
    
    
    @Override
    public User signUp(User user) throws LoginAlreadyTakenException, ServerConnectionErrorException{
        String logaux=null;
        try{
            
            String sqlExist = "Select login from user where login=?";
            
            con = PoolDB.getConnection();
            stmt=con.prepareStatement(sqlExist);
            stmt.setString(1, user.getLogin());
             ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                logaux=rs.getString(1);
            }
            rs.close();
            stmt.close();
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
                stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                stmt.executeUpdate();
                stmt.close();
                this.message = "signupok";
            }
            else
                this.message = "signupexist";
        }catch(SQLException e){
            this.message = "ServerError";
            LOGGER.severe("Error connecting with database"+e.getMessage());
            throw new ServerConnectionErrorException();
        }finally{
            PoolDB.returnConnection(con);
        }
        return user;
    }
    
    /**
     * Method to SignOut the application
     * @param user 
     *  
     */
    
    
    @Override
    public void logOut(User user) throws ServerConnectionErrorException{
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
        }
        finally{
            PoolDB.returnConnection(con);
        }

    }
    
}
