/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import server.model.DAO;
import utilities.interfaces.Connectable;


/**
 *
 * @author adria
 */
public class ConnectableFactory {

    public static synchronized Connectable getDAO() {
        return new DAO();
    }
}
