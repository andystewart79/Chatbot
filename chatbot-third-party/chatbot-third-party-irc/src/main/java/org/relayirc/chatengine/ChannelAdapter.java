
/* 
 * FILE: ChannelAdapter.java 
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
 * Provides a default do-nothing implementation of ChannelListener.
 * @author David M. Johnson
 */
public class ChannelAdapter implements ChannelListener {
   public void onActivation(ChannelEvent event) {}
   public void onAction(ChannelEvent event) {}
   public void onConnect(ChannelEvent event) {}
   public void onDisconnect(ChannelEvent event) {}
   public void onMessage(ChannelEvent event) {}
   public void onJoin(ChannelEvent event) {}
   public void onJoins(ChannelEvent event) {}
   public void onPart(ChannelEvent event) {}
   public void onBan(ChannelEvent event) {}
   public void onKick(ChannelEvent event) {}
   public void onNick(ChannelEvent event) {}
   public void onOp(ChannelEvent event) {}
   public void onQuit(ChannelEvent event) {} 
   public void onTopicChange(ChannelEvent event) {}
}

