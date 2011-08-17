/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.models;

import java.util.*;

import android.os.*;

public class TransportCompany_SL extends TransportCompany implements Parcelable
{
	public TransportCompany_SL() { };
	
	public TransportCompany_SL(String name, String phonenumber) {
		super(name, phonenumber);
	}
	
	public String getMessage(TransportArea area, TicketType type) {
		return type.getCode()+area.getCode();
	}

	public boolean checkMessage(String phonenumber, String message) { return false; }

	private TransportCompany_SL(Parcel in) {
		this.id = in.readInt();
		this.logo = in.readString();
		this.name = in.readString();
		this.phonenumber = in.readString();
		in.readTypedList(areas, TransportArea.CREATOR);
		in.readTypedList(types, TicketType.CREATOR);
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.id);
		out.writeString(this.logo);
		out.writeString(this.name);
		out.writeString(this.phonenumber);
		out.writeTypedList(this.areas);
		out.writeTypedList(this.types);
	}

	public static final Parcelable.Creator<TransportCompany_SL> CREATOR = new Parcelable.Creator<TransportCompany_SL>() {
		public TransportCompany_SL createFromParcel(Parcel in) {
			return new TransportCompany_SL(in);
		}

		public TransportCompany_SL[] newArray(int size) {
			return new TransportCompany_SL[size];
		}
	};

	public int describeContents() {
		return 0;
	}
}
