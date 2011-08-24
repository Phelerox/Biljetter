/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import java.util.*;
import java.io.*;
import java.text.*;

import android.os.*;
import android.util.*;

/**
 * Ticket is a class that holds all the information about the trip.
 * @author Erik Fredriksen <erik@fredriksen.se>
 */

public class Ticket implements Parcelable, Serializable, Comparable<Ticket>
{
	private static final long serialVersionUID = 7428325774966936563L;
	
	private int provider = 0;
	private long timestamp = 0;
	private long tickettimestamp = 0;
	private String timestampformatted = "";
	private String tickettimestampformatted = "";
	private String address = "";
	private String message = "";

	/**
	 * Creates an empty Ticket object
	 */
	public Ticket() { }
	
	
	/**
	 * Creates an empty Ticket object
	 * @param	address		The address that the text message arrived from.
	 * @param	timestamp	The timestamp when the text arrived.
	 */
	public Ticket(String address, String timestamp) {
		this(address, Long.parseLong(timestamp));
	}
	
	/**
	 * Creates an empty Ticket object
	 * @param	address		The address that the text message arrived from.
	 * @param	timestamp	The timestamp when the text arrived.
	 */
	public Ticket(String address, long timestamp) {
		this.address = address;
		this.timestamp = timestamp;
	}
	
	/**
	 * Set the arrival time of the SMS.
	 * @param	timestamp	The sms timestamp as a long
	 */
	public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
	
	/**
	 * @return Timestamp of arrival of the SMS.
	 */
	public long getTimestamp() { return this.timestamp; }
	
	/**
	 * @return Formatted string with the arrival of the SMS.
	 */
	public String getTimestampFormatted() {
		if ("".equals(this.timestampformatted) && this.timestamp > 0) {
			this.timestampformatted = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(this.timestamp));
		}
		return this.timestampformatted;
	}
	
	public void setTicketTimestamp(long tickettimestamp) { this.tickettimestamp = tickettimestamp; }
	public long getTicketTimestamp() { return this.tickettimestamp; }
	public String getTicketTimestampFormatted() {
		if ("".equals(this.tickettimestampformatted) && this.tickettimestamp > 0) {
			this.tickettimestampformatted = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(this.tickettimestamp));
		}
		return this.tickettimestampformatted;
	}
	public void setTicketTimestampFormatted(String tickettimestampformatted) { this.tickettimestampformatted = tickettimestampformatted; }
	
	/**
	 * Sets the address where the sms originated from.
	 * @param	address		The address of the sender
	 */
	public void setAddress(String address) { this.address = address; }
	public String getAddress() { return this.address; }
	
	/**
	 * Sets the content of the message.
	 * @param	message		The content
	 */
	public void setMessage(String message) { this.message = message; }
	public String getMessage() { return this.message; }
	
	
	public void setProvider(int provider) { this.provider = provider; }
	public int getProvider() { return this.provider; }
	
	
	
	public int compare(Ticket ticket, Ticket ticket2) {
		if (((Ticket)ticket).getTimestamp() > ((Ticket)ticket2).getTimestamp()) {
			return 1;
		} else if (((Ticket)ticket).getTimestamp() < ((Ticket)ticket2).getTimestamp()) {
			return -1;
		} else {
			return 0;
		}
	}

	public int compareTo(Ticket ticket) {
		if (((Ticket)ticket).getTimestamp() > getTimestamp()) {
			return 1;
		} else if (((Ticket)ticket).getTimestamp() < getTimestamp()) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Ticket))
		return false;

		return (((Ticket)obj).hashCode() == hashCode() ? true : false);
	}

	@Override
	public int hashCode() {
		int code = 17;
		code = 31*code + (int)(timestamp ^ (timestamp >> 32));
		code = 31*code + address.hashCode();
		code = 31*code + message.hashCode();
		code = 31*code + provider;

		return code;
	}
	
	private Ticket(Parcel in) {
		this.provider = in.readInt();
		this.timestamp = in.readLong();
		this.tickettimestamp = in.readLong();
		this.timestampformatted = in.readString();
		this.tickettimestampformatted = in.readString();
		this.address = in.readString();
		this.message = in.readString();
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.provider);
		out.writeLong(this.timestamp);
		out.writeLong(this.tickettimestamp);
		out.writeString(this.timestampformatted);
		out.writeString(this.tickettimestampformatted);
		out.writeString(this.address);
		out.writeString(this.message);
	}

	// this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
	public static final Parcelable.Creator<Ticket> CREATOR = new Parcelable.Creator<Ticket>() {
		public Ticket createFromParcel(Parcel in) {
		return new Ticket(in);
		}

		public Ticket[] newArray(int size) {
			return new Ticket[size];
		}
	};

	// 99.9% of the time you can just ignore this
	public int describeContents() {
		return 0;
	}

}
