/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import java.util.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import se.rebootit.android.tagbiljetter.models.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
 
public class Order extends Activity implements OnClickListener
{
	ArrayList<TransportCompany> lstCompanies = new ArrayList<TransportCompany>();
	ListAdapter adapter = new OrderCompanyListAdapter(this.lstCompanies, this);
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);
		
		//((Button)findViewById(R.id.btnVasttrafik)).setOnClickListener(this);
		
		TransportCompany transportCompany;

		transportCompany = new DefaultTransportCompany("SL-jävlarna");
		transportCompany.setLogo(R.drawable.logo_sl);
		lstCompanies.add(transportCompany);
		transportCompany = new DefaultTransportCompany("Värmlandstrafiken");
		lstCompanies.add(transportCompany);
		transportCompany = new DefaultTransportCompany("Västtrafik");
		transportCompany.setLogo(R.drawable.logo_vasttrafik);
		lstCompanies.add(transportCompany);
		
		
		ListView list = (ListView)findViewById(R.id.companylist);
		list.setAdapter(adapter);
	}
	
	public void onClick(View v)
	{
/*
		Intent intent = new Intent(this, OrderOptions.class);
		switch(v.getId())
		{
			case R.id.btnVasttrafik:
				//intent.putExtra("provider", TicketLoader.PROVIDER_VASTTRAFIK);
				startActivity(intent);

				break;
		}
*/
	}
}
