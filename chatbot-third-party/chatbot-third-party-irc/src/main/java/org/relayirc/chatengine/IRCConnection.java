
/*
 * FILE: IRCConnection.java
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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.relayirc.util.RCTest;
/** 
 * <p>A socket connection to a RFC-1459 compatible IRC server. 
 * Parses incoming IRC messages, replies, commands and errors 
 * and notifies all listeners of such. Also provides a writeln()
 * method for sending commands to the IRC server.</p>
 * 
 * FIX: Currently, only one listener is allowed.<br>
 *
 * @see org.relayirc.chatengine.IRCConnectionListener
 * @see org.relayirc.chatengine.IRCConnectionAdapter
 *  
 * @author David M. Johnson
 */
public class IRCConnection implements Runnable, IRCConstants {

   public static final int CONNECTED     =  0;
   public static final int CONNECTING    =  1;
   public static final int DISCONNECTED  =  2;
   public static final int DISCONNECTING =  3;

   private int       _state = DISCONNECTED;
   private String    _server;
   private int       _port;
   private String    _nick; 
   private String    _altNick; 
   private String    _userName; 
   private String    _fullName;
   private Socket    _socket;                                  
   private String    _localHost;          // = _socket.getLocalAddress().getHostName()
   private Thread    _messageLoopThread; 
   private BufferedReader        _inputStream;   // Input from server socket
   private DataOutputStream      _outputStream;  // Output to server socket
   private IRCConnectionListener _listener;      // FIX: support arbitrary num. of listeners!

   // Don't expose an IRCConnectionListener interface, but do use one internally.
   private _IRCConnectionMux  _mux = new _IRCConnectionMux();
   
   /** Used in parsing. */
   private class _tok {
      String token;
      int index;
   }

