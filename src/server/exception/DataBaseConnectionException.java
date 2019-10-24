/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.exception;

/**
 *
 * @author Adrian
 */
public class DataBaseConnectionException extends Exception {

    /**
     * Creates a new instance of <code>DataBaseConnectionException</code>
     * without detail message.
     */
    public DataBaseConnectionException() {
    }

    /**
     * Constructs an instance of <code>DataBaseConnectionException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DataBaseConnectionException(String msg) {
        super(msg);
    }
}
