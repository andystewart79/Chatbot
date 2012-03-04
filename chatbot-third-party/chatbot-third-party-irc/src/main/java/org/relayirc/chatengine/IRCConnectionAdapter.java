/*
 * FILE: IRCConnectionListener.java
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

/** 
 * <p>Do-nothing implementation of IRCConnectionListener to make it easy
 * to derive new connection listeners.</p>
 *
 * @see org.relayirc.chatengine.IRCConnection
 * @see org.relayirc.chatengine.IRCConnectionListener
 * @author David M. Johnson
 */
public class IRCConnectionAdapter implements IRCConnectionListener {
   public void onAction( String user, String chan, String txt ) {}
   public void onBan( String banned, String chan, String banner ) {}
   public void onClientInfo(String orgnick) {}
   public void onClientSource(String orgnick) {}
   public void onClientVersion(String orgnick) {}
   public void onConnect()  {}
   public void onDisconnect() {}
   public void onJoin( String user, String nick, String chan, boolean create ) {}
   public void onJoins( String users, String chan) {}
   public void onKick( String kicked, String chan, String kicker, String txt ) {}
   public void onMessage(String message) {}
   public void onPrivateMessage(String orgnick, String chan, String txt) {}
   public void onNick( String user, String oldnick, String newnick ) {}
   public void onNotice(String text) {}
   public void onPart( String user, String nick, String chan ) {}
   public void onOp( String oper, String chan, String oped ) {}
   public void onParsingError(String message) {}
   public void onPing(String params) {}
   public void onStatus(String msg) {}
   public void onTopic(String chanName, String newTopic) {}
   public void onVersionNotice(String orgnick, String origin, String version) {}
   public void onQuit( String user, String nick, String txt ) {}
   public void onReplyVersion(String version) {}
   public void onReplyListUserChannels(int channelCount) {}
   public void onReplyListStart() {}
   public void onReplyList(String channel, int userCount, String topic) {}
   public void onReplyListEnd() {}
   public void onReplyListUserClient(String msg) {}
   public void onReplyWhoIsUser(String userName, String miscText) {}
   public void onReplyWhoIsServer(String info) {}
   public void onReplyWhoIsOperator(String info) {}
   public void onReplyWhoIsIdle(String info) {}
   public void onReplyEndOfWhoIs() {}
   public void onReplyWhoIsChannels(String nick, String channels) {}
   public void onReplyMOTDStart() {}
   public void onReplyMOTD(String msg) {}
   public void onReplyMOTDEnd() {}
   public void onReplyNameReply(String channel, String users) {}
   public void onReplyTopic(String channel, String topic) {}

   public void onErrorNoMOTD() {}
   public void onErrorNeedMoreParams() {}
   public void onErrorNoNicknameGiven() {}
   public void onErrorNickNameInUse(String badNick) {}
   public void onErrorNickCollision(String badNick) {}
   public void onErrorErroneusNickname(String badNick) {}
   public void onErrorAlreadyRegistered() {}
   public void onErrorUnknown(String message) {}
   public void onErrorUnsupported(String messag) {}
}
