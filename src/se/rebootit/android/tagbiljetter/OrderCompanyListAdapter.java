/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import java.util.*;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import se.rebootit.android.tagbiljetter.models.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */

public class OrderCompanyListAdapter extends BaseAdapter
{
	private List<TransportCompany> lstCompanies;
	private Context context;
	private int skipped = 0;
 
	public OrderCompanyListAdapter(List<TransportCompany> lstCompanies, Context context) {
		this.lstCompanies = lstCompanies;
		this.context = context;
	}
 
	public int getCount() {
		return lstCompanies.size();
	}
 
	public TransportCompany getItem(int position) {
		return lstCompanies.get(position);
	}
 
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout itemLayout;
		TransportCompany transportCompany = lstCompanies.get(position);

		itemLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.order_companylist, parent, false);
		
		if (transportCompany.getTransportAreaCount() > 0 && transportCompany.getTicketTypeCount() > 0)
		{
			ImageView imgLogo = (ImageView)itemLayout.findViewById(R.id.companylogo);
			TextView txtName = (TextView)itemLayout.findViewById(R.id.companyname);
			if (transportCompany.getLogo() != null) {
				try {
					Bitmap bMap = BitmapFactory.decodeStream(this.context.getAssets().open("logos/"+transportCompany.getLogo()));
					imgLogo.setImageBitmap(bMap);
					
				} catch (Exception e) { e.printStackTrace(); }
			}
			txtName.setText(transportCompany.getName());
			
			// Make sure we count the skipped items
			if (position == 0) { skipped = 0; }

			// Give even rows a background color
			if ((position-skipped) % 2 == 1) {
				itemLayout.setBackgroundColor(0xaa555555);
			}
			
			return itemLayout;
		}
		else
		{
			skipped++;
			itemLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.no_row, parent, false);
			itemLayout.setVisibility(LinearLayout.GONE);
			return itemLayout;
		}
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
