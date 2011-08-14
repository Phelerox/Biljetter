/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import android.app.*;
import android.content.*;

public class Biljetter extends Application
{
	protected static final String SUSPEND_FILE = "Biljetter";
	protected static final String LOG_TAG = "Biljetter";
	
	static Context context;
	
	@Override
	public void onCreate() {
		this.context = this;
	}
	
	public static Context getContext() {
		return context;
	}
	
	public static SharedPreferences getSharedPreferences() {
		return context.getSharedPreferences("Biljetter", Context.MODE_WORLD_READABLE);
	}
}
