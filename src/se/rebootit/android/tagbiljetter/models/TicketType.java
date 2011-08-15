/**
 * This file is licensed under the GNU General Public License Version 3
 * For more information, please visit http://www.gnu.org/licenses/gpl.txt
 */

package se.rebootit.android.tagbiljetter.models;

public class TicketType
{
    private String code;
    private String name;
    private String description;
    
    public TicketType() { }
    
    public TicketType(String code, String name, String description)
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
}
