
/* 
 * FILE: ChatEngineAdapter.java 
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
 * Provides a default do-nothing implementation of ChatEngineListener
 * @author David M. Johnson.
 */
public class ChatEngineAdapter implements ChatEngineListener {
   public void onConnection(ChatEngineEvent event) {}
   public void onDisconnection(ChatEngineEvent event) {}
   public void onChannelJoin(ChatEngineEvent event) {}
   public void onChannelPart(ChatEngineEvent event) {}
   public void onStatus(ChatEngineEvent event) {}
}
