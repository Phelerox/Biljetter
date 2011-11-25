/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.contact;

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

import se.rebootit.android.tagbiljetter.*;
import se.rebootit.android.tagbiljetter.models.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
public class Contact extends Activity implements OnClickListener
{
	TransportCompany transportCompany;
	
	List<Reason> lstReasons = new ArrayList<Reason>();
	
	Spinner spnReason;
	TimePicker timePicker;
	DatePicker datePicker;
	
	EditText txtCity, txtLine;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact);
		
		Intent intent = getIntent();
		this.transportCompany = (TransportCompany)intent.getParcelableExtra("company");

		LinearLayout layoutHeader = (LinearLayout)findViewById(R.id.header);
		TextView txtCompanyname = (TextView)findViewById(R.id.companyname);
		ImageView imgCompanyLogo = (ImageView)findViewById(R.id.companylogo);

		if (transportCompany.getLogo() != null) {
			int logo = Biljetter.getContext().getResources().getIdentifier(transportCompany.getLogo(), "drawable","se.rebootit.android.tagbiljetter");
			int logobg = Biljetter.getContext().getResources().getIdentifier(transportCompany.getLogo()+"_bg", "drawable","se.rebootit.android.tagbiljetter");
			imgCompanyLogo.setImageResource(logo);
			layoutHeader.setBackgroundResource((logobg == 0 ? R.drawable.header_background : logobg));
		}
		else {
			imgCompanyLogo.setVisibility(ImageView.GONE);
		}

		txtCompanyname.setTextColor(Color.parseColor(transportCompany.getHeaderColor()));
		txtCompanyname.setText(transportCompany.getName());

		txtCity = ((EditText)findViewById(R.id.txtCity));
		txtLine = ((EditText)findViewById(R.id.txtLine));
		datePicker = ((DatePicker)findViewById(R.id.datePicker));
		timePicker = ((TimePicker)findViewById(R.id.timePicker));
		
		timePicker.setIs24HourView(true);
		
/*
		lstReasons.add(new Reason("Försenat fordon"));
		lstReasons.add(new Reason("Trasigt fordon"));
		lstReasons.add(new Reason("Beröm"));
*/
		lstReasons.add(new Reason("Otrevlig personal", "Hej!\n\nPå linje %line% i %city% med avgångstid %date% kl. %time% blev jag otrevligt bemött av er personal. Jag hoppas att ni kan framföra detta berörd part.\n\nMed vänliga hälsningar"));


		spnReason = (Spinner)findViewById(R.id.spnReason);
		ArrayAdapter<CharSequence> adapterReason = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		adapterReason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (Reason reason : lstReasons) {
			adapterReason.add(reason.getTitle());
		}
		spnReason.setAdapter(adapterReason);

		((Button)findViewById(R.id.btnPreview)).setOnClickListener(this);
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnPreview:
				Reason reason = lstReasons.get(spnReason.getSelectedItemPosition());

				String date = datePicker.getYear()+"-"+formatTimeAndDate(datePicker.getMonth())+"-"+formatTimeAndDate(datePicker.getDayOfMonth());
				String time = formatTimeAndDate(timePicker.getCurrentHour())+":"+formatTimeAndDate(timePicker.getCurrentMinute());

				reason.setCity(txtCity.getText().toString().trim());
				reason.setLine(txtLine.getText().toString().trim());
				reason.setDeparture(date, time);

				Intent intent = new Intent(this, ContactPreview.class);
				intent.putExtra("company", (Parcelable)transportCompany);
				intent.putExtra("reason", (Parcelable)reason);
				startActivityForResult(intent, 0);

				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
			case 0:
				if (resultCode == RESULT_OK) {
					finish();
				}
				break;
		}
	}
	
	public String formatTimeAndDate(int input) {
		return (input > 9 ? input : "0"+input).toString();
	}
}
