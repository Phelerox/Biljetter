/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.models;

import java.util.*;

import android.os.*;

public class DefaultTransportCompany extends TransportCompany implements Parcelable
{
	public DefaultTransportCompany() { }
	
	public DefaultTransportCompany(String name, String phonenumber) {
		super(name, phonenumber);
	}

	private DefaultTransportCompany(Parcel in) {
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

	public static final Parcelable.Creator<DefaultTransportCompany> CREATOR = new Parcelable.Creator<DefaultTransportCompany>() {
		public DefaultTransportCompany createFromParcel(Parcel in) {
			return new DefaultTransportCompany(in);
		}

		public DefaultTransportCompany[] newArray(int size) {
			return new DefaultTransportCompany[size];
		}
	};

	public int describeContents() {
		return 0;
	}
}
