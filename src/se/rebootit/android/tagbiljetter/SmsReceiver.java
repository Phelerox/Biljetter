package se.rebootit.android.tagbiljetter;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.net.*;
import android.os.*;
import android.telephony.*;
import android.util.*;
import android.widget.*;

/**
 * 
 */

public class SmsReceiver extends BroadcastReceiver 
{
	// All available column names in SMS table
	// [_id, thread_id, address, 
	// person, date, protocol, read, 
	// status, type, reply_path_present, 
	// subject, body, service_center, 
	// locked, error_code, seen]

	public static final String SMS_EXTRA_NAME = "pdus";
	public static final String SMS_URI = "content://sms";

	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String DATE = "date";
	public static final String READ = "read";
	public static final String STATUS = "status";
	public static final String TYPE = "type";
	public static final String BODY = "body";
	public static final String SEEN = "seen";

	public static final int MESSAGE_TYPE_INBOX = 1;
	public static final int MESSAGE_TYPE_SENT = 2;

	public static final int MESSAGE_IS_NOT_READ = 0;
	public static final int MESSAGE_IS_READ = 1;

	public static final int MESSAGE_IS_NOT_SEEN = 0;
	public static final int MESSAGE_IS_SEEN = 1;
	
	SharedPreferences sharedPreferences = Biljetter.getSharedPreferences();

	public void onReceive( Context context, Intent intent ) 
	{
		// Get SMS map from Intent
		Bundle extras = intent.getExtras();

		Ticket ticket = new Ticket();
		TicketLoader ticketLoader = new TicketLoader();

		if ( extras != null )
		{
			// Get received SMS array
			Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );

			// Get ContentResolver object for pushing encrypted SMS to incoming folder
			ContentResolver contentResolver = context.getContentResolver();

			for ( int i = 0; i < smsExtra.length; ++i )
			{
				SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);

				String fromaddress = sms.getOriginatingAddress();

				int provider = TicketLoader.getProvider(fromaddress);

				if (provider > 0)
				{
					ticket = ticketLoader.parseMessage(fromaddress, sms.getTimestampMillis(), sms.getMessageBody());

					putSmsToDatabase( contentResolver, sms );
					
					// Notification
					NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

					int icon = R.drawable.icon;
					CharSequence tickerText = "Ny biljett mottagen!";
					long when = System.currentTimeMillis();

					Notification notification = new Notification(icon, tickerText, when);

					//Context context = getApplicationContext();
					CharSequence contentTitle = TicketLoader.getProviderFormatted(ticket.getProvider());
					CharSequence contentText = "För resa "+ticket.getTicketTimestampFormatted();
					Intent notificationIntent = new Intent(context, TicketView.class);
					notificationIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					notificationIntent.putExtra("ticket", (Parcelable)ticket);


					Editor e = sharedPreferences.edit();
					e.putBoolean("rescan", true);
					e.commit();

					PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

					notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

					mNotificationManager.notify(ticket.hashCode(), notification);

					// Prevent the incoming sms from displaying a notification
					this.abortBroadcast(); 
				}
				else {
					return;
				}
			}
		}
	}

	private void putSmsToDatabase( ContentResolver contentResolver, SmsMessage sms )
	{
		// Create SMS row
		ContentValues values = new ContentValues();
		values.put( ADDRESS, sms.getOriginatingAddress() );
		values.put( DATE, sms.getTimestampMillis() );
		values.put( READ, MESSAGE_IS_READ );
		values.put( STATUS, sms.getStatus() );
		values.put( TYPE, MESSAGE_TYPE_INBOX );
		values.put( SEEN, MESSAGE_IS_SEEN );
		values.put( BODY, sms.getMessageBody().toString());

		// Push row into the SMS table
		contentResolver.insert( Uri.parse( SMS_URI ), values );
	}
}
