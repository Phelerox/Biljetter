/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.models;

import java.util.*;

import android.os.*;

public class TransportCompany_SL extends TransportCompany implements Parcelable
{
	private int id;
	private int logo;
	private String name;
	private String phonenumber;

	private List<TransportArea> areas = new ArrayList<TransportArea>();
	private List<TicketType> types = new ArrayList<TicketType>();

	public TransportCompany_SL(String name, String phonenumber) {
		this.name = name;
		this.phonenumber = phonenumber;
	}

	public String getMessage(TransportArea area, TicketType type) {
		return type.getCode()+area.getCode();
	}

	public boolean checkMessage(String phonenumber, String message) { return false; }

	public void addTicketType(TicketType type) { types.add(type); }
	public List<TicketType> getTicketTypes() { return types; }
	
	public void addTransportArea(TransportArea area) { areas.add(area); }
	public List<TransportArea> getTransportAreas() { return areas; }
	
	public void setId(int id) { this.id = id; }
	public int getId() { return this.id; }
	
	public void setLogo(int logo) { this.logo = logo; }
	public int getLogo() { return this.logo; }
	
	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }
	
	public void setPhoneNumber(String phonenumber) { this.phonenumber = phonenumber; }
	public String getPhoneNumber() { return this.phonenumber; }
	
	private TransportCompany_SL(Parcel in) {
		this.id = in.readInt();
		this.logo = in.readInt();
		this.name = in.readString();
		this.phonenumber = in.readString();
		in.readTypedList(areas, TransportArea.CREATOR);
		in.readTypedList(types, TicketType.CREATOR);
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.id);
		out.writeInt(this.logo);
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
