package org.wikidata.wdtk.wikibaseapi;

import java.util.Date;

/**
 * Simple class for saving recent changes
 * 
 * @author Markus Damm
 *
 */

public class RecentChange implements Comparable<RecentChange> {
	/**
	 * author of the recent change
	 */
	private String author;

	/**
	 * property that was recently changed
	 */
	private String propertyName;

	/**
	 * date and time of the recent change
	 */
	private Date date;

	/**
	 * Returns the author of the recent change
	 * 
	 * @return name (if user is registered) or the ip adress (if user is
	 *         unregistered) of the author of the recent change
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Returns the name of the changed property
	 * 
	 * @return name of the recently changed property
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Returns the date of the recent change
	 * 
	 * @return date of the recent change
	 */
	public Date getDate() {
		return date;
	}

	@Override
	public int compareTo(RecentChange other) {
		if (this.date.after(other.date)) {
			return 1;
		}
		return 0;
	}
}