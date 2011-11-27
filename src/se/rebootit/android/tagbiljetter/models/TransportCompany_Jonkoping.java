/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.models;

import android.os.*;

import java.text.*;
import java.util.*;
/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
public class TransportCompany_Jonkoping extends TransportCompany
{
	String[] months = new String[] { "jan", "feb", "mar", "apr", "maj", "jun", "jul", "aug", "sep", "okt", "nov", "dec" };

	public TransportCompany_Jonkoping() { };

	public TransportCompany_Jonkoping(String name, String phonenumber) {
		super(name, phonenumber);
	}

	public long getTicketTimestamp(String message) {
		String[] data = getMessageParts(message);

		if (data[0] != null && data[1] != null && data[2] != null && data[3] != null) {
			try {
				int year = Integer.parseInt("20"+data[3]);
				int month = (java.util.Arrays.asList(this.months).indexOf(data[2])+1);
				int day = Integer.parseInt(data[1]);

				String date = year+"-"+(month < 10 ? "0"+month : month)+"-"+(day < 10 ? "0"+day : day);

				return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(year+"-"+month+"-"+day+" "+data[0]).getTime();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return 0;
	}

	public static final Parcelable.Creator<TransportCompany_Jonkoping> CREATOR = new Parcelable.Creator<TransportCompany_Jonkoping>()
	{
		public TransportCompany_Jonkoping createFromParcel(Parcel in) {
			return new TransportCompany_Jonkoping(in);
		}

		public TransportCompany_Jonkoping[] newArray(int size) {
			return new TransportCompany_Jonkoping[size];
		}
	};

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
    }

    private TransportCompany_Jonkoping(Parcel in) {
        super(in);
    }
}
