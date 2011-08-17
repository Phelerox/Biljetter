/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;
 
import java.util.*;
import java.io.*;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.util.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.ContextMenu.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;

/**
 * TicketList is the class that lists all the found tickets in the users SMS inbox.
 * 
 * @author Erik Fredriksen <erik@fredriksen.se>
 */

public class TicketList extends Activity implements OnClickListener
{
	ArrayList<Ticket> lstTickets = new ArrayList<Ticket>();
	ListAdapter adapter = new TicketListAdapter(this.lstTickets, this);
	
	SharedPreferences sharedPreferences = Biljetter.getSharedPreferences();
	DataParser dataParser = Biljetter.getDataParser();
	
	IntentFilter mIntentFilter;
	
	boolean scanRunning = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticketlist);
		
		
/*
 		// Skapa ett nytt sms
		ContentValues values = new ContentValues();
		values.put( "address", "721505461" );
		values.put( "date", System.currentTimeMillis() );
		values.put( "read", 0 );
		values.put( "status", 1 );
		values.put( "type", 1 );
		values.put( "seen", 0 );
		values.put( "body", "R-A 08:04 13+'643732949'+\n+'475926302'+\n+'922407994'+\nSL biljett giltig till 08:04 2011-07-21\nRED PRIS 18 kr ink 6% moms\n0649G211034HL20K1\nhttp://mobil.sl.se" );

		// Push row into the SMS table
		ContentResolver contentResolver = Biljetter.getContext().getContentResolver();
		contentResolver.insert( Uri.parse( "content://sms" ), values );
		
		finish();
*/

