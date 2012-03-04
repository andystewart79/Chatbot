
/* 
 * FILE: ChannelSearchListener.java 
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
 * Inteface for listening to progress of a channel search. Implement
 * this interface to be notified of beginning of search, each channel
 * that is found to meet the search criteria and the end of the search.
 */
public interface ChannelSearchListener {

   /** Called when channel is found that meets search criteria. */
	public void searchFound(Channel chan);

   /** Called when seach begins. */
   public void searchStarted(int channels);

   /** Called when seach ends. */
   public void searchEnded();
}
