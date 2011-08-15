/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.models;

import java.util.*;

public abstract class TransportCompany
{
    private int id;
    private int logo;
    private String name;
    private String phonenumber;

    private List<TicketType> types;
    private List<TransportArea> areas;

    public abstract String getMessage(TransportArea area, TicketType type);
    public abstract boolean checkMessage(String phonenumber, String message);
    
	public abstract void addTicketType(TicketType type);
	public abstract void addTransportArea(TransportArea area);
	
	public abstract void setId(int id);
	public abstract int getId();
	
	public abstract void setLogo(int logo);
	public abstract int getLogo();
	
	public abstract void setName(String name);
	public abstract String getName();
	
	public abstract void setPhoneNumber(String phonenumber);
	public abstract String getPhoneNumber();
}
