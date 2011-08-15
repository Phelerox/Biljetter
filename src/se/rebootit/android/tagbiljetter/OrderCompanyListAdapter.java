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

import se.rebootit.android.tagbiljetter.models.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */

public class OrderCompanyListAdapter extends BaseAdapter
{
	private List<TransportCompany> lstCompanies;
	private Context context;
 
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
		
		ImageView imgLogo = (ImageView)itemLayout.findViewById(R.id.companylogo);
		TextView txtName = (TextView)itemLayout.findViewById(R.id.companyname);
		if (transportCompany.getLogo() > 0) {
			imgLogo.setImageResource(transportCompany.getLogo());
		}
		txtName.setText(transportCompany.getName());
		
		return itemLayout;
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
