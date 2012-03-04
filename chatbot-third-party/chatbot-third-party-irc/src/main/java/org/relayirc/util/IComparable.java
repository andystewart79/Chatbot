
package org.relayirc.util;

/** 
 * Objects that implement this interface are sortable by QuickSort.
 * @see org.relayirc.util.QuickSort
 */
public interface IComparable {
	/**
	 *	Compare to other object. Works like String.compareTo()
	 */
	public int compareTo(IComparable c);
}