   //------------------------------------------------------------------
   /**
    * Construct, but do not open, an IRC connection by specifying server 
    * hostname and port of a IRC server as well as user registration information.
    * @param server     DNS-resolvable hostname of server.
    * @param port       Server port number to connect to.
    * @param nick       User's IRC nick name (e.g. Mortz).
    * @param userName   User's login/user name (e.g. mps).
    * @param fullName   User's full name (e.g. Mortimer P. Snerd) 
    */
   public IRCConnection(String server, int port, 
      String nick, String altNick, String userName, String fullName)
   {
      _server     = server; 
      _port       = port;    
      _nick       = nick; 
      _altNick    = altNick; 
      _userName   = userName; 
      _fullName   = fullName;

      // Use do-nothing listener until we get a real one
      _listener = new IRCConnectionAdapter();
   }
   //------------------------------------------------------------------
	/** Get engine's state (see ChatEngine.DISCONNECTED, ChatEngine.CONNECTED, etc. */
	public int getState() {
	   return _state;
	}
   //------------------------------------------------------------------
	/** Set engine's state (see ChatEngine.DISCONNECTED, ChatEngine.CONNECTED, etc. */
	public void setState(int state) {
		_state = state;
	}
   //------------------------------------------------------------------
   /** 
    * Opens socket connection to IRC server. Starts message loop thread
    * and starts firing events to listeners.
    */
   public void open() {
      if (getState() == DISCONNECTED) {
		   setState(CONNECTING);
         _messageLoopThread = new Thread(this);
         _messageLoopThread.start();
      }
   }
   //------------------------------------------------------------------
   /** Close socket connection to IRC server and close down message loop thread. */
   public void close() {
      if (getState() == CONNECTED) {
		   setState(DISCONNECTING);

         _mux.onStatus("Closing connection");

         // Try to disconnect as gracefully as possible
         try { 
			   _outputStream.writeBytes("QUIT"); 
			} catch (Exception e) {}
         try { 
			   _socket.close(); 
			} catch (Exception e) {}
         _socket = null;

         // Wait for the message loop thread to die
         try {
            _messageLoopThread.join();
         }
         catch (InterruptedException e) {
            // I think we can safely ignore this
         }

		   setState(DISCONNECTED);
         _mux.onDisconnect();
      }
   }
   //------------------------------------------------------------------
   /** Returns true if we are connected. @deprecated Use getState() instead. */
   public boolean isConnected() {
      return (getState() == CONNECTED);
   }
   //------------------------------------------------------------------
   /** Returns true if we are waiting for a connection. 
	 * @deprecated Use getState() instead. */
   public boolean isConnecting() {
      return (getState() == CONNECTED);
   }
   //------------------------------------------------------------------
   /** For now, only one listener is supported. */
   public void setIRCConnectionListener(IRCConnectionListener listener) {
      _listener = listener;
   }
   //------------------------------------------------------------------
   /** Get nick name currently in use. */
   public String getNick() {
      return _nick;
   }   
   //------------------------------------------------------------------
   /** 
    * Send change-nickname request to IRC server and save value as
    * the nick name currently in use. 
    */
   public void sendNick(String nick) {
      _nick = nick;
      writeln("NICK "+_nick+"\r\n");
   }   
   //------------------------------------------------------------------
   /** Parses nick name of origin */
   private String parseOrgnick( String origin ) {
      String orgnick = null;
      if (origin.length() > 0) {
         StringTokenizer toker2 = new StringTokenizer(origin,"!");
         try {
            orgnick = toker2.nextToken();
         }
         catch (NoSuchElementException e) {
            orgnick = null;
         }
      }
      return orgnick;
   }
   //------------------------------------------------------------------
   /** 
    * The main message loop. Opens a socket connection to the IRC
    * server, sends logon information and enters message loop. The 
    * message loop parses each incoming message into a command string
    * or opcode and arguments and calls the appropriate method on the mux.
    */
   public void run() {

      try {
         _mux.onStatus("Contacting server ["+_server+":"+_port+"]");
         _socket = new Socket(_server,_port); 
	      _localHost = _socket.getLocalAddress().getHostName();
      }
      catch (Exception e) { // UnknownHostException or IOException
         _mux.onStatus("Unable to contact server ["+_server+":"+_port+"]");
         close();
         return;
      }
      _mux.onStatus("Contacted server ["+_server+":"+_port+"]");

      try {

         //-----------------------------------------------------------------------------
         // OPEN IO STREAMS AND LOG IN TO SERVER. 
         //           
         _mux.onStatus("Opening IO streams to server ["+_server+":"+_port+"]");
         _inputStream = new BufferedReader(
            new InputStreamReader(new DataInputStream(_socket.getInputStream())));
         _outputStream = new DataOutputStream(_socket.getOutputStream());

         // Register nick 
         _mux.onStatus("Registering nick ["+_nick+"] with server ["+_server+":"+_port+"]");
         writeln("NICK "+getNick()+"\r\n"); 

         // Register user name
         _mux.onStatus("Registering user name ["+_userName+"] with server ["+_server+":"+_port+"]");
         writeln( "USER "+_userName+" "+_localHost+" "+_server+" :"+_fullName+"\r\n");
      }
      catch (Exception e) { // IOException
         _mux.onStatus("Unable to send login message to server  ["+_server+":"+_port+"]");
         close();
         return;
      }

      try {
         String message;  // The full message 
         String origin;   // User who sent the message (nick!email@host)
         String command;  // Message's command
         String response; // Response to be sent
         
			int nicktries = 1;
         boolean terminate = false;

         //-----------------------------------------------------------------------------
         // MESSAGE LOOP. Loops until either there is no more to read, the user requests
         // disconnection or an exception we can't handle blows us out of the loop.
         //
         _mux.onStatus("Waiting for response from server ["+_server+":"+_port+"]");
         while ((message = _inputStream.readLine()) != null) {
            RCTest.println("message="+message);

            int pos=0;
            origin = new String("");
            command = new String(""); 
            Vector tokens = new Vector();

            // Parse message into a vector of strings
            StringTokenizer toker1 = new StringTokenizer(message);
            try {
               for (int i=0; i<6; i++) {
                  _tok t = new _tok();
                  t.token = toker1.nextToken();
                  t.index = pos;
                  tokens.addElement(t);
                  pos += t.token.length()+1;
               }
            }
            catch ( NoSuchElementException e ) {} // is ok
            catch ( Exception e ) {e.printStackTrace(); } // is not ok
            
            // Get origin, if there is one, and the command
            if (tokens.size() > 0) {
               if (((_tok)tokens.elementAt(0)).token.substring(0,1).equals(":")) {

                  // token 0 begins with ":" so assume it's the origin
                  origin = ((_tok)tokens.elementAt(0)).token.substring(1);
                  command = ((_tok)tokens.elementAt(1)).token;
               }
               else {
                  // assume token 0 is the command
                  command = ((_tok)tokens.elementAt(0)).token;
               }
            }

            //--------------------------------------------------------------------------
            // MESSAGE SWITCH. First handle commands. Parse the command arguments and 
            // pass them off to the appropriate handler method.            
            //
            if (command.equals("PING")) {
               String params = message.substring( message.indexOf("PING")+4 );
               _mux.onPing(params);
            } 

            else if (command.equals("PRIVMSG")) {
               String channel = ((_tok)tokens.elementAt(2)).token;
               String text = message.substring( ((_tok)tokens.elementAt(3)).index ).trim();
               String orgnick = parseOrgnick(origin); 
               if ( orgnick != null) {
                  if (text.indexOf("\001VERSION") != -1) {
                     _mux.onClientVersion(orgnick);
                  }
                  else if (text.indexOf("\001SOURCE") != -1) {
                     _mux.onClientSource(orgnick);
                  }
                  else if (text.indexOf("\001CLIENTINFO") != -1) {
                     _mux.onClientInfo(orgnick);
                  }
                  else if (text.indexOf("ACTION") != -1) {
                     _mux.onAction(orgnick,channel,text.substring(9));
                  }
                  else {
                     _mux.onPrivateMessage(orgnick,channel,text.substring(1));
                  }
               }
            }

            else if (command.equals("NOTICE")) {
               String orgnick = parseOrgnick(origin); 
               String text = message.substring( ((_tok)tokens.elementAt(3)).index ).trim();
					try {	
                  if (text.substring(0,9).equals(":\001VERSION")) {
                     _mux.onVersionNotice(orgnick,origin,text.substring(9));
						} 
						else {
                     _mux.onNotice(text);
						}
					}
					catch (StringIndexOutOfBoundsException e) {
					   // ignore
               }
            }

            else if (command.equals("MODE")) {
               _mux.onStatus("MODE: "+message);
               String orgnick = parseOrgnick(origin); 
               if ( orgnick != null) {
                  String chan = ((_tok)tokens.elementAt(2)).token;
                  String mode = ((_tok)tokens.elementAt(3)).token;
                  if (mode.equals("+o")) {
                     String oped = ((_tok)tokens.elementAt(4)).token;
                     _mux.onOp(orgnick,chan,oped);
                  }
                  else if (mode.equals("+b")) {
                     String banned = ((_tok)tokens.elementAt(4)).token;
                     _mux.onBan(banned,chan,orgnick);
                  }
               }
            }

            else if (command.equals("JOIN")) {
               String channel = ((_tok)tokens.elementAt(2)).token;
               String orgnick = parseOrgnick(origin); 
               if ( orgnick != null) {
                  _mux.onJoin(origin,orgnick,channel.substring(1),false);
               }
            }

            else if (command.equals("PART")) {
               RCTest.println("PART: "+message);
               String channel = ((_tok)tokens.elementAt(2)).token;
               String orgnick = parseOrgnick(origin); 
               if ( orgnick != null ) {
                  _mux.onPart(origin,orgnick,channel);
               }
            }

            else if (command.equals("KICK")) {
               RCTest.println("KICK: "+message);
               try {
                  String orgnick = parseOrgnick(origin); 
                  String channel = ((_tok)tokens.elementAt(2)).token;
                  String kicked = ((_tok)tokens.elementAt(3)).token;
                  String reason = ((_tok)tokens.elementAt(4)).token;
                  if ( orgnick != null) {
                     _mux.onKick(kicked,channel,orgnick,reason);
                  }
               }
               catch ( NoSuchElementException e ) {} // is ok
               catch ( Exception e ) {e.printStackTrace(); } // is not ok
            }  

            else if (command.equals("QUIT")) {
               String channel = ((_tok)tokens.elementAt(2)).token;
               String text = message.substring( ((_tok)tokens.elementAt(2)).index+1 );
               String orgnick = parseOrgnick(origin); 
               if ( orgnick != null) {
                  _mux.onQuit(origin,orgnick,text);
               }
            }

            else if (command.equals("NICK")) {
               String channel = ((_tok)tokens.elementAt(2)).token;
               String orgnick = parseOrgnick(origin); 
               if ( orgnick != null) {
                  _mux.onNick(origin,orgnick,channel.substring(1));
               }
            }

            else if (command.equals("TOPIC")) {
               String channel = ((_tok)tokens.elementAt(2)).token;
               String topic = (((_tok)tokens.elementAt(3)).token).substring(1);
               _mux.onTopic(channel,topic);
				}

            else if (command.equals("MSG")) {
               _mux.onMessage(message);
				}

            //--------------------------------------------------------------------------
            // MESSAGE SWITCH. Next handle replies and errors. Parse the reply arguments 
            // and pass them off to the appropriate handler method.            
            //
            else {

               // Parse command ID into integer
               int cmdid = -1;
               try {cmdid = Integer.parseInt(command);}
               catch (Exception e) {
                  cmdid = -1;
                  _mux.onParsingError(message);
                  continue;
               }

               switch (cmdid) {

                  case RPL_VERSION:
                     _mux.onReplyVersion(((_tok)tokens.elementAt(3)).token);
                     break;

                  case RPL_LUSERCHANNELS:
                     int channelCount = 0;
                     try {
                        channelCount = Integer.parseInt(((_tok)tokens.elementAt(3)).token);
                        _mux.onReplyListUserChannels(channelCount);
                     }
                     catch (Exception e) {
                        _mux.onParsingError(message);
                     }
                     break;
                  
                  case RPL_LISTSTART:
                     _mux.onReplyListStart();
                     break;

                  case RPL_LIST:
                     // "<channel> <# visible> :<topic>"
                     String channel = ((_tok)tokens.elementAt(3)).token;
                     int userCount = 0;
                     try {userCount = Integer.parseInt(((_tok)tokens.elementAt(4)).token);}
                     catch (Exception e) {}
                     String topic = message.substring(((_tok)tokens.elementAt(5)).index+1);
                     _mux.onReplyList(channel,userCount,topic);
                     break;

                  case RPL_LISTEND:
                     _mux.onReplyListEnd();
                     break;

                  case RPL_LUSERCLIENT: {
                        String msg = message.substring(((_tok)tokens.elementAt(3)).index+1);
                        _mux.onReplyListUserClient(msg);
                        break;
                     }

                  case RPL_WHOISUSER:
                     // "<nick> <user> <host> * :<real name>"
                     String user = ((_tok)tokens.elementAt(3)).token;
                     String rest = message.substring(((_tok)tokens.elementAt(4)).index);
                     _mux.onReplyWhoIsUser(user,rest);
                     break;

                  case RPL_WHOISSERVER:
                     // "<nick> <server> :<server info>"
                     String info = message.substring(((_tok)tokens.elementAt(3)).index);
                     _mux.onReplyWhoIsServer(info);
                     break;

                  case RPL_WHOISOPERATOR:
                     // "<nick> :is an IRC operator"
                     String opmsg = message.substring(((_tok)tokens.elementAt(3)).index);
                     _mux.onReplyWhoIsOperator(opmsg);
                     break;

                  case RPL_WHOISIDLE:
                     // "<nick> <integer> :seconds idle"
                     String secs = ((_tok)tokens.elementAt(3)).token;
                     _mux.onReplyWhoIsIdle("Idle for "+secs);
                     break;

                  case RPL_ENDOFWHOIS:
                     // "<nick> :End of /WHOIS list"
                     _mux.onReplyEndOfWhoIs();
                     break;

                  case RPL_WHOISCHANNELS:
                     // "<nick> :{[@|+]<channel><space>}"
                     String chans = message.substring(((_tok)tokens.elementAt(4)).index);
                     String orgnick = parseOrgnick(origin); 
                     _mux.onStatus("On channels "+chans.substring(1));
                     _mux.onReplyWhoIsChannels(orgnick,chans.substring(1));
                     break;

                  case RPL_MOTDSTART:
                     // Assume that MOTD indicates we are connected,   
                     // registered and ready to start chatting.
                     _mux.onConnect();
                     _mux.onReplyMOTDStart();
                     break;

                  case RPL_MOTD: {
                     String msg = message.substring(((_tok)tokens.elementAt(3)).index);
                     _mux.onReplyMOTD(msg);
                     break;
                  }

                  case RPL_ENDOFMOTD: {
                     String msg = message.substring(((_tok)tokens.elementAt(3)).index);
                     _mux.onReplyMOTDEnd();
                     break;
                  }

                  case RPL_TOPIC: {
                     String chan1 = ((_tok)tokens.elementAt(3)).token;
                     String top = message.substring(((_tok)tokens.elementAt(4)).index).substring(1);
                     _mux.onReplyTopic(chan1,top);
                     break;
                  }
                  case RPL_NAMREPLY: 
                     // "<channel> :[[@|+]<nick> [[@|+]<nick> [...]]]"
                     String chn = ((_tok)tokens.elementAt(4)).token;
                     String users = message.substring(((_tok)tokens.elementAt(5)).index);
                     RCTest.println("Channel "+chn+" Users ("+users.substring(1)+")\n");
                     _mux.onReplyNameReply(chn,users.substring(1));
                     break;

                  case ERR_NOMOTD:
                     _mux.onConnect();
                     _mux.onErrorNoMOTD();
                     break;

 					   case ERR_NONICKNAMEGIVEN:
						   _mux.onErrorNoNicknameGiven();
                     break;

 					   case ERR_NEEDMOREPARAMS:
						   _mux.onErrorNeedMoreParams();
                     break;

						case ERR_NICKNAMEINUSE:
                     _mux.onErrorNickNameInUse();
                     break;

						case ERR_NICKCOLLISION:
						   _mux.onErrorNickCollision();
						   break;

						case ERR_ERRONEUSNICKNAME:
                     _mux.onErrorErroneusNickname();
						   break;

						case ERR_ALREADYREGISTRED: 
						   _mux.onErrorAlreadyRegistered();
						   break;

                  // Some IRC servers use 001 - 004 for welcome message
                  case 001:
                  case 002:
                  case 003:
                  case 004:
                     // Welcome message indicates that we are connected.
                     _mux.onConnect();
                     if (tokens.size()>3) {
                        _mux.onStatus( message.substring(((_tok)tokens.elementAt(3)).index+1) );
                     }
                     break;

                  // Unsupported commands and replies
                  case RPL_LUSERME:
                  case RPL_ENDOFNAMES:
                  case 250:
                  case 252:
                  case 333:
                     _mux.onErrorUnsupported(message+"\n");
                     break;

                  // Unknown command or reply
	               default:
                     _mux.onErrorUnknown(message+"\n");
                     break;
               }
            }
         }
      } 
      catch (Exception e) {
		   if (getState() != DISCONNECTING) {
	         e.printStackTrace();
            _mux.onStatus("Closing connection dues to exception.");
			}
      }
      close();  
   }
   //------------------------------------------------------------------
   /** Write directly to the IRC chat server, refer to RFC-1459 for valid commands. */
   public void writeln(String message) {
      try {_outputStream.writeBytes(message+"\r\n");}catch (Exception e){}
   }  
    
