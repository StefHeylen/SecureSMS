package be.khleuven.kortlevenheylen.securesms.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import be.khleuven.kortlevenheylen.securesms.R;
import be.khleuven.kortlevenheylen.securesms.R.id;
import be.khleuven.kortlevenheylen.securesms.R.layout;
import be.khleuven.kortlevenheylen.securesms.R.menu;
import be.khleuven.kortlevenheylen.securesms.model.Message;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.Phones;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ConversationsActivity extends ListActivity {

	private HashMap<String, Integer> ids = new HashMap<String, Integer>();
	private ArrayList<String> unknownContacts = new ArrayList<String>();
	private boolean isContact = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversations);
		
		this.createListView();
		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String name = (String) parent.getItemAtPosition(position);
				
				//launchSmsActivity(name);
				if (!unknownContacts.contains(name)) {
					isContact = true;
				}
				goToInbox(name);
				isContact = false;
			}
		});
	}
	
	public void goToInbox(String selectedContact) {
		Intent intent = new Intent(this, InboxActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("name", selectedContact);
		bundle.putBoolean("contact", isContact);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	private void createListView() {
		Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
		// Alle kolommen die worden opgehaald (later eventueel te verwijderen
		// kolommen als er onnodige bijzijn)
		String[] columns = new String[] { "_id", "address", "person", "date",
				"body", "type" };
		Cursor cursor = getContentResolver().query(mSmsinboxQueryUri, columns,
				null, null, "date asc");
		
		if (cursor.getCount() > 0) {
			String count = Integer.toString(cursor.getCount());

			TreeSet<String> values = new TreeSet<String>();
			while (cursor.moveToNext()) {
				// de contact zijn naam opvragen
				String name = cursor.getString(cursor.getColumnIndex(columns[2]));
				String phoneNumber = cursor.getString(cursor.getColumnIndex(columns[1]));
				if (name == null) {
					// Als de contact niet in het contactboek zit wordt de
					// nummer weergegeven ipv de naam zelf
					name = phoneNumber;
					this.unknownContacts.add(phoneNumber); // zo kan er afgeleid worden of er een nr of naam doorgegeven wordt naar inbox
				}
				values.add(name);
			}

			String[] strArray = new String[values.size()];
			values.toArray(strArray);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, strArray);
			setListAdapter(adapter);

			this.setListAdapter(adapter);
		} else {
			
		}

	}

//	public static int getContactIDFromNumber(String contactNumber, Context context)
//	{
//	    contactNumber = Uri.encode(contactNumber);
//	    int phoneContactID = new Random().nextInt();
//	    Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,Uri.encode(contactNumber)),new String[] {PhoneLookup.DISPLAY_NAME, PhoneLookup._ID}, null, null, null);
//	        while(contactLookupCursor.moveToNext()){
//	            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(PhoneLookup._ID));
//	        }
//	        contactLookupCursor.close();
//
//	    return phoneContactID;
//	}
	
	public void launchNewSms(View view) {
		Intent intent = new Intent(this, SendSMSActivity.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_inbox, menu);
		return true;
	}

}