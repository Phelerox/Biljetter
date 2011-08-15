/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.models;

import java.util.*;

public class DefaultTransportCompany extends TransportCompany
{
	private int id;
	private int logo;
	private String name;
	private String phonenumber;

	private List<TicketType> types = new ArrayList<TicketType>();
	private List<TransportArea> areas = new ArrayList<TransportArea>();

	public DefaultTransportCompany(String name)
	{
		this.name = name;
	}

	public String getMessage(TransportArea area, TicketType type)
	{
		return area.getCode()+type.getCode();
	}

	public boolean checkMessage(String phonenumber, String message) { return false; }

	public void addTicketType(TicketType type) { types.add(type); }
	public void addTransportArea(TransportArea area) { areas.add(area); }
	
	public void setId(int id) { this.id = id; }
	public int getId() { return this.id; }
	
	public void setLogo(int logo) { this.logo = logo; }
	public int getLogo() { return this.logo; }
	
	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }
	
	public void setPhoneNumber(String phonenumber) { this.phonenumber = phonenumber; }
	public String getPhoneNumber() { return this.phonenumber; }
}
