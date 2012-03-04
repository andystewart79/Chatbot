/* 
 * FILE: ChatEngine.java 
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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.relayirc.util.RCTest;

///////////////////////////////////////////////////////////////////////////
/** 
 * <p>Manages a connection to an IRC server and handles incoming
 * messages by creating channel objects, routing messages to channel
 * objects and routing events to chat engine listeners.</p>
 *
 * <p>After you have constructed a chat engine, add a ChatEngineListener
 * to be notified of server connection and disconnection, channel
 * joins and parts and status messages from the engine. Connect to 
 * the chat server using the connect() method and use the sendJoin()
 * and sendPart() commands to join and leave chat channels. When a 
 * channel is joined, your listener will be informed and you may add 
 * a ChannelListener to the channel object so that you can repond 
 * to messages, bans, kicks, etc. from that channel.</p>
 *
 * @see org.relayirc.chatengine.ChatEngineListener
 * @see org.relayirc.chatengine.Server
 * @see org.relayirc.chatengine.Channel
 * @see org.relayirc.chatengine.IdentServer
 * @see org.relayirc.chatengine.ChannelSearch
 * @see org.relayirc.chatengine.IChatApp
 * @see org.relayirc.chatengine.IRCConnection
 * @see org.relayirc.chatengine.IRCConnectionListener
 *
 * @author David M. Johnson.
 */
public class ChatEngine implements IChatEngine {

   private Server        _server;           // Chat server
   private int           _channelCount = 0; // Num. channels on server
   private String        _nick;             // User login information
   private String        _altNick;          //   "    "
   private String        _userName;         //   "    "
   private String        _fullName;         //   "    "
   private IRCConnection _connection;       // Chat connection
   private ChannelSearch _search = null;    // Current search, if one exists
   private IdentServer   _identd;           // One-shot identd server 
  
	// Channel-specific IRCConnectionListener objects, keyed by name
   private Hashtable _channels = new Hashtable(); 

   private Vector    _listeners = new Vector();

   private String    _appName = "Relay IRC chat-engine";
   private String    _appVersion = "Unknown version";

   // Don't expose an IRCConnectionListener interface, but do use one internally.
   private _ChatEngineMux _mux = new _ChatEngineMux();

   //================================================================
   // Constructors 
   //================================================================

   /** 
     * Construct a chat engine by specifying server name, server port and 
     * user logon parameters. 
	  * @param serverName  IRC chat server hostname (e.g. irc.mindspring.com).
	  * @param serverPort  IRC chat server port (e.g. 6667).
     * @param altNick     Alternate nickname
     * @param userName    User's UNIX login name
     * @param fullName    User's real name
     */
   public ChatEngine(String serverName, int serverPort,
      String nick, String altNick, String userName, String fullName) { 

      _server = new Server(serverName,serverPort);

      _nick = nick;
      _altNick = altNick;
      _userName = userName;
      _fullName = fullName;

      _connection = new IRCConnection(
        _server.getName(),_server.getPort(),
        _nick,_altNick,_userName,_fullName);

      _connection.setIRCConnectionListener(_mux);
   }
   //================================================================
   // Thread safe notification architecture
   //================================================================

   interface _EngineEventNotifier {
      public void notify(ChatEngineListener listener);
   }
   //------------------------------------------------------------------
   private synchronized void notifyListeners(_EngineEventNotifier notifier) {
      for (int i=0; i<_listeners.size(); i++) {
         ChatEngineListener listener = (ChatEngineListener)_listeners.elementAt(i);
         notifier.notify(listener);
      }      
   }
   //------------------------------------------------------------------
   /** Add a chat engine listener. */
   public synchronized void addChatEngineListener(ChatEngineListener listener) {
      _listeners.addElement(listener);
   }
   //------------------------------------------------------------------
   /** Remove a chat engine listener. */
   public synchronized void removeChatEngineListener(ChatEngineListener listener) {
      _listeners.removeElement(listener);
   }

   //================================================================
   // Accessors 
   //================================================================

   /** Get app name to be reported to version queries. */ 
   public String getAppName() {return _appName;}

   /** Set app name to be reported to version queries. */ 
   public void setAppName(String name) {_appName = name;}
   
