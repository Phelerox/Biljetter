package se.rebootit.android.tagbiljetter;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
 
public class TicketView extends Activity
{
	Ticket ticket;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticketview);
        
        Intent intent = getIntent();
		this.ticket = intent.getParcelableExtra("ticket");
		
		if (ticket.getProvider() != TicketLoader.PROVIDER_SJ && ticket.getProvider() != TicketLoader.PROVIDER_RESPLUS) {
			((TextView)findViewById(R.id.sender)).setText(ticket.getAddress());
			((TextView)findViewById(R.id.sender)).setVisibility(TextView.VISIBLE);
			((TextView)findViewById(R.id.senderHeader)).setVisibility(TextView.VISIBLE);
		}
		((TextView)findViewById(R.id.received)).setText(ticket.getTicketTimestampFormatted());
		((TextView)findViewById(R.id.message)).setText(ticket.getMessage());
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ticketviewmenu, menu);
		
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

				CharSequence contentTitle = TicketLoader.getProviderFormatted(ticket.getProvider());
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
