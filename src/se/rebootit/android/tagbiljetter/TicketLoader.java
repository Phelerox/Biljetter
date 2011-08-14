package se.rebootit.android.tagbiljetter;

import java.text.*;
import java.util.*;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class TicketLoader
{
	protected static final int PROVIDER_UNKNOWN = 0;
	protected static final int PROVIDER_SJ = 1;
	protected static final int PROVIDER_RESPLUS = 2;
	protected static final int PROVIDER_SL = 3;
	protected static final int PROVIDER_VASTTRAFIK = 4;
	protected static final int PROVIDER_SKANETRAFIKEN = 5;
	protected static final int PROVIDER_VARMLANDSTRAFIKEN = 6;
	
	Context context = Biljetter.getContext();
	SharedPreferences sharedPreferences = Biljetter.getSharedPreferences();
	
	ArrayList<Ticket> lstTickets = new ArrayList<Ticket>();
	String[] months = new String[] { "jan", "feb", "mar", "apr", "maj", "jun", "jul", "aug", "sep", "okt", "nov", "dec" };
	
	public ArrayList<Ticket> getTickets() {
		return getTickets(false);
	}
	
	public ArrayList<Ticket> getTickets(boolean clearCache)
	{
		if (clearCache) {
			Editor e = sharedPreferences.edit();
			e.putLong("lastmessage", 0);
			e.commit();
		}
		this.lstTickets.clear();
		
		long lastmessage = sharedPreferences.getLong("lastmessage", 0);
		long lastmessagetime = 0;
		
		Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
		Cursor cursor = context.getContentResolver().query(
			mSmsinboxQueryUri,
			new String[] { "_id", "thread_id", "address", "person", "date", "body", "type" }, null, null, null);
			
		String[] columns = new String[] { "address", "person", "date", "body", "type" };
		int messageCount = cursor.getCount();
		int messageScanned = 0;
		
		Log.i(Biljetter.LOG_TAG, "Scan for tickets started");
		if (messageCount > 0)
		{
			while (cursor.moveToNext())
			{
				long timestamp = cursor.getLong(cursor.getColumnIndex(columns[2]));
				
				if (lastmessagetime == 0) {
					lastmessagetime = timestamp;
				}
				
				if (lastmessage >= timestamp) {
					Log.i(Biljetter.LOG_TAG, "No scan needed!");
					break;
				}
				
				String fromaddress = cursor.getString(cursor.getColumnIndex(columns[0]));
				String message = cursor.getString(cursor.getColumnIndex(columns[3]));
				int provider = getProvider(fromaddress);
				
				if (provider > 0)
				{
					Ticket ticket = parseMessage(fromaddress, timestamp, message);
					this.lstTickets.add(ticket);
				}
				
				messageScanned++;
			}
			
			Editor e = sharedPreferences.edit();
			e.putLong("lastmessage", lastmessagetime);
			e.commit();
		}
		Log.i(Biljetter.LOG_TAG, "Scan complete! "+messageScanned+" messages scanned!");
		
		return this.lstTickets;
	}
	
	public Ticket parseMessage(String address, long timestamp, String message)
	{
		int provider = getProvider(address);
		
		Ticket ticket = new Ticket(address, timestamp);
		ticket.setMessage(message);
		ticket.setProvider(provider);
		
		try
		{
			String[] parts = message.split("\n");
			String date;
			
			if (provider == TicketLoader.PROVIDER_VASTTRAFIK || provider == TicketLoader.PROVIDER_SKANETRAFIKEN)
			{
				parts = parts[0].split(" ");
				
				ticket.setTicketTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(parts[5]+" "+parts[4]).getTime());
			}
			else if (provider == TicketLoader.PROVIDER_VARMLANDSTRAFIKEN)
			{
				parts = parts[1].split(" ");
				date = "20"+parts[6]+"-"+(java.util.Arrays.asList(this.months).indexOf(parts[5])+1)+"-"+parts[4];
				
				ticket.setTicketTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date+" "+parts[3]).getTime());
			}
			else if (provider == TicketLoader.PROVIDER_SL)
			{
				parts = parts[4].split(" ");
				
				ticket.setTicketTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(parts[5]+" "+parts[4]).getTime());
			}
			else if (provider == TicketLoader.PROVIDER_SJ || provider == TicketLoader.PROVIDER_RESPLUS)
			{
				parts = parts[0].split(" ");
				
				int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date(timestamp)));
				int month = (java.util.Arrays.asList(this.months).indexOf(parts[1])+1);
				int day = Integer.parseInt(parts[0]);
				String[] time = parts[3].split(":");
				
				date = "-"+(month < 10 ? "0"+month : month)+"-"+(day < 10 ? "0"+day : day)+" "+time[0]+":"+time[1];
				
				long time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(year+date).getTime();
				if (time1 < timestamp) {
					year++;
				}
				
				ticket.setTicketTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(year+date).getTime());
			}
			
			// Convert to a readable format
			if (ticket.getTicketTimestamp() > 0) {
				ticket.setTicketTimestampFormatted(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(ticket.getTicketTimestamp())));
			}
		}
		catch (Exception e) { }
		
		return ticket;
	}
	
	/**
	 * Returns the Providers id by using the number the SMS was sent from
	 * @param	address		Number the sms was received from
	 */
	public static int getProvider(String address)
	{
		if (address.startsWith("SJ Biljett")) { // SJ
			return PROVIDER_SJ;
		}
		else if (address.startsWith("RESPLUS")) { // Resplus
			return PROVIDER_RESPLUS;
		}
		else if (address.startsWith("72450")) { // Västtrafik
			return PROVIDER_VASTTRAFIK;
		}
		else if (address.startsWith("72150")) { // Stockholms Lokaltrafik
			return PROVIDER_SL;
		}
		else if (address.startsWith("72040")) { // Skånetrafiken
			return PROVIDER_SKANETRAFIKEN;
		}
		else if (address.startsWith("72032")) { // Värmlandstrafiken
			return PROVIDER_VARMLANDSTRAFIKEN;
		}
		
		return PROVIDER_UNKNOWN;
	}
	
	/**
	 * Returns the formatted version of a provider
	 * @param	provider	The providers id
	 */
	public static String getProviderFormatted(int provider)
	{
		switch (provider)
		{
			case TicketLoader.PROVIDER_SJ:
				return "SJ";
			case TicketLoader.PROVIDER_RESPLUS:
				return "Resplus";
			case TicketLoader.PROVIDER_SL:
				return "SL";
			case TicketLoader.PROVIDER_VASTTRAFIK:
				return "Västtrafik";
			case TicketLoader.PROVIDER_SKANETRAFIKEN:
				return "Skånetrafiken";
			case TicketLoader.PROVIDER_VARMLANDSTRAFIKEN:
				return "Värmlandstrafiken";
			default:
				return "Okänd";
		}
	}
	
}
