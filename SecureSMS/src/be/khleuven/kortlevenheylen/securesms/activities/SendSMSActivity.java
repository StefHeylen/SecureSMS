package be.khleuven.kortlevenheylen.securesms.activities;

import java.util.ArrayList;

import be.khleuven.kortlevenheylen.securesms.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class SendSMSActivity extends Activity {

	private Button buttonSend;
	private EditText textPhoneNr;
	private EditText textSMS;
	private ImageButton imageButtonContactList;
	private ArrayList<String> phoneNumbersRecipients = new ArrayList<String>(1);
	
	public static final int PICK_CONTACT = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonSend = (Button) findViewById(R.id.buttonSend);
		textPhoneNr = (EditText) findViewById(R.id.editTextPhoneNo);
		textSMS = (EditText) findViewById(R.id.editTextSMS);
		imageButtonContactList = (ImageButton) findViewById(R.id.imageButtonContactList);

		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String phoneNo = textPhoneNr.getText().toString();
				String sms = textSMS.getText().toString();
				if (phoneNo.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Please enter a recipient!", Toast.LENGTH_LONG)
							.show();
				} else if (sms.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Please enter a message!", Toast.LENGTH_LONG)
							.show();
				} else {
					try {
						sendSMS(phoneNo,sms);
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(),
								"SMS faild, please try again later!",
								Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void openContactList(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT);
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (PICK_CONTACT):
			if (resultCode == Activity.RESULT_OK) {
				 Uri uri = data.getData(); // geeft uri naar contactboek
				 // ContentResolver haalt data op uit sqlite db dmv SQL en cursor is soort van pointer die verwijst naar 0'de positie binnen deze data
				 Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
				 while (cursor.moveToNext()) { 
				    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
				    String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				    if (hasPhone.equals("1")) { 
				        // De geselecteerde persoon heeft een telefoonnummer dus wordt deze opgehaald
				    	Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null); 
				    	while (phones.moveToNext()) { 
			    			String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));   
			    			this.addContact(phoneNumber);
				    	}
				    	phones.close();
				    }
				    else {
				    	this.openContactList(new View(this));
				    	Toast.makeText(getApplicationContext(), "This contact has no phone number!",
								Toast.LENGTH_LONG).show();
				    }
				 }
			}
			break;
		}
	}
	
	public void sendSMS(String phoneNumber, String message) {
		// maakt mogelijk om target aan te duiden om later op te roepen, bv na verzenden van bericht kunde via deze intent een andere activity oproepen (bv sent box)
		PendingIntent pi = PendingIntent.getActivity(this, 0,
		            new Intent(this, ConversationsActivity.class), 0);                
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);  
        
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("body", message);
        getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        
        Toast.makeText(getApplicationContext(),
				"Message sent!", Toast.LENGTH_LONG)
				.show();
        
//        Intent intent = new Intent(this, ConversationsActivity.class);
//		startActivity(intent);
	}
	
	private void addContact(String phoneNumber) {
		this.phoneNumbersRecipients.add(phoneNumber);
		this.textPhoneNr.setText(this.textPhoneNr.getText().length() == 0? phoneNumber : this.textPhoneNr.getText()+"; " + phoneNumber);
		//this.addNewContactField();
		
	}
	
//	private void addNewContactField() {
//		// Parameters van linearlayout aanmaken en toepassen
//		LinearLayout linearlayout = new LinearLayout(this);
//		LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		params.weight = (float) 1.0;
//		linearlayout.setLayoutParams(params);
//		
//		// Parameters van newPhoneNumber aanmaken en toepassen
//		EditText newPhoneNumber = new EditText(this);
//		LayoutParams params1 = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
//		newPhoneNumber.setHint("Please enter a recipient here");
//		params1.weight = (float) 0.95;
//		newPhoneNumber.setLayoutParams(params1);
//		
//		// invoerveld contactpersoon toevoegen aan layout
//		linearlayout.addView(newPhoneNumber);
//		
//		// Ref naar bestaande container ophalen en de nieuwe linearlayout hieraan toevoegen
//		LinearLayout linearContainer = (LinearLayout) findViewById(R.id.linearLayout1);
//		linearContainer.addView(linearlayout);
//	}

}