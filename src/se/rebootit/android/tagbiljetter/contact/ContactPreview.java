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
 
public class ContactPreview extends Activity implements OnClickListener
{
	TransportCompany transportCompany;
	Reason reason;
	
	String strEmail;
	
	TextView txtPreview;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactpreview);
		
		Intent intent = getIntent();
		this.transportCompany = (TransportCompany)intent.getParcelableExtra("company");
		this.reason = (Reason)intent.getParcelableExtra("reason");
		
		txtPreview = (TextView)findViewById(R.id.txtPreview);
		
		((Button)findViewById(R.id.btnSend)).setOnClickListener(this);
		
		strEmail = reason.getContent();
		
		strEmail = strEmail.replaceAll("%city%", reason.getCity());
		strEmail = strEmail.replaceAll("%line%", reason.getLine());
		strEmail = strEmail.replaceAll("%date%", reason.getDepartureDate());
		strEmail = strEmail.replaceAll("%time%", reason.getDepartureTime());
		
		txtPreview.setText(strEmail);
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnSend:
				strEmail+= "\n\n----------------\nSkickat via Biljetter\nAndroidappen som hjälper dig i resandet.";
			
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"Erik <erik@fredriksen.se>"});
				intent.putExtra(Intent.EXTRA_SUBJECT, reason.getTitle());
				intent.putExtra(Intent.EXTRA_TEXT, strEmail);

				try {
					startActivity(Intent.createChooser(intent, "Välj mejlklient"));
				} catch (Exception e) { }
			
				//setResult(RESULT_OK, getIntent());
				//finish();
				
				break;
		}
	}
}
