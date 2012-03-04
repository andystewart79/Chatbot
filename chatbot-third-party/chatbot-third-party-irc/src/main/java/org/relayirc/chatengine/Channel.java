
/* 
 * FILE: Channel.java 
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.Serializable;
import java.util.Vector;

///////////////////////////////////////////////////////////////////////////
/** 
 * An IRC channel class that includes methods for joining, parting,
 * kicking, banning, adding/removing channel listeners and property 
 * change support.
 * @author David M. Johnson
 */
public class Channel implements Serializable {

   private transient IChatEngine _engine = null;
   private transient Vector      _listeners = new Vector();
   private transient boolean     _isConnected = false;
	private String                _name = null;
	private String                _topic = new String();
	private int                   _userCount = 0;
   static final long             serialVersionUID = 2529627895164114595L;
   
	private PropertyChangeSupport _propChangeSupport = null;

	/** For internal chat engine use only. */
   private _ChannelMux _mux = new _ChannelMux();

   //------------------------------------------------------------------
   /** Construct channel with name alone. */ 
   public Channel(String name) {
      _name = name;
      _propChangeSupport = new PropertyChangeSupport(this);
   }
   //------------------------------------------------------------------
   /** Construct channel with chat engine. */
   public Channel(String name, IChatEngine engine) {
      this(name);
      _engine = engine;
   }
   //------------------------------------------------------------------
   /** Channel with name, topic, user count and a chat engine. */
   public Channel(String name, String topic, int ucount, IChatEngine engine) {
      this(name);
      _topic = topic;
      _userCount = ucount;
      _engine = engine;
   }
   //==================================================================
   //
   // Thread safe notification architecture
   //
   interface _ChannelEventNotifier {
      public void notify(ChannelListener listener);
   }
   private synchronized void notifyListeners(_ChannelEventNotifier notifier) {
      for (int i=0; i<_listeners.size(); i++) {
         ChannelListener listener = (ChannelListener)_listeners.elementAt(i);
         notifier.notify(listener);
      }      
   }
   /** Channel listener support. */
   public synchronized void addChannelListener(ChannelListener listener) {
      _listeners.addElement(listener);
   }
   /** Channel listener support. */
   public synchronized void removeChannelListener(ChannelListener listener) {
      _listeners.removeElement(listener);
   }
   //==================================================================
   /** Property change support. */
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      _propChangeSupport.addPropertyChangeListener(listener);
   }
   //------------------------------------------------------------------
   /** Property change support. */
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      _propChangeSupport.removePropertyChangeListener(listener);
   }
   //------------------------------------------------------------------	
   // Setters and getters...

   /** True if channel is connected/joined. */ 
   public boolean isConnected() {
      return _isConnected;
   }
   /** Set connection status. */ 
   public void setConnected(boolean value) {
      _isConnected = value;
   }
   //------------------------------------------------------------------
   /** Get the channel's chat engine, which may be null. */
   public IChatEngine getChatEngine() {
      return _engine;
   }
   //------------------------------------------------------------------
	/** Get channel's IRCConnectionListener, for internal use only. */
   IRCConnectionListener getChannelMux() {
	   if (_mux == null) {
		   _mux = new _ChannelMux();
		}
		return _mux;
	}
   //------------------------------------------------------------------
   /** Connect/join this channel, does nothing if channel has no chat engine. */
   public void connect() {
      if (_engine != null) {
         setConnected(true);
         _engine.sendJoin(this);

         final ChannelEvent event = new ChannelEvent(this);
         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onConnect(event);}
         });
      }
   }
   //------------------------------------------------------------------
   /** Disconnect/part this channel, does nothing if channel has no chat engine. */
   public void disconnect() {
      if (_engine != null) {
         _engine.sendPart(this);
         setConnected(false);

         final ChannelEvent event = new ChannelEvent(this);
         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onDisconnect(event);}
         });
      }
   }
   //------------------------------------------------------------------
	/** Get name of channel (e.g. "#java" or "#raleigh" */
   public String getName() {
	   return _name;
	}
   //------------------------------------------------------------------
   /** 
    * Set the chat engine to be used by this view. And: use that chat
    * engine to create a new chat view for this channel.
    */
   public void setChatEngine(IChatEngine engine) {
      _engine = engine;
   }
   //------------------------------------------------------------------
   /**
    * Set channel name, with property change support. Property
    * change event will include new and old values.
    */    
   public void setName(String name) {
      String old = _name;
      _name = name;
      _propChangeSupport.firePropertyChange("Name",old,_name);
   }
   //------------------------------------------------------------------
	/** Get channe's topic. */
	public String getTopic() {
	   return _topic;
	}
   //------------------------------------------------------------------
   /**
    * Set channel topic, with property change support. Property
    * change event will include new and old values.
    */    
   public void setTopic(String topic) {
      String old = _topic;
      _topic = topic;
      _propChangeSupport.firePropertyChange("Topic",old,_topic);
   }
   //------------------------------------------------------------------
	/** Number of users currently on channel. */
   public int getUserCount() {
      return _userCount;
   }
   //------------------------------------------------------------------
   /**
    * Set channel user count, with property change support. Property change
    * event will include new and old values as java.lang.Integer objects.
    */    
   public void setUserCount(int count) {
      int old = _userCount;
      _userCount = count;
      _propChangeSupport.firePropertyChange("UserCount",
		   new Integer(old),new Integer(_userCount));
   }
   //------------------------------------------------------------------
	/** String representation is channel name. */
   public String toString() {
      return _name;
   }
   //------------------------------------------------------------------
	/** Request that this channel be activated, given-focus or
	 * brought-to-front. */
   public void activate() {
      final ChannelEvent event = new ChannelEvent(this);
      notifyListeners(new _ChannelEventNotifier() { 
         public void notify(ChannelListener listener) {
			   listener.onActivation(event);
		   }
      });
   }
   //-------------------------------------------------------------------
   private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {

      try {in.defaultReadObject();} 
		catch (NotActiveException e) {e.printStackTrace();}
      _propChangeSupport = new PropertyChangeSupport(this);
      _listeners = new Vector();
   }
   //------------------------------------------------------------------
	/** Send an action to this channel. */
   public void sendAction(String msg) {
      _engine.sendMessage("\001ACTION "+msg+"\001",_name);
   }
   //------------------------------------------------------------------
	/** Ban a user from this channel. */
   public void sendBan(String nick) {
      _engine.sendCommand("MODE "+_name+" +b "+nick);
   }
   //------------------------------------------------------------------
	/** Send an arbitrary command to the chat server. */
   public void sendCommand(String cmd) {
      _engine.sendCommand(cmd);
   }
   //------------------------------------------------------------------
	/** Take operator rights from a user. */
   public void sendDeop(String nick) {
      _engine.sendCommand("MODE "+_name+" -o "+nick);
   }
   //------------------------------------------------------------------
	/** Join this channel, requires a chat engine. Before you call
	 * this method, make sure you have set the chat engine for this 
	 * channel. */
   public synchronized void sendJoin() {
      if (!_isConnected) {
         _isConnected = true;
         _engine.sendJoin(this);
      }
   }
   //------------------------------------------------------------------
	/** Kick a user from the channel. */ 
   public void sendKick(String nick) {
      _engine.sendCommand("KICK "+_name+" "+nick);
   }
   //------------------------------------------------------------------
   /** Send private message to server.
    * @deprecated Use Channel.privMsg() instead. */
   public void sendMessage(String str) {
      _engine.sendMessage(str,_name);
   }
   //------------------------------------------------------------------
	/** Give operator rights to a user. */
   public void sendOp(String nick) {
      _engine.sendCommand("MODE "+_name+" +o "+nick);
   }
   //------------------------------------------------------------------
   /** Part (leave) this channel. */
   public void sendPart() {
      _engine.sendPart(_name);
      _isConnected = false;

      final ChannelEvent event = new ChannelEvent(this);
      notifyListeners(new _ChannelEventNotifier() { 
         public void notify(ChannelListener listener) {
			   listener.onDisconnect(event);
		   }
      });
   }  
   //------------------------------------------------------------------
	/** Request version information for a user. */
   public void sendVersion(String nick) {
         _engine.sendVersion(nick);
   }

   //////////////////////////////////////////////////////////////////////

   private class _ChannelMux extends IRCConnectionAdapter {

      //------------------------------------------------------------------
      public void onAction(String user, String channel, String txt) {
         
         final ChannelEvent event = new ChannelEvent(Channel.this,txt);
         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onAction(event);}
         });
      }
      //------------------------------------------------------------------
      public void onBan(String banned, String chan, String banner) {
   
         final ChannelEvent event = new ChannelEvent(
			   Channel.this,banner,"",banned,"","");

         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onBan(event);}
         });
   
         // Was it something I said?
         if (banned.equals(_engine.getNick())) {
            _engine.fireStatusEvent("\nYou were banned from "+_name+"\n");
            disconnect();      
         }
      }
      //------------------------------------------------------------------
      public void onDisconnect() {
      }
      //------------------------------------------------------------------
      public void onJoin(String user, String nick, String chan, boolean create) 
		{
         final ChannelEvent event = 
			   new ChannelEvent(Channel.this,nick,user,"");

         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onJoin(event);}
         });
      }
      //------------------------------------------------------------------
      public void onJoins(String users, String chans) {
         
         final ChannelEvent event = new ChannelEvent(Channel.this,users);
         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onJoins(event);}
         });
      }
      //------------------------------------------------------------------
      public void onKick(String kicked, String chan, String kicker, String txt)       
		{
   
         final ChannelEvent event 
			   = new ChannelEvent(Channel.this,kicker,"",kicked,"",txt);

         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onAction(event);}
         });
   
         // Was it something I said?
         if (kicked.equals(_engine.getNick())) {
            _engine.fireStatusEvent("\nYou were kicked from "
               +_name+"("+(String)event.getValue()+")\n");
            disconnect();
         }
      }
      //------------------------------------------------------------------
      public void onTopic(String channel, String txt) {
		   setTopic(txt);
      }
      //------------------------------------------------------------------
      public void onMessage(String txt) {
      }
      //------------------------------------------------------------------
      public void onPrivateMessage(String orgnick, String chan, String txt) {
   
         final ChannelEvent event = 
			   new ChannelEvent(Channel.this,orgnick,"",txt);

         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onMessage(event);}
         });
      }
      //------------------------------------------------------------------
      public void onNick(String user, String oldnick, String newnick) {
   
         final ChannelEvent event = 
			   new ChannelEvent(Channel.this,oldnick,user,newnick);

         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onNick(event);}
         });
      }
      //------------------------------------------------------------------
      public void onPart(String user, String nick, String chan) {
   
         final ChannelEvent event = 
			   new ChannelEvent(Channel.this,nick,user,"");

         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onPart(event);}
         });
      }
      //------------------------------------------------------------------
      public void onOp(String oper, String chan, String oped) {
   
         final ChannelEvent event = 
			   new ChannelEvent(Channel.this,oper,"",oped,"","");

         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onOp(event);}
         });
      }
      //------------------------------------------------------------------
      public void onQuit(String user, String nick, String txt) {
   
         final ChannelEvent event = new ChannelEvent(Channel.this,nick,"",txt);
         notifyListeners(new _ChannelEventNotifier() { 
            public void notify(ChannelListener listener) 
				   {listener.onQuit(event);}
         });
      }
   }
}

