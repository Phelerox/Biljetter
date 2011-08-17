/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import java.util.*;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */

public class TicketListAdapter extends BaseAdapter
{
	private List<Ticket> lstTickets;
	private Context context;
	private HashMap<Integer, Boolean> providers = new HashMap<Integer, Boolean>();
 
	public TicketListAdapter(List<Ticket> lstTickets, Context context) {
		this.lstTickets = lstTickets;
		this.context = context;
	}
 
	public int getCount() {
		return lstTickets.size();
	}
 
	public Ticket getItem(int position) {
		return lstTickets.get(position);
	}
 
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout itemLayout;
		Ticket ticket = lstTickets.get(position);

/*
		if (this.providers.containsKey(ticket.getProvider()) && this.providers.get(ticket.getProvider()))
		{
*/
			itemLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.ticketlistitem, parent, false);
			
			TextView txtType = (TextView)itemLayout.findViewById(R.id.type);
			TextView txtDate = (TextView)itemLayout.findViewById(R.id.date);
			txtType.setText(DataParser.getCompanyName(ticket.getProvider()));
			txtDate.setText(ticket.getTicketTimestampFormatted());
			
			return itemLayout;
/*
		}
		else
		{
			itemLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.no_row, parent, false);
			itemLayout.setVisibility(LinearLayout.GONE);
			return itemLayout;
		}
*/
	}
	
	public void setProvider(int key, boolean value) {
		providers.put(key, value);
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
