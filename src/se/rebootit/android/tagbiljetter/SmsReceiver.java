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
	SharedPreferences sharedPreferences = Biljetter.getSharedPreferences();
	DataParser dataParser = Biljetter.getDataParser();

	public void onReceive(Context context, Intent intent) 
	{
		// Get SMS map from Intent
		Bundle extras = intent.getExtras();

		if (extras != null)
		{
			// Get received SMS array
			Object[] smsExtra = (Object[])extras.get("pdus");

			// Get ContentResolver object for pushing encrypted SMS to incoming folder
			ContentResolver contentResolver = context.getContentResolver();

			for (int i = 0; i < smsExtra.length; ++i)
			{
				SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);

				String fromaddress = sms.getOriginatingAddress();

				Ticket ticket = dataParser.parseMessage(fromaddress, sms.getTimestampMillis(), sms.getMessageBody());
				
				if (ticket != null)
				{
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

					// Try to write the sms to the database
					boolean successfulWrite = dataParser.writeSMStoDatabase(sms.getOriginatingAddress(), sms.getTimestampMillis(), sms.getMessageBody().toString(), 1);
					if (successfulWrite) {
						// Prevent the incoming sms from displaying a notification
						this.abortBroadcast(); 
					}
				}
				else {
					return;
				}
			}
		}
	}
}
