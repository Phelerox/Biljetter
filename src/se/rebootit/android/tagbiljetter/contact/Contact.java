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

import se.rebootit.android.tagbiljetter.*;
import se.rebootit.android.tagbiljetter.models.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
public class Contact extends Activity implements OnClickListener
{
	TransportCompany transportCompany;
	
	List<Reason> lstReasons = new ArrayList<Reason>();
	Reason reason;
	
	Spinner spnReason;
	TimePicker timePicker;
	DatePicker datePicker;
	TextView txtPreview;
	EditText txtCity, txtLine;

	String strEmail;

	int page = 1;

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
			imgCompanyLogo.setImageResource(logo);

			int logobg = Biljetter.getContext().getResources().getIdentifier(transportCompany.getLogo()+"_bg", "drawable","se.rebootit.android.tagbiljetter");
			layoutHeader.setBackgroundResource((logobg == 0 ? R.drawable.header_background : logobg));
		}
		else {
			imgCompanyLogo.setVisibility(ImageView.GONE);
		}

		txtCompanyname.setTextColor(Color.parseColor(transportCompany.getTextColor()));
		txtCompanyname.setText(transportCompany.getName());

		txtCity = (EditText)findViewById(R.id.txtCity);
		txtLine = (EditText)findViewById(R.id.txtLine);
		datePicker = (DatePicker)findViewById(R.id.datePicker);
		timePicker = (TimePicker)findViewById(R.id.timePicker);
		txtPreview = (TextView)findViewById(R.id.txtPreview);
		
		timePicker.setIs24HourView(true);

		lstReasons.add(new Reason("Otrevlig personal", "Hej!\n\nPå linje %line% i %city% med avgångstid %date% kl. %time% blev jag otrevligt bemött av er personal. Jag hoppas att ni kan framföra detta till berörd part.\n\nMed vänliga hälsningar"));

		spnReason = (Spinner)findViewById(R.id.spnReason);
		ArrayAdapter<CharSequence> adapterReason = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		adapterReason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (Reason reason : lstReasons) {
			adapterReason.add(reason.getTitle());
		}
		spnReason.setAdapter(adapterReason);

		((Button)findViewById(R.id.btnBack)).setOnClickListener(this);
		((Button)findViewById(R.id.btnNext)).setOnClickListener(this);
	}

	public void page(int newpage)
	{
		int oldpage = page;
		page = newpage;

		switch (page)
		{
			case 0:
				finish();
				break;

			case 1:

				((LinearLayout)findViewById(R.id.part2)).setVisibility(LinearLayout.GONE);
				((LinearLayout)findViewById(R.id.part1)).setVisibility(LinearLayout.VISIBLE);

				((Button)findViewById(R.id.btnBack)).setVisibility(View.INVISIBLE);
				break;

			case 2:
				reason = lstReasons.get(spnReason.getSelectedItemPosition());

				if ("".equals(txtCity.getText().toString().trim())) {
					Toast.makeText(this, "Du måste fylla i stad!", Toast.LENGTH_LONG).show();
					return;
				}
				reason.setCity(txtCity.getText().toString().trim());

				if ("".equals(txtLine.getText().toString().trim())) {
					Toast.makeText(this, "Du måste fylla i vilken linje!", Toast.LENGTH_LONG).show();
					return;
				}
				reason.setLine(txtLine.getText().toString().trim());

				((LinearLayout)findViewById(R.id.part1)).setVisibility(LinearLayout.GONE);
				((LinearLayout)findViewById(R.id.part2)).setVisibility(LinearLayout.VISIBLE);
				((LinearLayout)findViewById(R.id.part3)).setVisibility(LinearLayout.GONE);

				((Button)findViewById(R.id.btnBack)).setVisibility(View.VISIBLE);
				break;

			case 3:
				String date = datePicker.getYear()+"-"+formatTimeAndDate(datePicker.getMonth())+"-"+formatTimeAndDate(datePicker.getDayOfMonth());
				String time = formatTimeAndDate(timePicker.getCurrentHour())+":"+formatTimeAndDate(timePicker.getCurrentMinute());

				reason.setDeparture(date, time);

				strEmail = reason.getContent();
				strEmail = strEmail.replaceAll("%city%", reason.getCity());
				strEmail = strEmail.replaceAll("%line%", reason.getLine());
				strEmail = strEmail.replaceAll("%date%", reason.getDepartureDate());
				strEmail = strEmail.replaceAll("%time%", reason.getDepartureTime());
				strEmail+= "\n\n----------------\nSkickat via Biljetter\nAndroidappen som hjälper dig i resandet.";

				txtPreview.setText(strEmail);

				((LinearLayout)findViewById(R.id.part2)).setVisibility(LinearLayout.GONE);
				((LinearLayout)findViewById(R.id.part3)).setVisibility(LinearLayout.VISIBLE);
				break;

			case 4:
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"Erik <erik@fredriksen.se>"});
				intent.putExtra(Intent.EXTRA_SUBJECT, reason.getTitle());
				intent.putExtra(Intent.EXTRA_TEXT, strEmail);

				startActivity(Intent.createChooser(intent, "Välj mejlklient"));

				page--;
				break;

			default:
				page = oldpage;

		}
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnNext:
				page(page+1);
				break;

			case R.id.btnBack:
				page(page-1);
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			page(page-1);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	
	public String formatTimeAndDate(int input) {
		return (input > 9 ? input : "0"+input).toString();
	}
}
