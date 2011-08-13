package se.rebootit.android.tagbiljetter;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;

/**
 * @author Erik Fredriksen <erik@fredriksen.se>
 */
 
public class TicketView extends Activity
{
	Ticket ticket;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticketview);
        
        Intent intent = getIntent();
		this.ticket = intent.getExtras().getParcelable("ticket");
		
		((TextView)findViewById(R.id.message)).setText(ticket.getMessage());
		
		if (ticket.getProvider() != TicketLoader.PROVIDER_SJ && ticket.getProvider() != TicketLoader.PROVIDER_RESPLUS) {
			((TextView)findViewById(R.id.sender)).setText(ticket.getAddress());
			((TextView)findViewById(R.id.sender)).setVisibility(TextView.VISIBLE);
			((TextView)findViewById(R.id.senderHeader)).setVisibility(TextView.VISIBLE);
		}
		((TextView)findViewById(R.id.received)).setText(ticket.getTimestampFormatted());
    }
}
