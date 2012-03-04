
/* 
 * FILE: ChatOptions.java 
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
import java.io.IOException;
import java.io.NotActiveException;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;


/**
 * User's option settings. Includes identification information
 * like nick name and user name. Includes name of currently 
 * select IRC server and a list of other IRC servers.
 * @author David M. Johnson.
 */
public class ChatOptions implements Serializable {
   static final long serialVersionUID = 3975837295602359868L;

   // User info
   private String    _nickName = new String();
   private String    _altNick = new String();
   private String    _userName = new String();
   private String    _fullName = new String();
   
   // Selected server
   private Server    _server = null;
   
   // App configuration
   private Hashtable _colors = new Hashtable();
   private Hashtable _customOptions = new Hashtable();
   private boolean   _statusBarEnabled = true;   
   private String    _fontName = new String();
   private int       _fontStyle = 0;
   private int       _fontSize = 0;
   
   // Favorites
   private Vector     _channels = new Vector();  
   private ServerList _servers = new ServerList();
   private Vector     _users = new Vector();

   private transient PropertyChangeSupport   _propChangeSupport = null; 
   private transient PropertyChangeListener  _serverListListener = null;
   private transient PropertyChangeListener  _channelListener = null;
   private transient PropertyChangeListener  _userListener = null;

   //------------------------------------------------------------------
   public ChatOptions() {
      init();
   }
   //-------------------------------------------------------------------
   private void init() {
      _propChangeSupport = new PropertyChangeSupport(this);

      // ServerList will listen to the Servers for us
      _serverListListener = new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt) {
            _propChangeSupport.firePropertyChange("Servers",null,null);
         }
      };
      _servers.addPropertyChangeListener(_serverListListener);

      // Each time a channel is added, we'll add this listener to it
      _channelListener = new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt) {
            _propChangeSupport.firePropertyChange("Channels",null,null);
         }
      };
      // Each time a user is added, we'll add this listener to it
      _userListener = new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt) {
            _propChangeSupport.firePropertyChange("Users",null,null);
         }
      };
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
   /** Initialize with some useful default values. */
   public void initNewOptions() {

      setCustomOption("LookAndFeel","Metal");
      setFontSize(12);

      try {
         _userName = System.getProperty("user.name");
      }
      catch (Exception e) {
         _userName = "username";
      }
      _nickName = _userName;
      _altNick = _userName+"1";
      _fullName = _userName;
 
      _colors.put("Messages","Black");
      _colors.put("Actions","Red");
      _colors.put("Joins","Blue");
      _colors.put("Parts","Blue");
      _colors.put("Ops","Magenta");
      _colors.put("Kicks","Orange");
      _colors.put("Bans","Orange");
      _colors.put("Nicks","DarkGray");

      addServer(new Server("irc.linux.com",6667,"","Linux.com","Linux.com"));
      addServer(new Server("irc.dal.net",7000,"","DALNet","DALNet"));
      addServer(new Server("irg.chat.org",6667,"","EFNet","EFNet"));
      addServer(new Server("us.undernet.org",6667,"","Undernet","Undernet"));

      for (int i=0; i<getServerCount(); i++) {
         getServerAt(i).setFavorite(true);
      }
   }
   //------------------------------------------------------------------
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      _propChangeSupport.addPropertyChangeListener(listener);
   }
   //------------------------------------------------------------------
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      _propChangeSupport.removePropertyChangeListener(listener);
   }
   //------------------------------------------------------------------
   /**
    * Get alternate user's nick name, to be used if primary nick
    * name is already in use by somebody else.
    */
   public String getAltNick() {
      return _altNick;
   }
   //------------------------------------------------------------------
   /**
    * Get alternate user's nick name, to be used if primary nick
    * name is already in use by somebody else.
    * @param str New alternate nick name.
    */
   public void setAltNick(String str) {
      String old = _altNick;
      _altNick = str;
      _propChangeSupport.firePropertyChange("AltNick",old,_altNick);
   }
   //------------------------------------------------------------------
   /** Add channel to list of IRC channels frequented by user. */
   public void addChannel(Channel chan) {
      chan.addPropertyChangeListener(_channelListener);
      _channels.removeElement(chan);
      _channels.insertElementAt(chan,0);
      _propChangeSupport.firePropertyChange("Channel",null,null);
   }
   //------------------------------------------------------------------
   /** Get number of channels. */
   public int getChannelCount() {
      return _channels.size();
   }
   //------------------------------------------------------------------
   /** Get channel by index. */
   public Channel getChannelAt(int index) {
      return (Channel)_channels.elementAt(index);
   }
   //------------------------------------------------------------------
   /** Remove channel. */
   public void removeChannel(Channel channel) {
      channel.removePropertyChangeListener(_channelListener);
      _channels.removeElement(channel);
      _propChangeSupport.firePropertyChange("Channel",null,null);
   }
   //------------------------------------------------------------------
   /** Set custom option, keyed by name. */
   public void setCustomOption(String key, Object value) {
      _customOptions.put(key,value);
      _propChangeSupport.firePropertyChange("CustomOption",null,null);
   }
   //------------------------------------------------------------------
   /** Get custom option, keyed by name. */
   public Object getCustomOption(String key) {
      return _customOptions.get(key);
   }
   //------------------------------------------------------------------
   /** 
    * Gets name of color to be used for display of specified type of
    * chat messages. Color names are used because different GUI toolkits 
    * (e.g. WFC and JFC) use different classes to represent color.
    * @param type Type of chat message.
    */
   public String getDisplayColor( String type ) {
      return (String)_colors.get(type);
   }
   //------------------------------------------------------------------
   /**
    * Sets name of color to be use for display of specified type of 
    * chat messages. Color names are used because different GUI toolkits 
    * (e.g. WFC and JFC) use different classes to represent color.
    * @param type Type of chat message.
    * @param col Name of color.
    */
   public void setDisplayColor( String type, String col ) {
      _colors.put(type,col);
      _propChangeSupport.firePropertyChange("DisplayColor",null,null);
   }
   //------------------------------------------------------------------
   /** Get user's nick name. */
   public String getNick() {
      return _nickName;
   }
   //------------------------------------------------------------------
   /**
    * Set user's nick name.
    * @param str New nick name.
    */
   public void setNick( String str ) {
      String old = _nickName;
      _nickName = str;
      _propChangeSupport.firePropertyChange("Nick",old,_nickName);
   }
   //------------------------------------------------------------------
   /** Get name of font to be used for message display. */
   public String getFontName() {
      return _fontName;
   }
   //------------------------------------------------------------------
   /** Set name of font to be used for message display. */
   public void setFontName( String str ) {
      String old = _fontName;
      _fontName = str;
      _propChangeSupport.firePropertyChange("FontName",old,_fontName);
   }   
   //------------------------------------------------------------------
   /** Get point size of font to be used for message display. */
   public int getFontSize() {
      return _fontSize;
   }
   //------------------------------------------------------------------
   /** Set point size of font to be used for message display. */
   public void setFontSize( int size ) {
      int old = _fontSize;
      _fontSize = size;
      _propChangeSupport.firePropertyChange(
         "FontSize",new Integer(old),new Integer(_fontSize));
   }
   //------------------------------------------------------------------
   /** Get style of font to be used for message display. */
   public int getFontStyle() {
      return _fontStyle;
   }
   //------------------------------------------------------------------
   /* Set style of font to be used for message display. */
   public void setFontStyle( int style ) {
      int old = _fontStyle;
      _fontStyle = style;
      _propChangeSupport.firePropertyChange(
         "FontStyle",new Integer(old),new Integer(_fontStyle));
   }
   //------------------------------------------------------------------
   /** Get full name of user. */
   public String getFullName() {
      return _fullName;
   }
   //------------------------------------------------------------------
   /** Set full name of user. */
   public void setFullName( String str ) {
      String old = _fullName;
      _fullName = str;
      _propChangeSupport.firePropertyChange("FullName",old,_fullName);
   }
   //------------------------------------------------------------------
   /** Get name of IRC chat server. */
   public Server getServer() {
      return _server;
   }
   //------------------------------------------------------------------
   /** Set current chat server. */
   public void setServer( Server svr ) {
      Server old = _server;
      _server = svr;
      _propChangeSupport.firePropertyChange("Server",old,_server);
   }
   //------------------------------------------------------------------
   /** Add server to user's list of IRC servers. */
   public void addServer(Server svr) {
      // Don't need to add a listener to server being added because ServerList
      // listens to each server and ChatOptions listens to ServerList.
      _servers.addServer(svr);
   }
   //------------------------------------------------------------------
   /** Remove server from user's list of IRC servers. */
   public void removeServer(Server svr) {
      _servers.removeServer(svr);
   }
   //------------------------------------------------------------------
   /** Get vector of servers */
   public Vector getServers() {
      return _servers.getServers();
   }
   //------------------------------------------------------------------
   /** Get vector of servers */
   public ServerList getServerList() {
      return _servers;
   }
   //------------------------------------------------------------------
   /** Get number of users. */
   public int getServerCount() {
      return _servers.getServerCount();
   }
   //------------------------------------------------------------------
   /** Get user by index. */
   public Server getServerAt(int index) {
      return _servers.getServerAt(index);
   }
   //------------------------------------------------------------------
   /** Enable/disable display of status bar. */
   public void setStatusBarEnabled(boolean flag) {
      boolean old = _statusBarEnabled;
      _statusBarEnabled = flag;
      _propChangeSupport.firePropertyChange(
         "StatusBarEnabled",new Boolean(old),new Boolean(_statusBarEnabled));
   }
   //------------------------------------------------------------------
   /** Returns true if status bar is to be shown. */
   public boolean isStatusBarEnabled() {
      return _statusBarEnabled;
   }
   //------------------------------------------------------------------
   /** Get user's full name. */
   public String getUserName() {
      return _userName;
   }
   //------------------------------------------------------------------
   /** 
    * Set users full name.
    * @param str New name.
    */
   public void setUserName( String str ) {
      String old = _userName;
      _userName = str;
      _propChangeSupport.firePropertyChange("UserName",old,_userName);
   }
   //------------------------------------------------------------------
   /** Add user to list of users. */
   public void addUser(User user) {
      // user.addPropertyChangeListener(_userListener);
      _users.removeElement(user);
      _users.insertElementAt(user,0);
      _propChangeSupport.firePropertyChange("User",null,null);
   }
   //------------------------------------------------------------------
   /** Get number of users. */
   public int getUserCount() {
      return _users.size();
   }
   //------------------------------------------------------------------
   /** Get user by index. */
   public User getUserAt(int index) {
      return (User)_users.elementAt(index);
   }
   //------------------------------------------------------------------
   /** Remove user from user's list of favorite users. */
   public void removeUser(User user) {
      // user.removePropertyChangeListener(_userListener);
      _users.removeElement(user);
      _propChangeSupport.firePropertyChange("User",null,null);
   }
}

