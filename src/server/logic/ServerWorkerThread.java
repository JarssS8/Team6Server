/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.logic;

import java.net.Socket;

/**
 *
 * @author Adrian
 */
public class ServerWorkerThread extends Thread{
    private static Socket sockett=null;
    public ServerWorkerThread(Socket socket){
        sockett=socket;
    }
    
    public void reader(Socket socket) {
       
    }
    
}
