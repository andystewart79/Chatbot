
/* 
 * FILE: User.java 
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
import java.io.Serializable;

/** 
 * Represent a user that has been added to the favorites collection.
 * TODO: Perhaps a user object should query/listen-to the chat engine
 * to determine if user is online and which channels the user is on.
 * TODO: Perhaps a user object should support message, kick, ban and
 * other methods. 
 * TODO: Should provide property change support. 
 */
public class User implements Serializable {
   static final long serialVersionUID = -3629822682453945180L;

	private String _name;

   /** Construct user object for user specified by nick. */
   public User(String name) {
      _name = name;
   }
	/** Get user nick. */
	public String getName() {return _name;}

	/** Set user nick. */
   public void setName(String name) {_name=name;}

   /** String representation for display purposes. */
   public String toString() {return _name;}

   /*
   // Ideas
   public boolean isOnline() {}
   public boolean message(String message){}
   public void version() {}
   public void whois() {}
   public void ban() {}
   public void kick() {}
   */
}
