/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import utilities.beans.User;
import utilities.exception.LogicException;
import utilities.exception.LoginAlreadyTakenException;
import utilities.exception.LoginNotFoundException;
import utilities.exception.WrongPasswordException;
import utilities.interfaces.Connectable;

/**
 *
 * @author adria
 */
public class DAO implements Connectable {

    private String message;
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public User logIn(User user) throws LoginNotFoundException, WrongPasswordException, LogicException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User signUp(User user) throws LoginAlreadyTakenException, LogicException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void logOut(User user) throws LogicException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
