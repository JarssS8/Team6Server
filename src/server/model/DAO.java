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
 * This class interacts with the database and executes SQL querys to manipulate
 * data, then returns it to the ServerWorkerThread.
 * @author Diego Urraca
 */
public class DAO implements Connectable{
    //Connect with the Database
    private Connection con = null;
    private PreparedStatement stmt;
    private static final Logger LOGGER = Logger.getLogger("server.model.DAO");
 
    /**
     * Method for User/Password comprobation and log in the application.
     * @param user A User object that contains a User.
     * @return user with the result.
     * @throws LoginNotFoundException If login does not exist in the database.
     * @throws WrongPasswordException If password does not match with the user.
     * @throws ServerConnectionErrorException If there's an error in the server.
     */
    @Override
    public User logIn(User user) throws LoginNotFoundException, WrongPasswordException, ServerConnectionErrorException {
        User auxUser = new User();
        Boolean loginExists=false;
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
                loginExists=true;
            }
            rs.close();
            //Result data comprobation to generate needed messages
            if(!loginExists){//Cannot found the user
                LOGGER.severe("Login not found on database");
                throw new LoginNotFoundException();
            }
            else if(loginExists && !user.getPassword().equals(auxUser.getPassword())){//Invalid password
                LOGGER.severe("Wrong password to login");
                throw new WrongPasswordException();
            }
            else{//All OK
                LOGGER.info("User and password match");
                user=auxUser;
                //Update of the last log in
                String sqlFecha="Update user set lastAccess=? where login=?";
                stmt=con.prepareStatement(sqlFecha);
                stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(2, user.getLogin());
                stmt.executeUpdate();
                stmt.close();
            }
        }catch(SQLException e){
            LOGGER.severe("Error connecting with database"+e.getMessage());
            throw new ServerConnectionErrorException();
        }finally{
            PoolDB.returnConnection(con);
        }
        return user;
    }
    
    /**
     * Method to register a new user
     * @param user A User object that contains a User.
     * @return user with the result.
     * @throws LoginAlreadyTakenException If the login already exists in the
     * database.
     * @throws ServerConnectionErrorException If there's an error in the server.
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
            }
            else
                throw new LoginAlreadyTakenException();
        }catch(SQLException e){
            LOGGER.severe("Error connecting with database"+e.getMessage());
            throw new ServerConnectionErrorException();
        }finally{
            PoolDB.returnConnection(con);
        }
        return user;
    }
    
    /**
     * Method to SignOut the application
     * @param user A User object that contains a User. 
     * @throws ServerConnectionErrorException If there's an error in the server.
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
            throw new ServerConnectionErrorException();
        }
        finally{
            PoolDB.returnConnection(con);
        }

    }
    
}
