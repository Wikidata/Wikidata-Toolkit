package org.wikidata.wdtk.wikibaseapi;

import java.util.Date;

/**
 * Simple class for saving recent changes
 * 
 * @author Markus Damm
 *
 */

class RecentChange implements Comparable<RecentChange> {

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
	 * Constructor
	 * 
	 * @param propertyName
	 *                name of the changed property
	 * @param date
	 *                date of the recent change
	 * @param author
	 *                name of the author of the recent change
	 */
	RecentChange(String propertyName, Date date, String author) {
		this.propertyName = propertyName;
		this.date = date;
		this.author = author;
	}

	/**
	 * Returns the author of the recent change
	 * 
	 * @return name (if user is registered) or the ip adress (if user is
	 *         unregistered) of the author of the recent change
	 */
	String getAuthor() {
		return author;
	}

	/**
	 * Returns the name of the changed property
	 * 
	 * @return name of the recently changed property
	 */
	String getPropertyName() {
		return propertyName;
	}

	/**
	 * Returns the date of the recent change
	 * 
	 * @return date of the recent change
	 */
	Date getDate() {
		return date;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof RecentChange) {
			RecentChange o = (RecentChange) other;
			if (this.propertyName.equals(o.propertyName)
					&& (this.date.equals(o.date))
					&& (this.author.equals(o.author))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(RecentChange other) {
		if (this.date.after(other.date)) {
			return 1;
		}
		if (this.date.before(other.date)) {
			return -1;
		}
		return 0;
	}
}