   /** Get app version to be reported to version queries. */ 
   public String getAppVersion() {return _appVersion;}

   /** Set app verion to be reported to version queries. */ 
   public void setAppVersion(String version) {_appVersion = version;}

   //------------------------------------------------------------------
   /** Check connection status and returns true if connected. */
   public boolean isConnected() {
      return _connection.isConnected();
   }
   //------------------------------------------------------------------
   /** 
    * Check connection status and returns true if engine 
    * is in the process of connecting 
    */
   public boolean isConnecting() {
      return _connection.isConnecting();
   }
  //------------------------------------------------------------------
   /** Get version of IRC server. */
   //public String getServerVersion() {
       //return _serverVersion;
   //}
   //------------------------------------------------------------------
   /** Get nick name currently in use */
   public String getNick() {
      return _connection.getNick();
   }

   //================================================================
   // Methods 
   //================================================================

   /** Connect to IRC server that was specified in the constructor. */
   public void connect() {
      if (!isConnected() && !isConnecting()) {
         _identd = new IdentServer(this,_userName);
         _connection.open();
      }
      else {
         fireStatusEvent("Cannot connect: already connected.");
      }
   }
   //------------------------------------------------------------------
   /** 
    * Disconnect from server by sending a QUIT to the server, closing 
    * the socket to the server and then waiting for the message loop
    * thread to die.
    */
   public void disconnect() {
      if (isConnected()) {
         fireStatusEvent("Disconnecting...");
         _connection.close();
      }
      else {
         fireStatusEvent("Cannot disconnect: not connected.");
      }
   }
   //------------------------------------------------------------------
   /** Send status message to all ChatEngineListeners. */
   public void fireStatusEvent(String msg) {
      final ChatEngineEvent event = new ChatEngineEvent(this,msg);
      notifyListeners(new _EngineEventNotifier() { 
         public void notify(ChatEngineListener listener) {listener.onStatus(event);}
      });
   }
   //------------------------------------------------------------------
   /** 
    * Processes text entered by the user within a channel. If the 
    * text starts with /me, then the rest of the text is sent to the 
    * server as an ACTION command. If the text starts with / then the
    * / is stripped off and the text is sent to the server as a command.
    * Otherwise, the text is sent as a message. 
    */
   public void processUserInput(String txt, String chan) {

      if (txt.length()>2 && txt.toLowerCase().substring(0,3).equals("/me")) {

         // Action
         sendMessage("\001ACTION "+txt.substring(3)+"\001",chan);
         _mux.onAction(_connection.getNick(),chan,txt.substring(3));
      }
      else if (txt.substring(0,1).equals("/")) {
         
         // Some other command
         sendCommand(txt.substring(1));
      }
      else {
         
         // Normal message
         sendMessage(txt,chan);
         _mux.onPrivateMessage(_connection.getNick(),chan,txt);
      }
   }
   //------------------------------------------------------------------
   /** Send command string directly to server */
   public synchronized void sendCommand( String str ) {
      if (isConnected()) {
         try {
            RCTest.println("ChatEngine: sending command "+str);
            _connection.writeln(str+"\r\n");
            //RCTest.println("ChatEngine: sent command ");

            try {
               // If this is a join command, force creation of it's view
               StringTokenizer toker = new StringTokenizer(str);
               String cmd = toker.nextToken().toLowerCase();
               if (cmd.equals("join")) {
                  createChannel(toker.nextToken());
               } 
            }
            catch (Exception e) {
               e.printStackTrace();
            }
         }
         catch (Exception e) {
            fireStatusEvent("Error sending command");
         }
      }
   }
   //------------------------------------------------------------------
   /** 
    * Join specified channel by sending JOIN command to IRC server,
    * adding channel object to engine's channel collection and notifying 
    * listeners of channel join. 
    */
   public void sendJoin(Channel chan) {
         
      // Make sure channel is in hash
      if (!_channels.contains(chan)) {
         _channels.put(chan.getName(),chan);
      }

      // Ask server to join
      sendCommand("JOIN "+chan.getName());

      // Notify listeners that channel has been joined
      final ChatEngineEvent event = new ChatEngineEvent(this,chan);
      notifyListeners(new _EngineEventNotifier() { 
         public void notify(ChatEngineListener listener) {
            listener.onChannelJoin(event);}
         }
      );
   }
   //------------------------------------------------------------------
   /** 
    * Join specified channel by sending a JOIN command to the IRC server
    * creating a new channel object and notifying listeners of channel join. 
    */
   public void sendJoin(String name) {

      // See if channel object already exists
      name = name.trim().toLowerCase();
      Channel chan = (Channel)_channels.get(name);

      if (chan == null) {

         // Channel object does not exist, so create it
         RCTest.println("ChatEngine.sendJoin(): Creating channel ("+name+")\n");
         chan = new Channel(name,this);
         chan.setConnected(true);
         _channels.put(name,chan);

         // Ask server to join
         sendCommand("JOIN "+name);

         // Notify listeners that channel has been joined
         final ChatEngineEvent event = new ChatEngineEvent(this,chan);
         notifyListeners(new _EngineEventNotifier() { 
            public void notify(ChatEngineListener listener) {listener.onChannelJoin(event);}
         });
         
      }
      else {
         // Use pre-existing channel object
         sendJoin(chan);
         chan.setConnected(true);
      }
   }
   //------------------------------------------------------------------
   /** 
    * Send a PRIVMSG message to server.
    * @deprecated Use Channel.privMsg() instead.
    */
   public void sendMessage(String str, String chan) {
      sendCommand("PRIVMSG "+chan+" "+":"+str);
   }
   //------------------------------------------------------------------
   /** Send channel part, notify listeners and remove channel. */
   public void sendPart(Channel chan) {
      sendPart(chan.getName());
   }
   //------------------------------------------------------------------
   /** Send channel part, notify listeners and remove channel. */
   public void sendPart(String chanName) {
      sendCommand("PART "+chanName);
      Channel chan = getChannel(chanName,false);
      chan.getChannelMux().onPart(_userName,_nick,chanName);

      final ChatEngineEvent event = new ChatEngineEvent(this,chan);
      notifyListeners(new _EngineEventNotifier() { 
         public void notify(ChatEngineListener listener) {
            listener.onChannelPart(event);}
         }
      );
      _channels.remove(chan);
   }
   //------------------------------------------------------------------
   /** Send version information to server */
   public void sendVersion(String user) {
      sendCommand("PRIVMSG "+user+" "+":\001VERSION\001");
   }
   //------------------------------------------------------------------
   /** 
    * Send quit message to server.
    */
   public void sendQuit( String str ) {
      sendCommand("QUIT :"+str);   
   }
   //------------------------------------------------------------------
   /** Start a channel search using the specified channel search object. */ 
   public void startChannelSearch(ChannelSearch search) {
      _search = search;

      String cmd = "LIST ";

      /*
      if (search.getMinUsers()>Integer.MIN_VALUE && search.getMaxUsers()<Integer.MAX_VALUE) {
         cmd = cmd + "<" + search.getMaxUsers() + ",";
         cmd = cmd + ">" + search.getMinUsers(); 
      }
      else if (search.getMinUsers()>Integer.MIN_VALUE) {
         cmd = cmd + ">" + search.getMinUsers(); 
      }
      else if (search.getMaxUsers()<Integer.MAX_VALUE) {
         cmd = cmd + "<" + search.getMaxUsers();
      }
      */

      sendCommand(cmd);               
   }
   //------------------------------------------------------------------
   private void createChannel(String name) {
      RCTest.println("ChatEngine: createChannel for "+name);
      getChannel(name,true);
   }
   //------------------------------------------------------------------
   /** Determine if channel view is active. */
   private boolean isChannelActive( String channel ) {
      Channel chan = (Channel)_channels.get(channel);
      if (chan!=null) 
         return true;
      else
         return false;
   }
   //------------------------------------------------------------------
   /** Returns view for specified channel, or null if there is none. */
   private Channel getChannel(String name) {
      return getChannel(name,false);
   }
   //------------------------------------------------------------------
   /**
    * Returns channel object specified by name, creates one if necessary.
    * @param name Name of channel.
    * @param manditory If channel does not exist, then create it.
    */
   private synchronized Channel getChannel(String name, boolean manditory) {

      name = name.trim().toLowerCase();
      Channel chan = (Channel)_channels.get(name);

      if ( chan==null && manditory ) {

         // Channel object does not exist and we need it, so create it
         RCTest.println("ChatEngine.getChannel(): Creating channel ("+name+")\n");
         chan = new Channel(name,this);
         chan.setConnected(true);
         _channels.put(name,chan);

         // Notify listeners that channel has been joined
         final ChatEngineEvent event = new ChatEngineEvent(this,chan);
         notifyListeners(new _EngineEventNotifier() { 
            public void notify(ChatEngineListener listener) 
				   {listener.onChannelJoin(event);}
         });
         
      }   
      else if (chan==null) {

         // Channel object does not exist, and we don't need it
         RCTest.println("Couldn't find channel "+name+", hash size="
            +Integer.toString(_channels.size())+"\n");
      }
      return chan;
   }

