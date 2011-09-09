/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.models;

import java.text.*;
import java.util.*;
import java.util.regex.*;

import android.util.*;

import se.rebootit.android.tagbiljetter.*;

public abstract class TransportCompany
{
	protected int id;
	protected String name;
	protected String phonenumber;
	protected String logo;
	protected String ticketformat;

	protected List<TransportArea> areas = new ArrayList<TransportArea>();
	protected List<TicketType> types = new ArrayList<TicketType>();

	public TransportCompany() { }

	public TransportCompany(String name, String phonenumber) {
		this.name = name;
		this.phonenumber = phonenumber;
	}
	
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
	
	public String getMessage(TransportArea area, TicketType type) { return null; }
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
	
	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }
	
	public void setPhoneNumber(String phonenumber) { this.phonenumber = phonenumber; }
	public String getPhoneNumber() { return this.phonenumber; }
	
	public void setTicketFormat(String ticketformat) { this.ticketformat = ticketformat; }
	public String getTicketFormat() { return this.ticketformat; }
}
