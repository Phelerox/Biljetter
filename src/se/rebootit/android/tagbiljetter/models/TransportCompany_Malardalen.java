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
public class TransportCompany_Malardalen extends TransportCompany
{
	public TransportCompany_Malardalen() { };

	public TransportCompany_Malardalen(String name, String phonenumber) {
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

	public static final Parcelable.Creator<TransportCompany_Malardalen> CREATOR = new Parcelable.Creator<TransportCompany_Malardalen>()
	{
		public TransportCompany_Malardalen createFromParcel(Parcel in) {
			return new TransportCompany_Malardalen(in);
		}

		public TransportCompany_Malardalen[] newArray(int size) {
			return new TransportCompany_Malardalen[size];
		}
	};

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
    }

    private TransportCompany_Malardalen(Parcel in) {
        super(in);
    }
}