   ////////////////////////////////////////////////////////////////////////////////////

   private class _ChatEngineMux implements IRCConnectionListener {

      //------------------------------------------------------------------
      public void onAction( String user, String chan, String txt ) {
         getChannel(chan,true).getChannelMux().onAction(user,chan,txt);
      }
      //------------------------------------------------------------------
      public void onBan( String banned, String chan, String banner ) {
         getChannel(chan,true).getChannelMux().onBan(banned,chan,banner);
      }
      //------------------------------------------------------------------
      public void onClientInfo(String orgnick) {
         String response = "NOTICE "+orgnick+
            " :\001CLIENTINFO :Supported queries are VERSION and SOURCE\001";
         _connection.writeln(response);
      }
      //------------------------------------------------------------------
      public void onClientSource(String orgnick) {
         String response = "NOTICE "+orgnick+
            " :\001SOURCE :http://relayirc.netpedia.net\001";
         _connection.writeln(response);
      }
      //------------------------------------------------------------------
      public void onClientVersion(String orgnick) {
         // Tell them everything
         String osdesc = 
            System.getProperty("os.name").replace(':','-')+"/"+
            System.getProperty("os.version").replace(':','-')+"/"+
            System.getProperty("os.arch").replace(':','-');
         String vmdesc = "Java "+
            System.getProperty("java.version").replace(':','-')+" ("+osdesc+")";

         String response = "NOTICE "+orgnick+" :\001VERSION "+
            _appName+":"+_appVersion+":"+vmdesc+"\001";

         fireStatusEvent("\nSending VERSION information to "+orgnick+"\n");
         _connection.writeln(response);
      }
      //----------------------------------------------------------------------
      public void onConnect() { 
         final ChatEngineEvent event = new ChatEngineEvent(ChatEngine.this);
         notifyListeners(new _EngineEventNotifier() { 
            public void notify(ChatEngineListener listener) 
				   {listener.onConnection(event);}
         });
      }
      //----------------------------------------------------------------------
      public void onDisconnect() {
         final ChatEngineEvent event = new ChatEngineEvent(ChatEngine.this);
         notifyListeners(new _EngineEventNotifier() { 
            public void notify(ChatEngineListener listener) 
				   {listener.onDisconnection(event);}
         });
      }
      //------------------------------------------------------------------
      public void onJoin( String user, String nick, String chan, boolean create ) {
         getChannel(chan,true).getChannelMux().onJoin(user,nick,chan,create);
      }
      //------------------------------------------------------------------
      public void onJoins( String users, String chan) {
         getChannel(chan,true).getChannelMux().onJoins(users,chan);
      }
      //------------------------------------------------------------------
      public void onKick( String kicked, String chan, String kicker, String txt ) {
         getChannel(chan,true).getChannelMux().onKick(kicked,chan,kicker,txt);
      }
      //------------------------------------------------------------------
      public void onMessage(String message) {
         fireStatusEvent(message+"\n");
      }
      //------------------------------------------------------------------
      public void onPrivateMessage(String orgnick, String chan, String txt) {
         getChannel(chan,true).getChannelMux().onPrivateMessage(orgnick,chan,txt);
      }
      //------------------------------------------------------------------
      public void onNick( String user, String oldnick, String newnick ) {
         fireStatusEvent(oldnick+" now known as "+newnick);
         for (Enumeration e = _channels.elements() ; e.hasMoreElements() ;) {
            Channel chan = (Channel)e.nextElement();
            chan.getChannelMux().onNick(user,oldnick,newnick);
         }
      }
      //------------------------------------------------------------------
      public void onNotice(String text) {
         fireStatusEvent("NOTICE: "+text);
      }
      //------------------------------------------------------------------
      public void onPart( String user, String nick, String chan ) {
         getChannel(chan,true).getChannelMux().onPart(user,nick,chan);
      }
      //------------------------------------------------------------------
      public void onOp( String oper, String chan, String oped ) {
         getChannel(chan,true).getChannelMux().onOp(oper,chan,oped);
      }
      //------------------------------------------------------------------
      public void onParsingError(String message) {
         fireStatusEvent("Error parsing message: "+message);
      }
      //------------------------------------------------------------------
      public void onPing(String params) {
         _connection.writeln("PONG "+params+"\r\n");
      }
      //------------------------------------------------------------------
      public void onStatus(String msg) {
         fireStatusEvent(msg);
      }
      //------------------------------------------------------------------
      public void onVersionNotice(String orgnick, String origin, String version) {
         fireStatusEvent("\nVERSION Information for "+orgnick+"("+origin+")\n");
      }
      //------------------------------------------------------------------
      public void onQuit( String user, String nick, String txt ) {
         for (Enumeration e = _channels.elements() ; e.hasMoreElements() ;) {
            Channel chan = (Channel)e.nextElement();
            chan.getChannelMux().onQuit(user,nick,txt);
         }
      }
      //------------------------------------------------------------------
      /** Respond to server version reply. */
      public void onReplyVersion(String version) {
         fireStatusEvent("Server Version: "+version);
         /*try {
            System.out.println("0: "+((_tok)tokens.elementAt(0)).token);
            System.out.println("1: "+((_tok)tokens.elementAt(1)).token);
            System.out.println("2: "+((_tok)tokens.elementAt(2)).token);
            System.out.println("3: "+((_tok)tokens.elementAt(3)).token);
            System.out.println("4: "+((_tok)tokens.elementAt(4)).token);
         } catch (Exception e) {}*/
      }
      //------------------------------------------------------------------
      /** Respond to */
      public void onReplyListUserChannels(int channelCount) {
         _channelCount = channelCount;
      }
      //------------------------------------------------------------------
      /** Respond to channel-list-start reply. */
      public void onReplyListStart() {
         if (_search!=null) _search.searchStarted(_channelCount);
      }
      //------------------------------------------------------------------
      /** Respond to channel list item reply. */
      public void onReplyList(String channel, int userCount, String topic) {
         Channel channelObject = new Channel(channel,topic,userCount,ChatEngine.this);
         _search.processChannel(channelObject);
      }
      //------------------------------------------------------------------
      /** Respond to end-of-channel-list reply. */
      public void onReplyListEnd() {
         if (_search!=null) {
            _search.searchEnded();
            _search.setComplete(true);
            _search = null;
         }
      }
      //------------------------------------------------------------------
      /** 
       * Respond to RPL_LUSERCLIENT messages which usually look like this: 
       * "There are <integer> users and <integer> invisible on <integer> servers"
       */
      public void onReplyListUserClient(String msg) {
         fireStatusEvent(msg);
      }
      //------------------------------------------------------------------
      /** Respond to who-is-user reply. */
      public void onReplyWhoIsUser(String userName, String miscText) {
         fireStatusEvent(userName+" is logged into "+miscText);
      }
      //------------------------------------------------------------------
      /** Respond to who-is-server reply. */
      public void onReplyWhoIsServer(String info) {
         fireStatusEvent(info);
      }
      //------------------------------------------------------------------
      /** Respond to who-is-operator reply. */
      public void onReplyWhoIsOperator(String info) {
         fireStatusEvent(info);
      }
      //------------------------------------------------------------------
      /** Respond to who-is-idle reply. */
      public void onReplyWhoIsIdle(String info) {
         fireStatusEvent(info);
      }
      //------------------------------------------------------------------
      /** Respond to end of WHOIS reply. */
      public void onReplyEndOfWhoIs() {
         fireStatusEvent(""); // force a line feed
      }
      //------------------------------------------------------------------
      /** 
       * Respond to who-is-on-channels reply. The server sends this reply
       * after you do a WHOIS command on a user. The reply lists the channels
       * that the user is current inhabiting. 
       * @param nick Nick name of the user.
       * @param channels List of channels, separated by spaces.
       */
      public void onReplyWhoIsChannels(String nick, String channels) {
         fireStatusEvent(nick+" is on "+channels); 
      }
      //------------------------------------------------------------------
      /** Respond to Message-Of-The-Day start reply. */
      public void onReplyMOTDStart() {
      }
      //------------------------------------------------------------------
      /** Respond to Message-Of-The-Day reply. */
      public void onReplyMOTD(String msg) {
         fireStatusEvent(msg);
      }
      //------------------------------------------------------------------
      /** Respond to Message-Of-The-Day end reply. */
      public void onReplyMOTDEnd() {
      }
      //------------------------------------------------------------------
      /** 
       * Respond to name reply. This reply is sent to inform you of the 
       * users that inhabit the channel that you just joined.
       * @param channel Name of channel.
       * @param users List of users, separated by spaces. 
       */
      public void onReplyNameReply(String channel, String users) {
         onJoins(users,channel);
      }
      //------------------------------------------------------------------
      /** Respond to topic change. */
      public void onTopic(String channel, String topic) {
         getChannel(channel,true).getChannelMux().onTopic(channel,topic);
      }
		//------------------------------------------------------------------
      /** Respond to topic reply. */
      public void onReplyTopic(String channel, String topic) {
         getChannel(channel,true).getChannelMux().onReplyTopic(channel,topic);
      }
      //------------------------------------------------------------------
      /** Respond to no Message-Of-The-Day reply. */
      public void onErrorNoMOTD() {
		   fireStatusEvent("\nERROR: No message of the day.\n");
      }
      //------------------------------------------------------------------
      /** Respond to need more params error. */
      public void onErrorNeedMoreParams() {
		   fireStatusEvent("\nERROR: Meed more parameters.\n");
      }
      //------------------------------------------------------------------
      /** Respond to no nick name given error. */
      public void onErrorNoNicknameGiven() {
         onErrorNeedMoreParams();
      }
      //------------------------------------------------------------------
      /** Respond to nick name in use error. */
      public void onErrorNickNameInUse(String badNick) {
         onErrorNickCollision(badNick);
      }
      //------------------------------------------------------------------
      /** Respond to nick name collision error. */
      public void onErrorNickCollision(String badNick) {

         if (badNick.equals(_nick)) {
		      fireStatusEvent("\nWARNING: Nick name already in use, using alternate...\n");
            _connection.sendNick(_altNick);
         }
         else if (badNick.equals(_altNick)) {
		      fireStatusEvent("\nERROR: Alternate nick name already in use, disconnecting...\n");
            _connection.close();
         }
         else {
		      fireStatusEvent("\nERROR: Nick name already in use, reverting to "+_nick);
            _connection.sendNick(_nick);
         }
      }
      //------------------------------------------------------------------
      /** Respond to erroneus nick name error. */
      public void onErrorErroneusNickname(String badNick) {

         if (badNick.equals(_nick)) {
		      fireStatusEvent("\nERROR: Error in nick name, using alternate...\n");
            _connection.sendNick(_nick);
         }
         else if (badNick.equals(_nick)) {
		      fireStatusEvent("\nERROR: Error in alternate nick name, disconnecting...\n");
            _connection.close();
         }
         else {
		      fireStatusEvent("\nERROR: Error in nick name, reverting to "+_nick);
            _connection.sendNick(_nick);
         }
      }
      //------------------------------------------------------------------
      /** Respond to */
      public void onErrorAlreadyRegistered() {
	      fireStatusEvent("\nERROR: you are already connected to this server!\n");
	      disconnect();
      }
      //------------------------------------------------------------------
      /** Respond to message not recognized by message switch. */
      public void onErrorUnknown(String message) {
	      RCTest.println("UNKNOWN: "+message+"\n");
      }
      //------------------------------------------------------------------
      /** Respond to message not supported by message switch. */
      public void onErrorUnsupported(String message) {
	      RCTest.println("UNSUPPORTED: "+message+"\n");
      }
   }
}

