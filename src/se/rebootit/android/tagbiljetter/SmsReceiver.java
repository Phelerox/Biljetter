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

import se.rebootit.android.tagbiljetter.models.*;

public class SmsReceiver extends BroadcastReceiver 
{
	SharedPreferences sharedPreferences = Biljetter.getSharedPreferences();
	DataParser dataParser = Biljetter.getDataParser();
	DataBaseHelper dbHelper = Biljetter.getDataBaseHelper();

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

				String phonenumber = sms.getOriginatingAddress();
				long messagetime = sms.getTimestampMillis();
				String message = sms.getMessageBody();

				TransportCompany transportCompany = dataParser.parseMessage(phonenumber, messagetime, message);
				if (transportCompany != null)
				{
					int provider = transportCompany.getId();
					long tickettime = transportCompany.getTicketTimestamp(message);

					dbHelper.insertTicket(phonenumber, messagetime, message, provider, tickettime);

					// Show a notification
					if (sharedPreferences.getBoolean("shownotification", true))
					{
						Ticket ticket = new Ticket(phonenumber, messagetime);
						ticket.setMessage(message);
						ticket.setTicketTimestamp(tickettime);

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
						notification.defaults |= Notification.DEFAULT_SOUND;
						notification.defaults |= Notification.DEFAULT_VIBRATE;
						try {
							mNotificationManager.notify(ticket.hashCode(), notification);
						} catch (Exception e) { }
					}

					// Should we try so silence the sms?
					if (sharedPreferences.getBoolean("silencesms", false))
					{
						// Try to write the sms to the database
						boolean successfulWrite = dataParser.writeSMStoDatabase(sms.getOriginatingAddress(), sms.getTimestampMillis(), sms.getMessageBody().toString(), 1);
						if (successfulWrite) {
							// Prevent the incoming sms from displaying a notification
							this.abortBroadcast(); 
						}
					}
					
					// Send broadcast to TicketList telling it to update the tickets
					context.sendBroadcast(new Intent("se.rebootit.android.tagbiljett.TicketList.UPDATE_LIST"));	

					// Tell TicketList to update tickets on next start
					Editor e = sharedPreferences.edit();
					e.putBoolean("rescan", true);
					e.commit();
				}
				else {
					return;
				}
			}
		}
	}
}
