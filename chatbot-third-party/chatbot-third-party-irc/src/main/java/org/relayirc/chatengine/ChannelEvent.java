
/* 
 * FILE: ChannelEvent.java 
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
 * Event fired by a channel. The source of a ChannelEvent is always 
 * a Channel object.
 * @see org.relayirc.chatengine.Channel
 * @see org.relayirc.chatengine.ChannelListener
 * @author David M. Johnson
 */
public class ChannelEvent extends EventObject {

   private String _originNick = null;
   private String _originAddress = null;
   private String _subjectNick = null;
   private String _subjectAddress = null;
   private String _newNick = null;
   private Object _value = null;

   //------------------------------------------------------------------
	/** Event with no associated values. */
   public ChannelEvent(Channel src) {
      super(src);
   }
   //------------------------------------------------------------------
	/** Event with an optional arbitrary value. */
   public ChannelEvent(Channel src, Object value) {
      super(src);
      _value = value;
   }
   //------------------------------------------------------------------
	/** Event with originating user and an optional arbitrary value. */
   public ChannelEvent(Channel src,
      String originNick, String originAddress, Object value) {

      super(src);
      _originNick = originNick;
      _originAddress = originAddress;
      _value = value;
   }
   //------------------------------------------------------------------
	/** Event with originating user, destination user and an optional 
	 * arbitrary value. */
   public ChannelEvent(Channel src, 
      String originNick, String originAddress,
      String subjectNick, String subjectAddress, Object value) {

      this(src,originNick,originAddress,value);
      _subjectNick = subjectNick;
      _subjectAddress = subjectAddress;
   }
   //------------------------------------------------------------------

	/** Nick name of the originating chat user or null if not applicable. */
   public String getOriginNick() {return _originNick;}

	/** Address of the originating chat user or null if not applicable. */
   public String getOriginAddress() {return _originAddress;}

	/** Nick name of the chat user or null if not applicable. */
   public String getSubjectNick() {return _subjectNick;}

	/** Address of the destination chat user or null if not applicable. */
   public String getSubjectAddress() {return _subjectAddress;}

	/** Arbitrary value associated with event. */ 
   public Object getValue() {return _value;}
}
