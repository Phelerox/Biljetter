/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.models;

import android.os.*;

public class TransportArea implements Parcelable
{
    private String code;
    private String name;
    private String description;
    
    public TransportArea() { }
    
    public TransportArea(String code, String name, String description)
    {
		this.code = code;
		this.name = name;
		this.description = description;
	}
	
	public void setCode(String code) { this.code = code; }
	public String getCode() { return this.code; }
	
	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }
	
	public void setDescription(String description) { this.description = description; }
	public String getDescription() { return this.description; }
	
	private TransportArea(Parcel in) {
		this.code = in.readString();
		this.name = in.readString();
		this.description = in.readString();
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.code);
		out.writeString(this.name);
		out.writeString(this.description);
	}

	public static final Parcelable.Creator<TransportArea> CREATOR = new Parcelable.Creator<TransportArea>() {
		public TransportArea createFromParcel(Parcel in) {
			return new TransportArea(in);
		}

		public TransportArea[] newArray(int size) {
			return new TransportArea[size];
		}
	};

	public int describeContents() {
		return 0;
	}
}
