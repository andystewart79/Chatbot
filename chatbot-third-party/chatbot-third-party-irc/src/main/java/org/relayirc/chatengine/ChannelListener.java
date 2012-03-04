
/* 
 * FILE: ChannelListener.java 
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

///////////////////////////////////////////////////////////////////////

/**
 * Implement this interface to listen to channel events incuding 
 * messages, joins, parts, bans and kicks.
 * @see org.relayirc.chatengine.Channel
 * @author David M. Johnson
 */
public interface ChannelListener {

   /** Channel has been activated, given focus or brought-to-front. 
	 * Event includes no originating user, subject user or value. */
   abstract public void onActivation(ChannelEvent event);
   
   /** User has acted. Event includes origin nick, origin address and the 
	 * value is a string containing the text of the user's action. */
   abstract public void onAction(ChannelEvent event);

   /** Engine has connected/joined the channel. 
	 * Event includes no originating user, subject user or value. */
   abstract public void onConnect(ChannelEvent event);
   
   /** Engine has disconnected/parted the channel. 
	 * Event includes no originating user, subject user or value. */
   abstract public void onDisconnect(ChannelEvent event);
   
   /** A user has spoken. Event includes origin nick, origin address and the 
	 * value is a string containing the text of the user's message. */
   abstract public void onMessage(ChannelEvent event);
   
   /** A user has joined the channel. Event includes origin nick, 
	 * origin address and the value is a string containing the 
	 * text of the user's message. */
   abstract public void onJoin(ChannelEvent event);
   
   /** Multiple users have joined the channel. Event's value 
	 * is a String containing a list of user nick names who are 
	 * currently on a channel you just joined. */
   abstract public void onJoins(ChannelEvent event);
   
   /** A user has parted/left the channel. Event includes origin nick, 
	 * origin address. */
   abstract public void onPart(ChannelEvent event);
   
   /** An operator has banned a user from the channel. Event includes
	 * origin nick, origin address, subject nick, subject nick and subject
	 * address. The origin is the operator and the subject is the banned. */
   abstract public void onBan(ChannelEvent event);
   
   /** An operator has kicked a user from the channel. Event includes 
	 * origin nick, origin address, subject nick, subject nick and subject
	 * address. The origin is the operator and the subject is the kicked. */
   abstract public void onKick(ChannelEvent event);
   
   /** A user has changed nick names. Event includes origin nick, 
	 * origin address and the value is the origin's new nick name. */
   abstract public void onNick(ChannelEvent event);
   
   /** An operator has given a user operator rights. Event includes  
	 * origin nick, origin address, subject nick, subject nick and subject
	 * address. The origin is the operator and the subject is the reciever 
	 * of rights. */
   abstract public void onOp(ChannelEvent event);
   
   /** A user has quit/disconnected from the chat server. Event 
	 * includes origin nick, origin address and the value is the 
	 * origin's new nick name. */
   abstract public void onQuit(ChannelEvent event);
   
   /** Channel topic has changed. The event's value is a sting 
	 * containing the new topic. */
   abstract public void onTopicChange(ChannelEvent event);
}

