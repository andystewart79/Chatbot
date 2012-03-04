
/* 
 * FILE: IChatEngine.java 
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

///////////////////////////////////////////////////////////////////////

/**
 * Interface for a chat engine. Where possible, use it instead of ChatEngine.
 * @see org.relayirc.chatengine.ChatEngine
 * @see org.relayirc.chatengine.ChatEngineListener
 * @author David M. Johnson
 */
public interface IChatEngine {

   /** Add a chat engine listener. */
   public void addChatEngineListener(ChatEngineListener listener);

   /** Remove a chat engine listener. */
   public void removeChatEngineListener(ChatEngineListener listener);
   
   /** Connect to server specified by current chat-options. */
   abstract public void connect();

   /** Disconnect from server. */
   abstract public void disconnect();

   /** Check connection status. */
   abstract public boolean isConnected();

   /** Check connection status. */
   abstract public boolean isConnecting();

   /** Fire status event to listeners. */
   abstract public void fireStatusEvent(String statusMsg);

   /** Get nick name currently in use. */
   abstract public String getNick();

   /** Process input from user. */
   abstract public void processUserInput(String txt, String chan);
   
   /** Send command string directly to server. */
   abstract public void sendCommand(String cmd);
   
   /** Send join message to server for specified channel. */
   abstract public void sendJoin(Channel chan);

   /** Send join message to server for specified channel. */
   abstract public void sendJoin(String chan);

   /** Send private message to server. */
   abstract public void sendMessage(String msg, String chan);

   /** Send version information to server. */
   abstract public void sendVersion(String msg);

   /** Send parting message to server. */
   abstract public void sendPart(Channel chan);

   /** Send parting message to server. */
   abstract public void sendPart(String chan);

   /** Send quit message to server. */
   abstract public void sendQuit(String msg);

   /** Start a channel search. */ 
   abstract public void startChannelSearch(ChannelSearch search);

   /** Get app name to be reported to version queries. */ 
   public String getAppName();

   /** Set app name to be reported to version queries. */ 
   public void setAppName(String name);
   
   /** Get app version to be reported to version queries. */ 
   public String getAppVersion();

   /** Set app verion to be reported to version queries. */ 
   public void setAppVersion(String version);
}

