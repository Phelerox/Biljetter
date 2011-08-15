/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
 
public class Order extends Activity implements OnClickListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);
		
		((Button)findViewById(R.id.btnVasttrafik)).setOnClickListener(this);
	}
	
	public void onClick(View v)
	{
		Intent intent = new Intent(this, OrderOptions.class);
		switch(v.getId())
		{
			case R.id.btnVasttrafik:
				//intent.putExtra("provider", TicketLoader.PROVIDER_VASTTRAFIK);
				startActivity(intent);
/*
				Spinner spinner = (Spinner) findViewById(R.id.spnCity);
//				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
				ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mapVasttrafik.values().toArray());

				//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);
*/

				break;
		}
	}
}