   ////////////////////////////////////////////////////////////////////////////////////
   
   private class _IRCConnectionMux extends IRCConnectionAdapter {

      public void onAction( String user, String chan, String txt ) {
         _listener.onAction( user, chan, txt );
      }
      public void onBan( String banned, String chan, String banner ) {
         _listener.onBan( banned, chan, banner );
      }
      public void onClientInfo(String orgnick) {
         _listener.onClientInfo(orgnick);
      }
      public void onClientSource(String orgnick) {
         _listener.onClientSource(orgnick);
      }
      public void onClientVersion(String orgnick) {
         _listener.onClientVersion(orgnick);
      }
      public void onConnect() { 
         if (getState() != CONNECTED) {
			   setState(CONNECTED);
            onStatus("Connected to server ["+_server+":"+_port+"].\n");
            _listener.onConnect() ;
         }
      }
      public void onDisconnect() {
         _listener.onDisconnect();
      }
      public void onJoin( String user, String nick, String chan, boolean create ) {
         _listener.onJoin( user, nick, chan, create );
      }
      public void onJoins( String users, String chan) {
         _listener.onJoins( users, chan);
      }
      public void onKick( String kicked, String chan, String kicker, String txt ) {
         _listener.onKick( kicked, chan, kicker, txt );
      }
      public void onMessage(String message) {
         _listener.onMessage(message);
      }
      public void onPrivateMessage(String orgnick, String chan, String txt) {
         _listener.onPrivateMessage(orgnick, chan, txt);
      }
      public void onNick( String user, String oldnick, String newnick ) {
         _listener.onNick( user, oldnick, newnick );
      }
      public void onNotice(String text) {
         _listener.onNotice(text);
      }
      public void onPart( String user, String nick, String chan ) {
         _listener.onPart( user, nick, chan );
      }
      public void onOp( String oper, String chan, String oped ) {
         _listener.onOp( oper, chan, oped );
      }
      public void onParsingError(String message) {
         _listener.onParsingError(message);
      }
      public void onPing(String params) {
         _listener.onPing(params);
      }
      public void onStatus(String msg) {
         _listener.onStatus(msg);
      }
      public void onTopic(String chanName, String newTopic) {
         _listener.onTopic(chanName, newTopic);
      }
      public void onVersionNotice(String orgnick, String origin, String version) {
         _listener.onVersionNotice(orgnick, origin, version);
      }
      public void onQuit( String user, String nick, String txt ) {
         _listener.onQuit( user, nick, txt );
      }
      public void onReplyVersion(String version) {
         _listener.onReplyVersion(version);
      }
      public void onReplyListUserChannels(int channelCount) {
         _listener.onReplyListUserChannels(channelCount);
      }
      public void onReplyListStart() {
         _listener.onReplyListStart();
      }
      public void onReplyList(String channel, int userCount, String topic) {
         _listener.onReplyList(channel, userCount, topic);
      }
      public void onReplyListEnd() {
         _listener.onReplyListEnd();
      }
      public void onReplyListUserClient(String msg) {
         _listener.onReplyListUserClient(msg);
      }
      public void onReplyWhoIsUser(String userName, String miscText) {
         _listener.onReplyWhoIsUser(userName, miscText);
      }
      public void onReplyWhoIsServer(String info) {
         _listener.onReplyWhoIsServer(info);
      }
      public void onReplyWhoIsOperator(String info) {
         _listener.onReplyWhoIsOperator(info);
      }
      public void onReplyWhoIsIdle(String info) {
         _listener.onReplyWhoIsIdle(info);
      }
      public void onReplyEndOfWhoIs() {
         _listener.onReplyEndOfWhoIs();
      }
      public void onReplyWhoIsChannels(String nick, String channels) {
         _listener.onReplyWhoIsChannels(nick, channels);
      }
      public void onReplyMOTDStart() {
         _listener.onReplyMOTDStart();
      }
      public void onReplyMOTD(String msg) {
         _listener.onReplyMOTD(msg);
      }
      public void onReplyMOTDEnd() {
         _listener.onReplyMOTDEnd();
      }
      public void onReplyNameReply(String channel, String users) {
         _listener.onReplyNameReply(channel, users);
      }
      public void onReplyTopic(String channel, String topic) {
         _listener.onReplyTopic(channel, topic);
      }
      public void onErrorNoMOTD() {
         _listener.onErrorNoMOTD();
      }
      public void onErrorNeedMoreParams() {
         _listener.onErrorNeedMoreParams();
      }
      public void onErrorNoNicknameGiven() {
         _listener.onErrorNoNicknameGiven();
      }
      public void onErrorNickNameInUse() {
         _listener.onErrorNickNameInUse(_nick);
      }
      public void onErrorNickCollision() {
         _listener.onErrorNickCollision(_nick);
      }
      public void onErrorErroneusNickname() {
         _listener.onErrorErroneusNickname(_nick);
      }
      public void onErrorAlreadyRegistered() {
         _listener.onErrorAlreadyRegistered();
      }
      public void onErrorUnknown(String message) {
         _listener.onErrorUnknown(message);
      }
      public void onErrorUnsupported(String message) {
         _listener.onErrorUnsupported(message);
      }
   }
}
