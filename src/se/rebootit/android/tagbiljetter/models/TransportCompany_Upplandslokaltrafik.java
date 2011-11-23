/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.models;

import java.text.*;
import java.util.*;

import android.os.*;

public class TransportCompany_Upplandslokaltrafik extends TransportCompany implements Parcelable
{
	String[] months = new String[] { "jan", "feb", "mar", "apr", "maj", "jun", "jul", "aug", "sep", "okt", "nov", "dec" };
	
	public TransportCompany_Upplandslokaltrafik() { };
	
	public TransportCompany_Upplandslokaltrafik(String name, String phonenumber) {
		super(name, phonenumber);
	}
	
	public long getTicketTimestamp(String message) {
		String[] data = getMessageParts(message);

		if (data[0] != null && data[1] != null) {
			try {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data[0]+" "+data[1]).getTime();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return 0;
	}
	
	private TransportCompany_Upplandslokaltrafik(Parcel in) {
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

	public static final Parcelable.Creator<TransportCompany_Upplandslokaltrafik> CREATOR = new Parcelable.Creator<TransportCompany_Upplandslokaltrafik>() {
		public TransportCompany_Upplandslokaltrafik createFromParcel(Parcel in) {
			return new TransportCompany_Upplandslokaltrafik(in);
		}

		public TransportCompany_Upplandslokaltrafik[] newArray(int size) {
			return new TransportCompany_Upplandslokaltrafik[size];
		}
	};

	public int describeContents() {
		return 0;
	}
}
