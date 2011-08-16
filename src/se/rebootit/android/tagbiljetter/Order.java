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
import android.widget.AdapterView.*;

import se.rebootit.android.tagbiljetter.models.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
 
public class Order extends Activity
{
	ArrayList<TransportCompany> lstCompanies = new ArrayList<TransportCompany>();
	ListAdapter adapter = new OrderCompanyListAdapter(this.lstCompanies, this);

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);

		TransportCompany transportCompany;

		// Storstockholms Lokaltrafik
		transportCompany = new TransportCompany_SL("SL", "72150");
		transportCompany.setLogo(R.drawable.logo_sl);
		transportCompany.addTransportArea(new TransportArea("A", "Zon A", ""));
		transportCompany.addTransportArea(new TransportArea("B", "Zon B", ""));
		transportCompany.addTransportArea(new TransportArea("C", "Zon C", ""));
		transportCompany.addTransportArea(new TransportArea("AB", "Zon A+B", ""));
		transportCompany.addTransportArea(new TransportArea("AC", "Zon A+C", ""));
		transportCompany.addTransportArea(new TransportArea("BC", "Zon B+C", ""));
		transportCompany.addTransportArea(new TransportArea("ABC", "Zon A+B+C", ""));
		transportCompany.addTicketType(new TicketType("H", "Hel", "Helt pris på biljetten."));
		transportCompany.addTicketType(new TicketType("R", "Reducerad", "Reducerat pris på biljetten."));
		lstCompanies.add(transportCompany);
		
		// Västtrafik
		transportCompany = new DefaultTransportCompany("Västtrafik", "72450");
		transportCompany.setLogo(R.drawable.logo_vasttrafik);
		transportCompany.addTransportArea(new TransportArea("G", "Göteborg", ""));
		transportCompany.addTransportArea(new TransportArea("GP", "Göteborg+", ""));
		transportCompany.addTransportArea(new TransportArea("TV", "Trollhättan och Vänersborg", ""));
		transportCompany.addTicketType(new TicketType("V", "Vuxen", ""));
		transportCompany.addTicketType(new TicketType("S", "Skolungdom", ""));
		transportCompany.addTicketType(new TicketType("VN", "Vuxen (natt)", ""));
		transportCompany.addTicketType(new TicketType("SN", "Skolungdom (natt)", ""));
		lstCompanies.add(transportCompany);


		Intent intent = new Intent(Order.this, OrderOptions.class);
		intent.putExtra("transportcompany", (Parcelable)transportCompany);
		startActivity(intent);

		ListView list = (ListView)findViewById(R.id.companylist);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> info, View v, int position, long id) {
				TransportCompany transportCompany = lstCompanies.get(position);

				Intent intent = new Intent(Order.this, OrderOptions.class);
				intent.putExtra("transportcompany", (Parcelable)transportCompany);
				startActivity(intent);
			}
		});
	}
}
