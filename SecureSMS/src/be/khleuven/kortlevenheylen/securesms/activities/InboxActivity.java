package be.khleuven.kortlevenheylen.securesms.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TreeSet;

import be.khleuven.kortlevenheylen.securesms.R;
import be.khleuven.kortlevenheylen.securesms.R.layout;
import be.khleuven.kortlevenheylen.securesms.R.menu;
import be.khleuven.kortlevenheylen.securesms.model.DateSorter;
import be.khleuven.kortlevenheylen.securesms.model.Message;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class InboxActivity extends ListActivity {

	String selected = "none";
	boolean isContact = false;
	ArrayList<Message> messages = new ArrayList<Message>();
	// Alle kolommen die worden opgehaald (later eventueel te verwijderen kolommen als er onnodige bijzijn)
	String[] columns = new String[] { "_id", "address", "person", "date",
			"body", "type" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		
		Bundle b = getIntent().getExtras();
		selected = b.getString("name");
		isContact = b.getBoolean("contact");
		this.createListView();
		
	}
	
	private Cursor getCursorInbox() {
		Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
		// Alle kolommen die worden opgehaald (later eventueel te verwijderen kolommen als er onnodige bijzijn)
		Cursor cursorInbox = getContentResolver().query(mSmsinboxQueryUri, columns,
				null, null, "date desc");
		return cursorInbox;
	}
	
	private Cursor getCursorSent() {
		Uri mSmsSentQueryUri = Uri.parse("content://sms/sent");
		// Alle kolommen die worden opgehaald (later eventueel te verwijderen kolommen als er onnodige bijzijn) 
		Cursor cursorSent = getContentResolver().query(mSmsSentQueryUri, columns,
				null, null, "date desc");
		return cursorSent;
	}
	
	private void createListView() {
		
		Cursor cursorInbox = this.getCursorInbox();
		Cursor cursorSent = this.getCursorSent();

		if (cursorInbox.getCount() > 0) {
			String count = Integer.toString(cursorInbox.getCount());
			this.addMessagesFromInbox(cursorInbox);
		}
		if (cursorSent.getCount() > 0) {
			String count = Integer.toString(cursorSent.getCount());
			this.addMessagesFromSent(cursorSent);
		}

		if (this.messages.size() > 0) {
			// messages sorteren op datum
			// ---------------------------
			this.sortMessagesOnDate(messages);
			
			ArrayList<String> messageBodies = this.getAllMessages(this.messages);
			String[] strArray = new String[messageBodies.size()];
			messageBodies.toArray(strArray);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, strArray);
			setListAdapter(adapter);
			this.setListAdapter(adapter);
		} else {
			// show "no messages";
		}

	}
	
	private ArrayList<String> getAllMessages(ArrayList<Message> messages) {
		ArrayList<String> messageBodies = new ArrayList<String>();
		for (Message message : messages) {
			messageBodies.add(message.getBody());
		}
		return messageBodies;
	}

	private void addMessagesFromSent(Cursor cursorSent) {
		while (cursorSent.moveToNext()) {
			// de contact zijn naam opvragen
			
			if (isContact) {
				String name = cursorSent.getString(cursorSent.getColumnIndex(columns[2]));
				if (this.selected.equals(name)) {
					// bericht bijhouden
					String strDate = cursorSent.getString(cursorSent.getColumnIndex(columns[3])); //date
					long longDate = Long.parseLong(strDate);
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(longDate);
					Date date = cal.getTime();
					String body = cursorSent.getString(cursorSent.getColumnIndex(columns[4]));
					
					Message message = new Message(date, name, body);
					this.messages.add(message);
					
				}
			} else {
				String phoneNumber = cursorSent.getString(cursorSent.getColumnIndex(columns[1]));
				phoneNumber = phoneNumber.replaceAll("\\D+", "");
				if (this.selected.equals(phoneNumber)) {
					// bericht bijhouden
					String strDate = cursorSent.getString(cursorSent.getColumnIndex(columns[3])); //date
					long longDate = Long.parseLong(strDate);
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(longDate);
					Date date = cal.getTime();
					String body = cursorSent.getString(cursorSent.getColumnIndex(columns[4]));
					
					Message message = new Message(date, phoneNumber, body);
					this.messages.add(message);
					
				}
			}
		}
	}
	
	private void sortMessagesOnDate(ArrayList<Message> messages) {
		DateSorter comparator = new DateSorter();
		Collections.sort(messages, comparator);
	}
	
	private void addMessagesFromInbox(Cursor cursorInbox) {
		while (cursorInbox.moveToNext()) {
			// de contact zijn naam opvragen
			
			if (isContact) {
				String name = cursorInbox.getString(cursorInbox.getColumnIndex(columns[2]));
				if (this.selected.equals(name)) {
					// bericht bijhouden
					String strDate = cursorInbox.getString(cursorInbox.getColumnIndex(columns[3])); //date
					long longDate = Long.parseLong(strDate);
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(longDate);
					Date date = cal.getTime();
					String body = cursorInbox.getString(cursorInbox.getColumnIndex(columns[4]));
					
					Message message = new Message(date, name, body);
					this.messages.add(message);
					
				}
				
			} else {
				String phoneNumber = cursorInbox.getString(cursorInbox.getColumnIndex(columns[1]));
				
				if (this.selected.equals(phoneNumber)) {
					// bericht bijhouden
					String strDate = cursorInbox.getString(cursorInbox.getColumnIndex(columns[3])); //date
					long longDate = Long.parseLong(strDate);
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(longDate);
					Date date = cal.getTime();
					String body = cursorInbox.getString(cursorInbox.getColumnIndex(columns[4]));
					
					Message message = new Message(date, phoneNumber, body);
					this.messages.add(message);
					
				}
			}
			
			// String id =
			// cursor.getString(cursor.getColumnIndex(columns[0]));
			// values[0] =
			// cursor.getString(cursor.getColumnIndex(columns[1])); //
			// address
			// values[0] =
			// cursor.getString(cursor.getColumnIndex(columns[2])); // name
			// values[0] =
			// cursor.getString(cursor.getColumnIndex(columns[3])); //date
			// values[0] =
			// cursor.getString(cursor.getColumnIndex(columns[4])); //msg
			// String type =
			// cursor.getString(cursor.getColumnIndex(columns[5])); //type

		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_inbox, menu);
		return true;
	}
}
