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
public class TransportCompany_Ostgotatrafiken extends TransportCompany
{
	String[] months = new String[] { "jan", "feb", "mar", "apr", "maj", "jun", "jul", "aug", "sep", "okt", "nov", "dec" };

	public TransportCompany_Ostgotatrafiken() { };

	public TransportCompany_Ostgotatrafiken(String name, String phonenumber) {
		super(name, phonenumber);
	}

	public long getTicketTimestamp(String message) {
		String[] data = getMessageParts(message);

		if (data[0] != null && data[1] != null) {
			try {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data[0]+" "+data[1]).getTime();
			} catch (Exception e) { e.printStackTrace(); }
		}
		return 0;
	}

	public static final Parcelable.Creator<TransportCompany_Ostgotatrafiken> CREATOR = new Parcelable.Creator<TransportCompany_Ostgotatrafiken>()
	{
		public TransportCompany_Ostgotatrafiken createFromParcel(Parcel in) {
			return new TransportCompany_Ostgotatrafiken(in);
		}

		public TransportCompany_Ostgotatrafiken[] newArray(int size) {
			return new TransportCompany_Ostgotatrafiken[size];
		}
	};

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
    }

    private TransportCompany_Ostgotatrafiken(Parcel in) {
        super(in);
    }
}
