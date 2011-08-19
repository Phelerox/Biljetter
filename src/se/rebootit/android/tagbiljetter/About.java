/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import java.util.*;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.text.method.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
/**
 * Donate is the class that shows the Flattr button for this project
 * 
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
public class About extends Activity implements OnClickListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		((Button)findViewById(R.id.btnFlattr)).setOnClickListener(this);
		
		((TextView)findViewById(R.id.txtDescription)).setMovementMethod(LinkMovementMethod.getInstance());

	}
	
	public void onClick(View v)
	{
		switch(v.getId())
		{
			case R.id.btnFlattr:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://flattr.com/thing/371293"));
				startActivity(browserIntent);
				break;
		}
	}
}
