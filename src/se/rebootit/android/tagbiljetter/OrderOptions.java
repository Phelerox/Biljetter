/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import java.util.*;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.telephony.gsm.*;

import se.rebootit.android.tagbiljetter.models.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
 
public class OrderOptions extends Activity implements OnClickListener
{
	TransportCompany transportCompany;

	List<TransportArea> areas;
	List<TicketType> types;
	
	String number;
	String message;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderoptions);

		Intent intent = getIntent();
		this.transportCompany = (TransportCompany)intent.getParcelableExtra("transportcompany");

		LinearLayout layoutHeader = (LinearLayout)findViewById(R.id.header);
		TextView txtCompanyname = (TextView)findViewById(R.id.companyname);
		ImageView imgCompanyLogo = (ImageView)findViewById(R.id.companylogo);

		if (transportCompany.getLogo() != null) {
			int logo = Biljetter.getContext().getResources().getIdentifier(transportCompany.getLogo(), "drawable","se.rebootit.android.tagbiljetter");
			int logobg = Biljetter.getContext().getResources().getIdentifier(transportCompany.getLogo()+"_bg", "drawable","se.rebootit.android.tagbiljetter");
			imgCompanyLogo.setImageResource(logo);
			layoutHeader.setBackgroundResource(logobg);
		}
		else {
			imgCompanyLogo.setVisibility(ImageView.GONE);
		}

		txtCompanyname.setTextColor(Color.parseColor(transportCompany.getHeaderColor()));
		txtCompanyname.setText(transportCompany.getName());

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

				this.number = transportCompany.getPhoneNumber();
				this.message = transportCompany.getMessage(area, type);
				
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bekräfta köp");
				builder.setMessage("Detta kommer skicka \""+message+"\" till "+number+".");
				builder.setPositiveButton("Ja", dialogClickListener);
				builder.setNegativeButton("Nej", dialogClickListener);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.show();
				
				break;
		}
	}
	
	
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					Toast.makeText(Biljetter.getContext(), "Skickar beställning!", Toast.LENGTH_LONG).show();
					
					SmsManager sm = SmsManager.getDefault();
					sm.sendTextMessage(number, null, message, null, null);
					
					setResult(RESULT_OK, getIntent());
				
					finish();
					
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					Toast.makeText(Biljetter.getContext(), "Beställning avbruten!", Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};
}
