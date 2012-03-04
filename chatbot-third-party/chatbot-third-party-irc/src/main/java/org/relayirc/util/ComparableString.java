
package org.relayirc.util;

///////////////////////////////////////////////////////////////////////////

/** 
 * Sortable string that implements IComparable. 
 * @see org.relayirc.util.IComparable 
 */ 
public class ComparableString implements IComparable {
   private String _str = null;
	public ComparableString(String str) {
		_str = str;
	}
	public int compareTo(IComparable other) {
		if (other instanceof ComparableString) {
			ComparableString compString = (ComparableString)other;
			String otherString = (String)compString.getString();
			return _str.compareTo(otherString);
		}
		else return -1;
	}
	public String getString() {return _str;}
	public void setString(String str) {_str = str;}
}
