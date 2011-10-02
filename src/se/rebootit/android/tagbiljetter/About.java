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
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
public class About extends Activity implements OnClickListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		((Button)findViewById(R.id.btnGitHub)).setOnClickListener(this);
		((Button)findViewById(R.id.btnFlattr)).setOnClickListener(this);
		((Button)findViewById(R.id.btnWizard)).setOnClickListener(this);
		
		// Make sure the links in the text is clickable
		((TextView)findViewById(R.id.txtDescription)).setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void onClick(View v)
	{
		Intent intent;
		switch(v.getId())
		{
			// Open our github page in the web browser
			case R.id.btnGitHub:
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/erifre/Biljetter"));
				startActivity(intent);
				break;

			// Open our flattr page in the web browser
			case R.id.btnFlattr:
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://flattr.com/thing/371293"));
				startActivity(intent);
				break;

			// Open our Wizard
			case R.id.btnWizard:
				intent = new Intent(this, Wizard.class);
				startActivity(intent);
				break;
		}
	}
}
