/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */
package se.rebootit.android.tagbiljetter.models;

import android.os.*;

import java.util.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
public class DefaultTransportCompany extends TransportCompany
{
	public DefaultTransportCompany() { }

	public DefaultTransportCompany(String name, String phonenumber) {
		super(name, phonenumber);
	}

	public static final Parcelable.Creator<DefaultTransportCompany> CREATOR = new Parcelable.Creator<DefaultTransportCompany>()
	{
		public DefaultTransportCompany createFromParcel(Parcel in) {
			return new DefaultTransportCompany(in);
		}

		public DefaultTransportCompany[] newArray(int size) {
			return new DefaultTransportCompany[size];
		}
	};

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
    }

    private DefaultTransportCompany(Parcel in) {
        super(in);
    }
}
