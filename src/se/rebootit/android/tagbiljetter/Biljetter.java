/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter;

import java.io.*;

import android.app.*;
import android.content.*;
import android.database.sqlite.*;

public class Biljetter extends Application
{
	public static final String SUSPEND_FILE = "Biljetter";
	public static final String LOG_TAG = "Biljetter";
	
	static Context context;
	static DataParser dataParser;
	static DataBaseHelper dbHelper;
	
	@Override
	public void onCreate() {
		this.context = this;
	}
	
	// Provides an easy way of getting the context everythere in the application
	public static Context getContext() {
		return context;
	}
	
	// Provides an easy way of getting SharedPreferences everythere in the application
	public static SharedPreferences getSharedPreferences() {
		return context.getSharedPreferences("Biljetter", Context.MODE_WORLD_READABLE);
	}
	
	// Provides an easy way of getting the DataParser everythere in the application
	public static DataParser getDataParser() {
		if (dataParser == null) {
			dataParser = new DataParser();
			dataParser.getCompanies();
		}
		return dataParser;
	}

	public static DataBaseHelper getDataBaseHelper()
	{
		if (dbHelper == null) {
			try
			{
				dbHelper = new DataBaseHelper(context);
				dbHelper.createDataBase();
				dbHelper.openDataBase();
			} catch (IOException ioe) {
				throw new Error("Unable to create database");
			}
		}
		return dbHelper;
	}
}
