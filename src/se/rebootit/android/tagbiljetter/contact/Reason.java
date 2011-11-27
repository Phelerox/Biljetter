/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.contact;

import java.util.*;
import java.io.*;
import java.text.*;

import android.os.*;
import android.util.*;

/**
 * Reason is a class that holds all the information about the trip.
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
public class Reason implements Parcelable
{
	String title;
	String content;
	String city;
	String line;
	String departureDate;
	String departureTime;
	
	public Reason(String title) {
		this.title = title;
	}
	
	public Reason(String title, String content) {
		this.title = title;
		this.content = content;
	}
	
	public String getTitle() { return this.title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getContent() { return this.content; }
	public void setContent(String content) { this.content = content; }
	
	public String getCity() { return this.city; }
	public void setCity(String city) { this.city = city; }
	
	public String getLine() { return this.line; }
	public void setLine(String line) { this.line = line; }
	
	public String getDepartureDate() { return this.departureDate; }
	public String getDepartureTime() { return this.departureTime; }
	public void setDeparture(String date, String time)
	{
		this.departureDate = date;
		this.departureTime = time;
	}
	
	private Reason(Parcel in) {
		this.title = in.readString();
		this.content = in.readString();
		this.city = in.readString();
		this.line = in.readString();
		this.departureDate = in.readString();
		this.departureTime = in.readString();
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.title);
		out.writeString(this.content);
		out.writeString(this.city);
		out.writeString(this.line);
		out.writeString(this.departureDate);
		out.writeString(this.departureTime);
	}

	// this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
	public static final Parcelable.Creator<Reason> CREATOR = new Parcelable.Creator<Reason>() {
		public Reason createFromParcel(Parcel in) {
		return new Reason(in);
		}

		public Reason[] newArray(int size) {
			return new Reason[size];
		}
	};

	// 99.9% of the time you can just ignore this
	public int describeContents() {
		return 0;
	}
}
