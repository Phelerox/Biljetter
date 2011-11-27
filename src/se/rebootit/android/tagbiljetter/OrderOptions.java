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
import android.widget.AdapterView.*;
import android.telephony.gsm.*;

import se.rebootit.android.tagbiljetter.models.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
 
public class OrderOptions extends Activity implements OnClickListener, OnItemSelectedListener
{
	TransportCompany transportCompany;

	List<TransportArea> areas;
	List<TicketType> types;
	
	String number;
	String message;

	TextView txtAreaDescription, txtTypeDescription;

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
		txtAreaDescription = (TextView)findViewById(R.id.txtAreaDescription);
		txtTypeDescription = (TextView)findViewById(R.id.txtTypeDescription);

		int logo = Biljetter.getContext().getResources().getIdentifier((transportCompany.getLogo() != null ? transportCompany.getLogo() : "nologo"), "drawable","se.rebootit.android.tagbiljetter");
		imgCompanyLogo.setImageResource(logo);

		int logobg = Biljetter.getContext().getResources().getIdentifier(transportCompany.getLogo()+"_bg", "drawable","se.rebootit.android.tagbiljetter");
		layoutHeader.setBackgroundResource((logobg == 0 ? R.drawable.header_background : logobg));

		txtCompanyname.setTextColor(Color.parseColor(transportCompany.getTextColor()));
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

		spnArea.setOnItemSelectedListener(this);
		spnType.setOnItemSelectedListener(this);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		String description;
		switch (parent.getId())
		{
			case R.id.spnArea:
				description = areas.get(pos).getDescription();
				if ("".equals(description) || description == null) {
					txtAreaDescription.setVisibility(TextView.GONE);
				}
				else {
					txtAreaDescription.setText(description);
					txtAreaDescription.setVisibility(TextView.VISIBLE);
				}
				break;

			case R.id.spnType:
				description = types.get(pos).getDescription();
				if ("".equals(description) || description == null) {
					txtTypeDescription.setVisibility(TextView.GONE);
				}
				else {
					txtTypeDescription.setText(description);
					txtTypeDescription.setVisibility(TextView.VISIBLE);
				}
				break;
		}
    }

    public void onNothingSelected(AdapterView parent) {
      // Do nothing.
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
				builder.setTitle(getString(R.string.OrderOptions_confirm));
				builder.setMessage(getString(R.string.OrderOptions_confirmMessage).replace("%message%", message).replace("%number%", number));
				builder.setPositiveButton(getString(R.string.OrderOptions_yes), dialogClickListener);
				builder.setNegativeButton(getString(R.string.OrderOptions_no), dialogClickListener);
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
					Toast.makeText(Biljetter.getContext(), getString(R.string.OrderOptions_sending), Toast.LENGTH_LONG).show();
					
					SmsManager sm = SmsManager.getDefault();
					sm.sendTextMessage(number, null, message, null, null);
					
					setResult(RESULT_OK, getIntent());
				
					finish();
					
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					Toast.makeText(Biljetter.getContext(), getString(R.string.OrderOptions_interrupted), Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};
}
