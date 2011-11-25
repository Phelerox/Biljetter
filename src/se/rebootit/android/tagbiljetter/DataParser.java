/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.content.SharedPreferences.*;
import android.database.*;
import android.net.*;
import android.util.*;

import se.rebootit.android.tagbiljetter.models.*;

public class DataParser
{
	Context context = Biljetter.getContext();
	SharedPreferences sharedPreferences = Biljetter.getSharedPreferences();

	ArrayList<TransportCompany> lstCompanies = new ArrayList<TransportCompany>();

	DataBaseHelper dbHelper = Biljetter.getDataBaseHelper();

	static HashMap<Integer, TransportCompany> mapCompanies = new HashMap<Integer, TransportCompany>();

	/**
	 * Scan the phone inbox and look for tickets that we can import
	 * @param clearCache	Clear the cache and scan the WHOLE inbox
	 */
	public void scanForTickets(boolean clearCache)
	{
		if (clearCache) {
			Editor e = sharedPreferences.edit();
			e.putLong("lastmessage", 0);
			e.commit();
		}

		Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), new String[] { "_id", "thread_id", "address", "person", "date", "body", "type" }, null, null, null);

		int messageCount = cursor.getCount();
		int messageScanned = 0;
		long lastmessage = sharedPreferences.getLong("lastmessage", 0);
		long lastmessagetime = 0;

		if (messageCount > 0)
		{
			while (cursor.moveToNext())
			{
				long messagetime = cursor.getLong(cursor.getColumnIndex("date"));

				if (lastmessagetime == 0) {
					lastmessagetime = messagetime;
				}

				if (lastmessage >= messagetime) {
					break;
				}

				String phonenumber = cursor.getString(cursor.getColumnIndex("address"));
				String message = cursor.getString(cursor.getColumnIndex("body"));

				TransportCompany transportCompany = parseMessage(phonenumber, messagetime, message);
				if (transportCompany != null)
				{
					int provider = transportCompany.getId();
					long tickettime;
					if (transportCompany instanceof TransportCompany_SJ) {
						tickettime = transportCompany.getTicketTimestamp(message, messagetime);
					} else {
						tickettime = transportCompany.getTicketTimestamp(message);
					}

					dbHelper.insertTicket(phonenumber, messagetime, message, provider, tickettime);
				}

				messageScanned++;
			}

			Editor e = sharedPreferences.edit();
			e.putLong("lastmessage", lastmessagetime);
			e.commit();
		}
	}

	/**
	 * Returns the TransportCompany
	 * @param companyid		The companys id number
	 */
	public static TransportCompany getCompany(int companyid) {
		try {
			return mapCompanies.get(companyid);
		}
		catch (Exception e) { }
		return null;
	}

	/**
	 * Returns the company name
	 * @param companyid		The companys id number
	 */
	public static String getCompanyName(int companyid) {
		try {
			return mapCompanies.get(companyid).getName();
		}
		catch (Exception e) { }
		return "";
	}

	/**
	 * Parse the message and check if it's a valid ticket
	 * @param phonenumber	From what number did the sms arrive from?
	 * @param timestamp		Timestamp arrived
	 * @param message		The message
	 */
	public TransportCompany parseMessage(String phonenumber, long timestamp, String message)
	{
		for (TransportCompany transportCompany : lstCompanies)
		{
			if (phonenumber.startsWith(transportCompany.getPhoneNumber())) 
			{
				String expr = transportCompany.getTicketFormat();

				Pattern pattern = Pattern.compile(expr, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
				Matcher matcher = pattern.matcher(message);
				
				if (matcher.matches()) {
					return transportCompany;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Return the list of loaded companies
	 */
	public ArrayList<TransportCompany> getCompanies()
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		DefaultHandler handler = new TransportCompanyHandler();
		AssetManager assetManager = context.getAssets();
		try
		{
			InputStream inputStream = assetManager.open("TransportCompanies.xml");
			SAXParser parser = factory.newSAXParser();
			parser.parse(inputStream, handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.lstCompanies.clear();
		this.lstCompanies.addAll((ArrayList)((TransportCompanyHandler)handler).getCompanies());
		
		for (TransportCompany transportCompany : this.lstCompanies) {
			mapCompanies.put(transportCompany.getId(), transportCompany);
		}
		
		Collections.sort(this.lstCompanies, new Comparator<TransportCompany>() {
			public int compare(TransportCompany p1, TransportCompany p2) {
				return p1.getName().compareTo(p2.getName());
				}
			});
		
		return this.lstCompanies;
	}
	
	private class TransportCompanyHandler extends DefaultHandler
	{
		private ArrayList<TransportCompany> companies = new ArrayList<TransportCompany>();
		private TransportCompany currentCompany;
		private StringBuilder builder;
		
		public ArrayList<TransportCompany> getCompanies() {
			return this.companies;
		}
		
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			super.characters(ch, start, length);
			builder.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String name) throws SAXException
		{
			super.endElement(uri, localName, name);
			if (this.currentCompany != null) {
				if (localName.equalsIgnoreCase("company")){
					this.companies.add(this.currentCompany);
				}
				builder.setLength(0);
			}
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			builder = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
			super.startElement(uri, localName, name, attributes);
			
			if (localName.equalsIgnoreCase("company")) {
				try {
					String type = attributes.getValue("type");
					this.currentCompany = (TransportCompany)Class.forName(type).newInstance();
				} catch (Exception e) {
					this.currentCompany = new DefaultTransportCompany();
				}
				this.currentCompany.setId(Integer.parseInt(attributes.getValue("id")));
				this.currentCompany.setName(attributes.getValue("name"));
				this.currentCompany.setPhoneNumber(attributes.getValue("phonenumber"));
				this.currentCompany.setLogo(attributes.getValue("logo"));
				this.currentCompany.setEmail(attributes.getValue("email"));
				if (attributes.getValue("headercolor") != null) {
					this.currentCompany.setHeaderColor(attributes.getValue("headercolor"));
				}
			}
			else if (localName.equalsIgnoreCase("area")) {
				String areaCode = attributes.getValue("code");
				String areaName = attributes.getValue("name");
				String areaDescription = attributes.getValue("description");
				TransportArea area = new TransportArea(areaCode, areaName, areaDescription);
				this.currentCompany.addTransportArea(area);
			}
			else if (localName.equalsIgnoreCase("type")) {
				String typeCode = attributes.getValue("code");
				String typeName = attributes.getValue("name");
				String typeDescription = attributes.getValue("description");
				TicketType type = new TicketType(typeCode, typeName, typeDescription);
				this.currentCompany.addTicketType(type);
			}
			else if (localName.equalsIgnoreCase("ticket")) {
				String ticketFormat = attributes.getValue("format");
				this.currentCompany.setTicketFormat(ticketFormat);
			}
		}
	}
	
	/**
	 * Write a message to the inbox database
	 * @param address	From what phone number
	 * @param timestamp	Time of arrival
	 * @param body		Body of the message
	 * @param read		Is the message read or not?
	 */
	public boolean writeSMStoDatabase(String address, long timestamp, String body, int read)
	{
		ContentValues values = new ContentValues();
		values.put("address", address);
		values.put("date", timestamp);
		values.put("read", read);
		values.put("status", 1);
		values.put("type", 1);
		values.put("seen", read );
		values.put("body", body);

		ContentResolver contentResolver = Biljetter.getContext().getContentResolver();
		contentResolver.insert(Uri.parse( "content://sms" ), values);
		
		// Check if the message was saved correctly
		Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), new String[] { "_id", "thread_id", "address", "person", "date", "body", "type" }, null, null, null);
		while (cursor.moveToNext())
		{
			long msgTimestamp = cursor.getLong(cursor.getColumnIndex("date"));
			String msgAddress = cursor.getString(cursor.getColumnIndex("address"));
			String msgBody = cursor.getString(cursor.getColumnIndex("body"));

			// It's in there! :D
			if (timestamp == msgTimestamp && address.equals(msgAddress) && body.equals(msgBody)) {
				return true;
			}

			// No need to scan the whole inbox.
			if (timestamp >= msgTimestamp) {
				break;
			}
		}
		
		// It was not...
		return false;
	}

	// Load old tickets and convert them
	public void convertFromSuspend()
	{
		final File cache_dir = Biljetter.getContext().getCacheDir();
		final File suspend_f = new File(cache_dir.getAbsoluteFile() + File.separator + Biljetter.SUSPEND_FILE);

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		boolean keep = true;

		try
		{
			fis = new FileInputStream(suspend_f);
			ois = new ObjectInputStream(fis);

			List<Ticket> tickets = (List)ois.readObject();

			for (Ticket ticket : tickets)
			{
				dbHelper.insertTicket(ticket.getAddress(), ticket.getTimestamp(), ticket.getMessage(), ticket.getProvider(), ticket.getTicketTimestamp());
			}
			keep = false;
		}
		catch (Exception e) {

		}
		finally {
			try {
				if (ois != null) ois.close();
				if (fis != null) fis.close();
				if (keep == false) suspend_f.delete();
			}
			catch (Exception e) { }
		}
	}

	public static CharSequence readAsset(String asset, Activity activity)
	{
		BufferedReader in = null;

		try
		{
			in = new BufferedReader(new InputStreamReader(activity.getAssets().open(asset)));

			String line;
			StringBuilder buffer = new StringBuilder();

			while ((line = in.readLine()) != null) {
				buffer.append(line).append('\n');
			}

			return buffer;
		}
		catch (IOException e) {
			return "";
		}
		finally {
			try {
				in.close();
			}
			catch (Exception e) { }
		}
	}
}
