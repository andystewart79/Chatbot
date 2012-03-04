
/* 
 * FILE: IdentServer.java 
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is Relay IRC chat client.
 * 
 * The Initial Developer of the Original Code is David M. Johnson.
 * Portions created by David M. Johnson are Copyright (C) 1998. 
 * All Rights Reserved.
 *
 * Contributor(s): No contributors to this file.''
 */

package org.relayirc.chatengine;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/** 
 * Implments a "one-shot" ident authentication server. This is needed
 * for systems that do not have an ident authentication server. 
 * @author David M. Johnson.
 */
public class IdentServer implements Runnable {
   String _userName = null;

   /** 
    * Construct identity server for specified chat server with specified
    * chat options.
    */
   public IdentServer( IChatEngine ctl, String userName) {
      _userName = userName;
      Thread t = new Thread(this);
      t.start();
   }
   //------------------------------------------------------------------
   /**
    * Start running and stop once one identity request has been serviced.
    */
   public void run() {

      ServerSocket echoServer = null;        
      String line;
      BufferedReader is;
      DataOutputStream os;
      Socket clientSocket = null;           

      try {
         echoServer = new ServerSocket(113);        
      }
      catch (IOException e) {           
      }   

      try {
         clientSocket = echoServer.accept();
         is = new BufferedReader( 
                 new InputStreamReader( 
                    new DataInputStream( clientSocket.getInputStream() )));
         os = new DataOutputStream(clientSocket.getOutputStream());
         while (true) {             
            line = is.readLine();
            if (line != null) {
               String resp = line+" : USERID : UNIX : "+_userName;
               System.out.println("IdentServer: "+resp);
               os.writeBytes(resp); 
               clientSocket.close();
               echoServer.close();
               return;
            }
         }  
      }
      catch (Exception e) {
			e.printStackTrace();	
			System.out.println("");
		   System.out.println("WARNING: Unable to start Relay IRC's built-in Ident Server.");
			System.out.println(" ");
		   System.out.println("   If you are running Linux, then ignore this message but be");
		   System.out.println("   aware that most chat servers require that running an identity");
		   System.out.println("   such as in.identd");
			System.out.println(" ");
		   System.out.println("   If you are running Windows, then this probably indicates");
		   System.out.println("   that you are not connected to the internet.");
			System.out.println(" ");
      }
   }    
}   


