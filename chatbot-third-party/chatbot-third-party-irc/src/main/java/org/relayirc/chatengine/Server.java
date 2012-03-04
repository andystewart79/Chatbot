
/* 
 * FILE: Server.java 
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.Serializable;

/**
 * Currently, a server object just holds information about an IRC server.
 * It might make sense to have an openConnection() method which returns
 * an IRC connection object.
 * @author David M. Johnson.
 */
public class Server implements Serializable {
   static final long serialVersionUID = -6620548660161628041L;

   private String  _name =  "";       // Server host name
   private int     _ports[] = null;   // Currently, only the 1st one is used
   private String  _title =  "";      // Optional
   private String  _network = "";     // Optional
   private String  _group = "";       // Optional
   private boolean _favorite = false;   

   private transient PropertyChangeSupport _propChangeSupport = null;

   //---------------------------------------------------------------
   public Server(String name, int port) {
      this(name,port,"");
   }  
   //---------------------------------------------------------------
   public Server(String name, int port, String group) {
      this(name,port,"","",group);
   }
   //---------------------------------------------------------------
   public Server(String name, int port, String title, String network, String group) {
      _propChangeSupport = new PropertyChangeSupport(this);
      _title = title;
      _network = network;
      _name = name;
      _group = group;
      setPort(port);
   }
   //-------------------------------------------------------------------
   private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {
      
      try {in.defaultReadObject();} catch (NotActiveException e) {e.printStackTrace();}
      _propChangeSupport = new PropertyChangeSupport(this);
   }
   //------------------------------------------------------------------
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      _propChangeSupport.addPropertyChangeListener(listener);
   }
   //------------------------------------------------------------------
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      _propChangeSupport.removePropertyChangeListener(listener);
   }
   //---------------------------------------------------------------
   public boolean isFavorite() {
      return _favorite;
   }
   public void setFavorite(boolean fave) {
      boolean old = _favorite;
      _favorite=fave;
      _propChangeSupport.firePropertyChange("Favorite",new Boolean(old),new Boolean(_favorite));
   }
   //---------------------------------------------------------------
   public String getGroup() {
      return _group;
   }
   public void setGroup(String group) {
      String old = _group;
      _group = group;
      _propChangeSupport.firePropertyChange("Group",old,_group);
   }
   //---------------------------------------------------------------
   public String getName() {
      return _name;
   }
   public void setName(String name) {
      _name = name;
   }
   //---------------------------------------------------------------
   public String getNetwork() {
      return _network;
   }
   public void setNetwork(String network) {
      String old = _network;
      _network = network;
      _propChangeSupport.firePropertyChange("Network",old,_network);
   }
   //---------------------------------------------------------------
   public int getPort() {
      if (_ports==null) {
         _ports = new int[1];
         _ports[0] = 0;
      }
      return _ports[0];
   }
   public void setPort(int port) {
      int old = 0;
      if (_ports==null) {
         _ports = new int[1];
      }
      else {
         old = _ports[0];
      }
      _ports[0] = port;
      _propChangeSupport.firePropertyChange("Port",new Integer(old),new Integer(_ports[0]));
   }
   //---------------------------------------------------------------
   // FIX: add property change support
   public int[] getPorts() { 
      return _ports;
   }
   // FIX: add property change support
   public void setPorts(int ports[]) {
      _ports = ports;
   }
   //---------------------------------------------------------------
   public String getTitle() {
      return _title;
   }
   public void setTitle(String title) {
      String old = _title;
      _title = title;
      _propChangeSupport.firePropertyChange("Title",old,_title);
   }
   //---------------------------------------------------------------
   public String toString() {
      if (_network!=null && _network.length()>0)
         return _name+" ("+_network+")";
      else
         return _name;
   }
}

