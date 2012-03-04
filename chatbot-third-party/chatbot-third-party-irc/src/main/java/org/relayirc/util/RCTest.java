
/* 
 * FILE: RCTest.java 
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

package org.relayirc.util;

/**
 * Provides debugging output capability.
 * @author David M. Johnson.
 */
public class RCTest {

   private static boolean _debug = false;

   //--------------------------------------------------------------------
   public static boolean isDebug() {
      return _debug;
   }
   //--------------------------------------------------------------------
   public static void setDebug(boolean flag) {
      _debug = flag;
      if (_debug) println("Debug is ON");
   }
   //--------------------------------------------------------------------
   public static void println(String msg) {
      if (_debug) {
         System.out.println(msg);
      }
   }
}
