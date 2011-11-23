/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import se.rebootit.android.tagbiljetter.models.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
public class TicketView extends Activity
{
	Ticket ticket;
	DataParser dataParser = Biljetter.getDataParser();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticketview);

		Intent intent = getIntent();
		this.ticket = intent.getParcelableExtra("ticket");


		LinearLayout layoutHeader = (LinearLayout)findViewById(R.id.header);
		TextView txtCompanyname = (TextView)findViewById(R.id.companyname);
		ImageView imgCompanyLogo = (ImageView)findViewById(R.id.companylogo);

		TransportCompany transportCompany = dataParser.getCompany(ticket.getProvider());

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

		((TextView)findViewById(R.id.sender)).setText(ticket.getAddress());

		if (!"".equals(ticket.getTicketTimestampFormatted())) {
			((TextView)findViewById(R.id.validtoHeader)).setVisibility(TextView.VISIBLE);
			((TextView)findViewById(R.id.validto)).setVisibility(TextView.VISIBLE);
			((TextView)findViewById(R.id.validto)).setText(ticket.getTicketTimestampFormatted());
		}

		((TextView)findViewById(R.id.received)).setText(ticket.getTimestampFormatted());
		((TextView)findViewById(R.id.message)).setText(ticket.getMessage());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ticketview, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.createnotification:
				NotificationManager mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

				Notification notification = new Notification(R.drawable.icon, null, System.currentTimeMillis());

				CharSequence contentTitle = DataParser.getCompanyName(ticket.getProvider());
				CharSequence contentText = getString(R.string.SmsReceiver_description).replace("%date%", ticket.getTicketTimestampFormatted());
				Intent notificationIntent = new Intent(this, TicketView.class);
				notificationIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				notificationIntent.putExtra("ticket", (Parcelable)this.ticket);
				
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

				notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);

				mNotificationManager.notify(ticket.hashCode(), notification);
				
				finish();
				return true;
			
			case R.id.close:
				finish();
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
