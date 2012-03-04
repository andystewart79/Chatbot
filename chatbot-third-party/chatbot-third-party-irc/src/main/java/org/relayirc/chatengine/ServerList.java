/* 
 * FILE: ServerList.java 
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
 * Contributor(s): No contributors to this file.  
 */

package org.relayirc.chatengine;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.relayirc.util.RCTest;


/** 
 * A list of IRC servers that can be constructed by reading a 
 * MIRC servers.ini file. TODO: Finish exportMircFile()
 */
public class ServerList implements Serializable {
   static final long serialVersionUID = -5947643727921290160L;

   private Vector _servers = new Vector();

   public int    getServerCount()       {return _servers.size();}
   public Server getServerAt(int index) {return (Server)_servers.elementAt(index);}
   public Vector getServers()           {return _servers;}
    
   private transient PropertyChangeSupport   _propChangeSupport = null;
   private transient PropertyChangeListener  _serverListener = null;

   //-------------------------------------------------------------------
   /** 
    * Construct empty server list.
    */
   public ServerList() {
      init();
   }
   //-------------------------------------------------------------------
   /** 
    * Construct server list from vector of servers.
    * @param servers Vector of servers.
    */
   public ServerList(Vector servers) {
      this();
      _servers = servers;
   }
   //-------------------------------------------------------------------
   /** 
    * Construct server list from MIRC servers.ini file.
    * @param fileName Name of file to read.
    */
   public ServerList(File file) {
      this();
      importMircFile(file);
   }
   //-------------------------------------------------------------------
   private void init() {
      _propChangeSupport = new PropertyChangeSupport(this);
      _serverListener = new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt) {
            _propChangeSupport.firePropertyChange("Server",null,null);
         }
      };
      for (int i=0; i<_servers.size(); i++) {
         Server svr = (Server)_servers.elementAt(i);
         svr.addPropertyChangeListener(_serverListener);
      }
   }
   //-------------------------------------------------------------------
   private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {
      try {
         in.defaultReadObject();
         init();
      } catch (NotActiveException e) {
          e.printStackTrace();
      }
   }
 
   //------------------------------------------------------------------
	/** Property change support. */
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      RCTest.println("ServerList.addPropertyChangeListener("+listener.toString()+")");
      _propChangeSupport.addPropertyChangeListener(listener);
   }
   //------------------------------------------------------------------
	/** Property change support. */
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      //RCTest.println("ServerList.removePropertyChangeListener("+listener+")");
      _propChangeSupport.removePropertyChangeListener(listener);
   }
   //------------------------------------------------------------------
   /** Add server to user's list of IRC servers. */
   public void addServer(Server svr) {
      RCTest.println("ServerList.addServer(svr="+svr+")");
      svr.addPropertyChangeListener(_serverListener);
      _servers.insertElementAt(svr,0);
      _propChangeSupport.firePropertyChange("Server",null,null);
   }
   //------------------------------------------------------------------
   /** Remove server from user's list of IRC servers. */
   public void removeServer(Server svr) {
      _servers.removeElement(svr);
      svr.removePropertyChangeListener(_serverListener);
      _propChangeSupport.firePropertyChange("Server",null,null);
   }
   //-------------------------------------------------------------------
	/** Import a MIRC format servers file. */
   public void importMircFile(File file) {

      // A typical line from a MIRC servers.ini files looks like this:
      // n12=Austnet: CanadaSERVER:ca.austnet.org:6667,6668GROUP:austnet

	   try {
	      RandomAccessFile servers = new RandomAccessFile(file,"r");
         String line = null;
         while ((line=servers.readLine()) != null) {

            if (line.startsWith("[servers]")) continue;

            try {

               int equalTag   = line.indexOf("=");
               int serverTag  = line.indexOf("SERVER:");
               int groupTag   = line.indexOf("GROUP:");

               String desc    = line.substring(equalTag+1,serverTag);
               String address = line.substring(serverTag+7,groupTag);
               String group   = line.substring(groupTag+6).trim();

               // Parse network and title
               String network = "";
               String title = "";
               int descColon = desc.indexOf(":");
               if (descColon != -1) {
                  network = desc.substring(0,descColon);
                  title = desc.substring(descColon+1);
               }
               else {
                  title = desc;
               }

               // Parse server host name
               String server = "";
               int port = 0;
               int addressColon = address.indexOf(":");
               server = address.substring(0,addressColon);

               // Parse port numbers
               String portStr = address.substring(addressColon+1);
               int portsArray[] = ServerList.stringToIntArray(portStr);

               Server svr = new Server(server,0,title,network,group);
               svr.setPorts(portsArray);
               _servers.addElement(svr);

               //RCTest.println("Server: "+svr);
            }
            catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
      catch (Exception e) {
         e.printStackTrace();
         return;
      }
      RCTest.println("ServerList: read "+_servers.size()+" servers");
   }

   //-------------------------------------------------------------------
	/** Convert integer array to a string. */
   public static String intArrayToString(int[] intArray) {
      String ret = new String();
      for (int i=0; i<intArray.length; i++) {
         if (ret.length()>0) 
            ret = ret+","+Integer.toString(intArray[i]);
         else   
            ret = Integer.toString(intArray[i]);
      }
      return ret;
   }

   //-------------------------------------------------------------------
	/** Convert string to an integer array. */
   public static int[] stringToIntArray(String intList) {

      Vector intVector = new Vector();
      StringTokenizer toker = new StringTokenizer(intList,",");
      
      while (toker.hasMoreTokens()) {
         try {
            int newPort = Integer.parseInt((String)toker.nextToken());
            intVector.addElement(new Integer(newPort));
         } catch (Exception e) {e.printStackTrace();}
      }

      int intArray[] = new int[intVector.size()];
      for (int i=0; i<intVector.size(); i++) {
         intArray[i] = ((Integer)intVector.elementAt(i)).intValue();
      }

      return intArray;
   }

   //-------------------------------------------------------------------
	/** Export server list file to MIRC format - NOT IMPLEMENTED. */
   public void exportMircFile(String fileName) {
   }
}
