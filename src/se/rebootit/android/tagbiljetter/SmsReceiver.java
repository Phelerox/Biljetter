/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

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
	DataParser dataParser = Biljetter.getDataParser();

	public void onReceive( Context context, Intent intent ) 
	{
		// Get SMS map from Intent
		Bundle extras = intent.getExtras();

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

				Ticket ticket = dataParser.parseMessage(fromaddress, sms.getTimestampMillis(), sms.getMessageBody());
				
				if (ticket != null)
				{
					putSmsToDatabase( contentResolver, sms );

					// Notification
					NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

					Notification notification = new Notification(R.drawable.icon, context.getString(R.string.SmsReceiver_newticket), System.currentTimeMillis());

					CharSequence contentTitle = DataParser.getCompanyName(ticket.getProvider());
					CharSequence contentText = context.getString(R.string.SmsReceiver_description).replace("%date%", ticket.getTicketTimestampFormatted());
					Intent notificationIntent = new Intent(context, TicketView.class);
					notificationIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
					notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					notificationIntent.putExtra("ticket", (Parcelable)ticket);

					PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
					notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
					mNotificationManager.notify(ticket.hashCode(), notification);

					// Send broadcast to TicketList telling it to update the tickets
					context.sendBroadcast(new Intent("se.rebootit.android.tagbiljett.TicketList.UPDATE_LIST"));	

					// Tell TicketList to update tickets on next start
					Editor e = sharedPreferences.edit();
					e.putBoolean("rescan", true);
					e.commit();

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
