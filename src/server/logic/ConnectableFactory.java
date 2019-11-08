/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import server.model.DAO;
import utilities.interfaces.Connectable;


/**
 * This class is a Factory for Connectable interface.
 * @author adria
 */
public class ConnectableFactory {

    /**
     * This method returns a DAO implementation.
     * @return An object implementing DAO.
     */
    public static synchronized Connectable getDAO() {
        return new DAO();
    }
}
