package se.rebootit.android.tagbiljetter;

import java.io.*;
import java.net.*;
import java.util.*;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.*;

import org.apache.http.util.*;

public class DataBaseHelper extends SQLiteOpenHelper
{
	private static String DB_PATH = "/data/data/se.rebootit.android.tagbiljetter/databases/";
	private static String DB_NAME = "biljetter.sqlite";

	private SQLiteDatabase myDataBase;
	private SQLiteDatabase db; 
	private final Context myContext;

	/**
	* Constructor
	* Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	* @param context
	*/
	public DataBaseHelper(Context context)
	{
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}	

	/**
	* Creates a empty database on the system and rewrites it with your own database.
	* */
	public void createDataBase() throws IOException
	{
		boolean dbExist = checkDataBase();
		//dbExist = false;

		if (dbExist) {
			//do nothing - database already exist
		} else {
			// By calling this method and empty database will be created into the default system path
			// of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();

			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/**
	* Check if the database already exist to avoid re-copying the file each time you open the application.
	* @return true if it exists, false if it doesn't
	*/
	private boolean checkDataBase()
	{
		SQLiteDatabase checkDB = null;

		try
		{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		} catch(SQLiteException e) {
			//database does't exist yet.
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	/**
	* Copies your database from your local assets-folder to the just created empty database in the
	* system folder, from where it can be accessed and handled.
	* This is done by transfering bytestream.
	* */
	private void copyDataBase() throws IOException
	{
		//Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		//Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException
	{
		//Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		
		this.db = this.getReadableDatabase();
	}

	@Override
	public synchronized void close()
	{
		if(myDataBase != null)
		myDataBase.close();

		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}
	
	public ArrayList<Ticket> getTickets()
	{
		ArrayList<Ticket> lstTickets = new ArrayList<Ticket>();
		
		Cursor cursor = db.rawQuery("SELECT * FROM tickets ORDER BY messagetime DESC", null);
		while (cursor.moveToNext())
		{
			String phonenumber = cursor.getString(cursor.getColumnIndex("phonenumber"));
			long messagetime = Long.parseLong(cursor.getString(cursor.getColumnIndex("messagetime")));
			long tickettime = Long.parseLong(cursor.getString(cursor.getColumnIndex("tickettime")));
			String message = cursor.getString(cursor.getColumnIndex("message"));
			int provider = cursor.getInt(cursor.getColumnIndex("provider"));
			
			Ticket ticket = new Ticket(phonenumber, messagetime);
			ticket.setTicketTimestamp(tickettime);
			ticket.setMessage(message);
			ticket.setProvider(provider);
			
			lstTickets.add(ticket);
		}
		return lstTickets;
	}
	
	public boolean insertTicket(String phonenumber, long messagetime, String message, int provider, long tickettime)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put("phonenumber", phonenumber);
			values.put("messagetime", messagetime);
			values.put("tickettime", tickettime);
			values.put("message", message);
			values.put("provider", provider);
			
			db.replace("tickets", null, values);

			return true;
		}
		catch (Exception e) { e.printStackTrace(); }
		return false;
	}
}
