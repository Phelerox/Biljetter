/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;
 
import java.util.*;
import java.io.*;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.SharedPreferences.*;
import android.util.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.ContextMenu.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;

import se.rebootit.android.tagbiljetter.models.*;

/**
 * TicketList is the class that lists all the found tickets in the users SMS inbox.
 * 
 * @author Erik Fredriksen <erik@fredriksen.se>
 */

public class TicketList extends Activity implements OnClickListener
{
	ArrayList<Ticket> lstTickets = new ArrayList<Ticket>();
	TicketListAdapter adapter = new TicketListAdapter(this.lstTickets, this);
	
	SharedPreferences sharedPreferences = Biljetter.getSharedPreferences();
	DataParser dataParser = Biljetter.getDataParser();
	DataBaseHelper dbHelper = Biljetter.getDataBaseHelper();
	
	IntentFilter mIntentFilter;
	
	boolean scanRunning = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ticketlist);

		// Listen for messages from SmsReceiver
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("se.rebootit.android.tagbiljett.TicketList.UPDATE_LIST");

		// If this is the first time this version of the application is run? Then show the Wizard!
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			int versionNumber = pinfo.versionCode;

			if (sharedPreferences.getInt("wizard", 0) < versionNumber)
			{
				final Dialog dialog = new Dialog(this);
				dialog.setCancelable(true);
				dialog.setContentView(R.layout.wizard);
				dialog.setTitle(getString(R.string.Wizard_header));

				TextView txtChangelog = (TextView)dialog.findViewById(R.id.txtChangelog);
				txtChangelog.setText(dataParser.readAsset("changelog.txt", this).toString());

				Button button = (Button)dialog.findViewById(R.id.btnClose);
				button.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();

				scanForTickets(false, false);

				Editor e = sharedPreferences.edit();
				e.putInt("wizard", versionNumber);
				e.commit();
			}
		} catch (Exception e) { }

		((Button)findViewById(R.id.btnScan)).setOnClickListener(this);
		((Button)findViewById(R.id.btnOrder)).setOnClickListener(this);

		// Create the list with all the tickets and make them clickable
		ListView list = (ListView)findViewById(R.id.ticketlist);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> info, View v, int position, long id) {
				Ticket ticket = lstTickets.get(position);

				// Show TicketView
				Intent intent = new Intent(TicketList.this, TicketView.class);
				intent.putExtra("ticket", (Parcelable)ticket);
				startActivity(intent);

				updateList();
			}
		});

		// Convert old tickets to the new format
		dataParser.convertFromSuspend();

		// Load tickets and update the list
		updateList();
	}

	/**
	 * Update the ListView to always have the latest data visible
	 */
	private void updateList()
	{
		this.lstTickets.clear();
		this.lstTickets.addAll(dbHelper.getTickets());

		if (this.lstTickets.size() > 0)
		{
			// Make sure all the tickets are sorted by date
			Collections.sort(this.lstTickets);

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
				scanForTickets(true, true);
				break;
				
			case R.id.btnOrder:
				Intent intent = new Intent(this, Order.class);
				startActivity(intent);
				break;
		}
	}

	private void scanForTickets() {
		scanForTickets(false, false);
	}
	
	private void scanForTickets(boolean clearCache) {
		scanForTickets(clearCache, false);
	}

	/**
	 * Scan for new tickets
	 * @param clearCache	Clear previous cache and scan the whole inbox
	 * @param notify		Tell the user that a scan is ongoing
	 */
	private void scanForTickets(final boolean clearCache, final boolean notify)
	{
		if (scanRunning) {
			return;
		}
		scanRunning = true;
				
		final Handler mHandler = new Handler();
		final ProgressDialog dialog = new ProgressDialog(this);
		if (notify) {
			dialog.setMessage(getString(R.string.TicketList_searchingfortickets));
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}

		final Runnable mUpdateResults = new Runnable() {
			public void run() {
				updateList();

				if (notify) {
					dialog.dismiss();
					Toast.makeText(TicketList.this, getString(R.string.TicketList_ticketsloaded), Toast.LENGTH_LONG).show();
				}
			}
		};

		Thread t = new Thread() {
			public void run() {
				dataParser.scanForTickets(clearCache);

				mHandler.post(mUpdateResults);
				scanRunning = false;
			}
		};
		t.start();
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
				scanForTickets(false, true);
				return true;

			case R.id.order:
				intent = new Intent(this, Order.class);
				startActivity(intent);
				return true;
				
			case R.id.settings:
				intent = new Intent(this, Settings.class);
				startActivity(intent);
				return true;
				
			case R.id.about:
				intent = new Intent(this, About.class);
				startActivity(intent);
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

	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateList();
		}
	};
}
