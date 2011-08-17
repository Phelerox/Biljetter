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
 
public class OrderOptions extends Activity implements OnClickListener
{
	TransportCompany transportCompany;

	List<TransportArea> areas;
	List<TicketType> types;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderoptions);

        Intent intent = getIntent();
		this.transportCompany = (TransportCompany)intent.getParcelableExtra("transportcompany");

		TextView txtCompanyname = (TextView)findViewById(R.id.companyname);

		txtCompanyname.setText(transportCompany.getName());
		if (transportCompany.getLogo() > 0) {
			txtCompanyname.setCompoundDrawablePadding(15);
			txtCompanyname.setCompoundDrawablesWithIntrinsicBounds(transportCompany.getLogo(), 0, 0, 0);
		}

		Spinner spnArea = (Spinner)findViewById(R.id.spnArea);
		ArrayAdapter<CharSequence> adapterArea = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		adapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		areas = transportCompany.getTransportAreas();
		for (TransportArea area : areas) {
			adapterArea.add(area.getName());
		}
		spnArea.setAdapter(adapterArea);
		
		Spinner spnType = (Spinner)findViewById(R.id.spnType);
		ArrayAdapter<CharSequence> adapterType = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		types = transportCompany.getTicketTypes();
		for (TicketType type : types) {
			adapterType.add(type.getName());
		}
		spnType.setAdapter(adapterType);

		((Button)findViewById(R.id.btnSend)).setOnClickListener(this);
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnSend:
				TransportArea area = areas.get(((Spinner)findViewById(R.id.spnArea)).getSelectedItemPosition());
				TicketType type = types.get(((Spinner)findViewById(R.id.spnType)).getSelectedItemPosition());
			
				Toast.makeText(this, "Skickar "+transportCompany.getMessage(area, type)+" till "+transportCompany.getPhoneNumber(), Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
