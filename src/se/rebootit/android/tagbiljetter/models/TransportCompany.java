/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */
package se.rebootit.android.tagbiljetter.models;

import android.os.*;
import android.util.*;

import java.text.*;
import java.util.*;
import java.util.regex.*;

import se.rebootit.android.tagbiljetter.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
public abstract class TransportCompany implements Parcelable
{
	protected int id;
	protected String name;
	protected String phonenumber;
	protected String email;
	protected String logo = null;
	protected String textcolor = "#000000";
	protected String ticketformat;

	protected List<TransportArea> areas = new ArrayList<TransportArea>();
	protected List<TicketType> types = new ArrayList<TicketType>();

	public TransportCompany() { }

	public TransportCompany(String name, String phonenumber) {
		this.name = name;
		this.phonenumber = phonenumber;
	}

	// Extract the validity of the ticket from the message
	public long getTicketTimestamp(String message) {
		String[] data = getMessageParts(message);

		if (data[0] != null && data[1] != null) {
			try {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data[1]+" "+data[0]).getTime();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return 0;
	}

	public long getTicketTimestamp(String message, long messagetime) {
		return getTicketTimestamp(message);
	}

	public String[] getMessageParts(String message)
	{
		String expr = getTicketFormat();

		Pattern pattern = Pattern.compile(expr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(message);

		String[] output = new String[matcher.groupCount()+1];

		while (matcher.find()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				output[i] = matcher.group(i+1);
			}
		}

		return output;
	}

	public String getMessage(TransportArea area, TicketType type) {
		return area.getCode()+type.getCode();
	}
	public boolean checkMessage(String phonenumber, String message) { return false; }

	public void addTransportArea(TransportArea area) { areas.add(area); }
	public List<TransportArea> getTransportAreas() { return areas; }
	public int getTransportAreaCount() { return areas.size(); }

	public void addTicketType(TicketType type) { types.add(type); }
	public List<TicketType> getTicketTypes() { return types; }
	public int getTicketTypeCount() { return areas.size(); }

	public void setId(int id) { this.id = id; }
	public int getId() { return this.id; }

	public void setLogo(String logo) { this.logo = logo; }
	public String getLogo() { return this.logo; }

	public void setTextColor(String textcolor) { this.textcolor = textcolor; }
	public String getTextColor() { return this.textcolor; }

	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }

	public void setPhoneNumber(String phonenumber) { this.phonenumber = phonenumber; }
	public String getPhoneNumber() { return this.phonenumber; }

	public void setEmail(String email) { this.email = email; }
	public String getEmail() { return this.email; }

	public void setTicketFormat(String ticketformat) { this.ticketformat = ticketformat; }
	public String getTicketFormat() { return this.ticketformat; }

	protected TransportCompany(Parcel in) {
		this.id = in.readInt();
		this.logo = in.readString();
		this.textcolor = in.readString();
		this.name = in.readString();
		this.phonenumber = in.readString();
		in.readTypedList(areas, TransportArea.CREATOR);
		in.readTypedList(types, TicketType.CREATOR);
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.id);
		out.writeString(this.logo);
		out.writeString(this.textcolor);
		out.writeString(this.name);
		out.writeString(this.phonenumber);
		out.writeTypedList(this.areas);
		out.writeTypedList(this.types);
	}
}
