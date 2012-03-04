
/* 
 * FILE: ChatEngineEvent.java 
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
import java.util.EventObject;

///////////////////////////////////////////////////////////////////////
/**
 * Event fired by a ChatEngine. Has either a Channel object, a Server
 * object, a string status message or no value; other fields will be null.
 * @author David M. Johnson.
 */
public class ChatEngineEvent extends EventObject {

	private Channel _channel = null;
	private Server _server = null;
	private String _message = null;

   //------------------------------------------------------------------
	/** Event with no associated value. */ 
	public ChatEngineEvent(ChatEngine src) {
		super(src);
	}
   //------------------------------------------------------------------
	/** Event associated with a channel. */ 
	public ChatEngineEvent(ChatEngine src, Channel channel) {
		super(src);
		_channel = channel;
	}
   //------------------------------------------------------------------
	/** Event associated with server. */
	public ChatEngineEvent(ChatEngine src, Server server) {
		super(src);
		_server = server;
	}
   //------------------------------------------------------------------
	/** 
	 * Event associated with status message. ChatEngine sends out
    * status messages as events. 
    */
	public ChatEngineEvent(ChatEngine src, String message) {
		super(src);
		_message = message;
	}
   //------------------------------------------------------------------

	/** Get associated Channel object, or null if not applicable. */ 
	public Channel getChannel() {return _channel;}
	
	/** Get associated Server object, or null if not applicable. */ 
	public Server getServer() {return _server;}
	
	/** Get associated message, or null if not applicable. */ 
	public String getMessage() {return _message;}
}