/*
		Intent intent = new Intent(this, Order.class);
		startActivity(intent);
		finish();
*/
		
		// Load the previous list of tickets
		loadState();
		
		// Listen for messages from SmsReceiver
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("se.rebootit.android.tagbiljett.TicketList.UPDATE_LIST");

		((Button)findViewById(R.id.btnScan)).setOnClickListener(this);
		((Button)findViewById(R.id.btnOrder)).setOnClickListener(this);
		
		ListView list = (ListView)findViewById(R.id.ticketlist);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> info, View v, int position, long id) {
				Ticket ticket = lstTickets.get(position);

				Intent intent = new Intent(TicketList.this, TicketView.class);
				intent.putExtra("ticket", (Parcelable)ticket);
				startActivity(intent);

				updateList();
			}
		});
		
		updateList();
	}

	/**
	 * Update the ListView to always have the latest data visible
	 */
	private void updateList()
	{
		if (this.lstTickets.size() > 0)
		{			
			// Sort the tickets
			Collections.sort(this.lstTickets);
			
			TicketListAdapter adapter = ((TicketListAdapter)((ListView)findViewById(R.id.ticketlist)).getAdapter());
			
/*
			adapter.setProvider(TicketLoader.PROVIDER_RESPLUS, sharedPreferences.getBoolean("pref_show_RESPLUS", true));
			adapter.setProvider(TicketLoader.PROVIDER_SJ, sharedPreferences.getBoolean("pref_show_SJ", true));
			adapter.setProvider(TicketLoader.PROVIDER_SKANETRAFIKEN, sharedPreferences.getBoolean("pref_show_SKANETRAFIKEN", true));
			adapter.setProvider(TicketLoader.PROVIDER_STOCKHOLM, sharedPreferences.getBoolean("pref_show_SL", true));
			adapter.setProvider(TicketLoader.PROVIDER_VARMLANDSTRAFIKEN, sharedPreferences.getBoolean("pref_show_VARMLANDSTRAFIKEN", true));
			adapter.setProvider(TicketLoader.PROVIDER_VASTTRAFIK, sharedPreferences.getBoolean("pref_show_VASTTRAFIK", true));
*/

			adapter.notifyDataSetChanged();
			
			((LinearLayout)findViewById(R.id.no_tickets)).setVisibility(LinearLayout.GONE);
		}
		else {
			((LinearLayout)findViewById(R.id.no_tickets)).setVisibility(LinearLayout.VISIBLE);
		}		
	}
	
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btnScan:
				loadTickets(true, true);
				break;
				
			case R.id.btnOrder:
				Intent intent = new Intent(this, Order.class);
				startActivity(intent);
				break;
		}
	}
	
	private void loadTickets() {
		loadTickets(false, false);
	}
	
	private void loadTickets(boolean clearCache) {
		loadTickets(clearCache, false);
	}

	private void loadTickets(final boolean clearCache, final boolean notify)
	{
		if (scanRunning) {
			return;
		}
		scanRunning = true;
				
		final Handler mHandler = new Handler();

		final Runnable mUpdateResults = new Runnable() {
			public void run() {
				updateList();
				
				if (notify) {
					Toast.makeText(TicketList.this, getString(R.string.TicketList_ticketsloaded), Toast.LENGTH_LONG).show();
				}
			}
		};

		Thread t = new Thread() {
			public void run() {
				ArrayList<Ticket> tmpList = dataParser.getTickets(clearCache);
				if (clearCache) {
					lstTickets.clear();
				}
				lstTickets.addAll(tmpList);
				mHandler.post(mUpdateResults);
				scanRunning = false;
			}
		};
		t.start();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
			case 0: // Settings
				if (resultCode == RESULT_OK)
				{
					if (data.getBooleanExtra("clearcache", false)) {
						this.lstTickets.clear();
						Toast.makeText(this, getString(R.string.TicketList_cachecleared), Toast.LENGTH_LONG).show();
						updateList();
					}
					if (data.getBooleanExtra("reload", false)) {
						updateList();
					}
				}
				break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ticketlist, menu);
		
		return true;
	}
	
	@Override
	protected void onResume() {
		registerReceiver(mIntentReceiver, mIntentFilter);
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(mIntentReceiver);
		super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.scan:
				loadTickets(false, true);
				return true;
				
			case R.id.scanAll:
				loadTickets(true, true);
				return true;
				
			case R.id.order:
				intent = new Intent(this, Order.class);
				startActivity(intent);
				return true;
				
			case R.id.settings:
				intent = new Intent(this, Settings.class);
				startActivityForResult(intent, 0);
				return true;
				
			case R.id.donate:
				intent = new Intent(this, Donate.class);
				startActivity(intent);
				return true;
			
			case R.id.quit:
				finish();
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		savedInstanceState.putParcelableArrayList("tickets", this.lstTickets);

		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		
		if (this.lstTickets.size() == 0) {
			this.lstTickets = (ArrayList)savedInstanceState.getParcelableArrayList("tickets");
			
			updateList();
		}
	}
	
	@Override
	public void finish()
	{
		saveState();
		super.finish();
	}
	
	private void saveState()
	{
		final File cache_dir = this.getCacheDir(); 
		final File suspend_f = new File(cache_dir.getAbsoluteFile() + File.separator + Biljetter.SUSPEND_FILE);

		FileOutputStream   fos  = null;
		ObjectOutputStream oos  = null;
		boolean            keep = true;

		try
		{
			fos = new FileOutputStream(suspend_f);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(this.lstTickets);
		}
		catch (Exception e) {
			keep = false;
			Log.e(Biljetter.LOG_TAG, "Failed to suspend");
		}
		finally {
			try {
				if (oos != null)   oos.close();
				if (fos != null)   fos.close();
				if (keep == false) suspend_f.delete();
			}
			catch (Exception e) { }
		}
	}

	public void loadState()
	{		
		final File cache_dir = this.getCacheDir(); 
		final File suspend_f = new File(cache_dir.getAbsoluteFile() + File.separator + Biljetter.SUSPEND_FILE);

		FileInputStream    fis  = null;
		ObjectInputStream  ois  = null;
		boolean            keep = true;

		try
		{
			fis = new FileInputStream(suspend_f);
			ois = new ObjectInputStream(fis);

			this.lstTickets.addAll((List)ois.readObject());
		}
		catch (Exception e) {
			keep = false;
			Log.e(Biljetter.LOG_TAG, "Failed to resume");
		}
		finally {
			try {
				if (ois != null)   ois.close();
				if (fis != null)   fis.close();
				if (keep == false) suspend_f.delete();
			}
			catch (Exception e) { }
		}
		
		
		if (sharedPreferences.getBoolean("rescan", false)) {
			loadTickets(false, false);
			Editor e = sharedPreferences.edit();
			e.putBoolean("rescan", false);
			e.commit();
		}
	}
	
	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			loadTickets(false);     
		}
	};

}
