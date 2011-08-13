package se.rebootit.android.tagbiljetter;

import java.util.*;
import java.io.*;

import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.util.*;
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
	protected static final String SUSPEND_FILE = "Biljetter";
	protected static final String LOG_TAG = "Biljetter";

	ArrayList<Ticket> lstTickets = new ArrayList<Ticket>();
	ListAdapter adapter = new TicketListAdapter(this.lstTickets, this);
	
	SharedPreferences sharedPreferences = Biljetter.getSharedPreferences();
	TicketLoader ticketLoader = new TicketLoader();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticketlist);
		
		((Button)findViewById(R.id.btnAdd)).setOnClickListener(this);

		loadState();

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
		
		if (sharedPreferences.getBoolean("rescan", false)) {
			loadTickets(false);	
			Editor e = sharedPreferences.edit();
			e.putBoolean("rescan", false);
			e.commit();
		}
	}

	/**
	 * Update the ListView to always have the latest data visible
	 */
	private void updateList()
	{
		if (lstTickets.size() > 0)
		{
			TicketListAdapter adapter = ((TicketListAdapter)((ListView)findViewById(R.id.ticketlist)).getAdapter());
			
			adapter.setProvider(TicketLoader.PROVIDER_RESPLUS, sharedPreferences.getBoolean("pref_show_RESPLUS", true));
			adapter.setProvider(TicketLoader.PROVIDER_SJ, sharedPreferences.getBoolean("pref_show_SJ", true));
			adapter.setProvider(TicketLoader.PROVIDER_SKANETRAFIKEN, sharedPreferences.getBoolean("pref_show_SKANETRAFIKEN", true));
			adapter.setProvider(TicketLoader.PROVIDER_SL, sharedPreferences.getBoolean("pref_show_SL", true));
			adapter.setProvider(TicketLoader.PROVIDER_VARMLANDSTRAFIKEN, sharedPreferences.getBoolean("pref_show_VARMLANDSTRAFIKEN", true));
			adapter.setProvider(TicketLoader.PROVIDER_VASTTRAFIK, sharedPreferences.getBoolean("pref_show_VASTTRAFIK", true));

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
			case R.id.btnAdd:
				loadTickets(true);
				break;
		}
	}

	private void loadTickets(final boolean clearCache)
	{
		final Handler mHandler = new Handler();

		final Runnable mUpdateResults = new Runnable() {
			public void run() {
				updateList();
				
				Toast.makeText(TicketList.this, "Biljetter laddade!", Toast.LENGTH_LONG).show();
			}
		};

		Thread t = new Thread() {
			public void run() {
				lstTickets.clear();
				lstTickets.addAll(ticketLoader.getTickets(clearCache));
				mHandler.post(mUpdateResults);
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
						Toast.makeText(this, "Cache rensat", Toast.LENGTH_LONG).show();
						updateList();
					}
					if (data.getBooleanExtra("need_rescan", false)) {
						updateList();
					}
				}
				break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ticketlistmenu, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.scan:
				loadTickets(false);
				return true;
				
			case R.id.scan2:
				loadTickets(true);
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
	
	private void saveState() {
		final File cache_dir = this.getCacheDir(); 
		final File suspend_f = new File(cache_dir.getAbsoluteFile() + File.separator + SUSPEND_FILE);

		FileOutputStream   fos  = null;
		ObjectOutputStream oos  = null;
		boolean            keep = true;

		try {
			fos = new FileOutputStream(suspend_f);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(this.lstTickets);
		}
		catch (Exception e) {
			keep = false;
			Log.e(TicketList.LOG_TAG, "Failed to suspend");
		}
		finally {
			try {
				if (oos != null)   oos.close();
				if (fos != null)   fos.close();
				if (keep == false) suspend_f.delete();
			}
			catch (Exception e) { /* do nothing */ }
		}
	}

	public void loadState() {
		final File cache_dir = this.getCacheDir(); 
		final File suspend_f = new File(cache_dir.getAbsoluteFile() + File.separator + SUSPEND_FILE);

		FileInputStream    fis  = null;
		ObjectInputStream  ois  = null;
		boolean            keep = true;

		try {
			fis = new FileInputStream(suspend_f);
			ois = new ObjectInputStream(fis);

			this.lstTickets.addAll((List)ois.readObject());
		}
		catch (Exception e) {
			keep = false;
			Log.e(TicketList.LOG_TAG, "Failed to resume");
		}
		finally {
			try {
				if (ois != null)   ois.close();
				if (fis != null)   fis.close();
				if (keep == false) suspend_f.delete();
			}
			catch (Exception e) { /* do nothing */ }
		}
	}

}
