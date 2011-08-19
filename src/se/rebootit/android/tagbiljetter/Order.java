/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import java.util.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;

import se.rebootit.android.tagbiljetter.models.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
 
public class Order extends Activity
{
	ArrayList<TransportCompany> lstCompanies = new ArrayList<TransportCompany>();
	ListAdapter adapter = new OrderCompanyListAdapter(this.lstCompanies, this);
	
	DataParser dataParser = new DataParser();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order);

		lstCompanies.addAll(dataParser.getCompanies());
		
		ListView list = (ListView)findViewById(R.id.companylist);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> info, View v, int position, long id) {
				TransportCompany transportCompany = lstCompanies.get(position);

				Intent intent = new Intent(Order.this, OrderOptions.class);
				intent.putExtra("transportcompany", (Parcelable)transportCompany);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
			case 0:
				if (resultCode == RESULT_OK) {
					finish();
				}
				break;
		}
	}
}
