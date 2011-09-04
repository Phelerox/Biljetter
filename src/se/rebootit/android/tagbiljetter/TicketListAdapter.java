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
		Ticket ticket = lstTickets.get(position);

		LinearLayout itemLayout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.ticketlist_listitem, parent, false);
		
		TextView txtType = (TextView)itemLayout.findViewById(R.id.type);
		TextView txtDate = (TextView)itemLayout.findViewById(R.id.date);
		txtType.setText(DataParser.getCompanyName(ticket.getProvider()));
		txtDate.setText(ticket.getTicketTimestampFormatted());
		
		// Give even rows a background color
		if (position % 2 == 1) {
			itemLayout.setBackgroundColor(0x30558cd0);
		}
		
		return itemLayout;
	}
	
	public void setProvider(int key, boolean value) {
		providers.put(key, value);
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
