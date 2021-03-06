/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.models;

import java.text.*;
import java.util.*;

import android.os.*;

public class TransportCompany_SJ extends TransportCompany implements Parcelable
{
	String[] months = new String[] { "jan", "feb", "mar", "apr", "maj", "jun", "jul", "aug", "sep", "okt", "nov", "dec" };
	
	public TransportCompany_SJ() { };
	
	public TransportCompany_SJ(String name, String phonenumber) {
		super(name, phonenumber);
	}
	
	public long getTicketTimestamp(String message, long messagetime) {
		String[] data = getMessageParts(message);

		if (data[0] != null && data[1] != null) {
			try {
				String[] parts = data[0].split(" ");

				int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date(messagetime)));
				int month = (java.util.Arrays.asList(this.months).indexOf(parts[1])+1);
				int day = Integer.parseInt(parts[0]);

				String date = year+"-"+(month < 10 ? "0"+month : month)+"-"+(day < 10 ? "0"+day : day);
				
				return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(year+"-"+month+"-"+day+" "+data[1]).getTime();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return 0;
	}

	private TransportCompany_SJ(Parcel in) {
		this.id = in.readInt();
		this.logo = in.readString();
		this.headercolor = in.readString();
		this.name = in.readString();
		this.phonenumber = in.readString();
		in.readTypedList(areas, TransportArea.CREATOR);
		in.readTypedList(types, TicketType.CREATOR);
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.id);
		out.writeString(this.logo);
		out.writeString(this.headercolor);
		out.writeString(this.name);
		out.writeString(this.phonenumber);
		out.writeTypedList(this.areas);
		out.writeTypedList(this.types);
	}

	public static final Parcelable.Creator<TransportCompany_SJ> CREATOR = new Parcelable.Creator<TransportCompany_SJ>() {
		public TransportCompany_SJ createFromParcel(Parcel in) {
			return new TransportCompany_SJ(in);
		}

		public TransportCompany_SJ[] newArray(int size) {
			return new TransportCompany_SJ[size];
		}
	};

	public int describeContents() {
		return 0;
	}
}
