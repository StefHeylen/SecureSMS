package be.khleuven.kortlevenheylen.securesms.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import be.khleuven.kortlevenheylen.securesms.R;
import be.khleuven.kortlevenheylen.securesms.R.layout;
import be.khleuven.kortlevenheylen.securesms.R.menu;
import be.khleuven.kortlevenheylen.securesms.model.Popup;
import be.khleuven.kortlevenheylen.securesms.model.Uploader;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class VerificationActivity extends Activity {

	String number;
	EditText code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification);
		
		Bundle b = getIntent().getExtras();
		this.number = b.getString("phoneNumber");
		this.code = (EditText) findViewById(R.id.editTextEnterVerificationCode);
		
	}

	public void verifyCode(View view) {
//		// retrieve number
//		try {
//			FileInputStream fis = openFileInput("myApp.txt");
//			StringBuffer fileContent = new StringBuffer("");
//
//			byte[] buffer = new byte[1024];
//			int length;
//			while ((length = fis.read(buffer)) != -1) {
//			    fileContent.append(new String(buffer));
//			}
//			
//			String a[] = fileContent.toString().split("\n"); 
//			String tmp[] = a[0].split(";");
//			number = tmp[1];
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		String pathToInternalStorage = getFilesDir().getAbsolutePath();
		if (this.code.getText().toString().length() > 0) {
			Uploader.verifyCode(number, this.code.getText().toString(), pathToInternalStorage);
		} else {
			Popup.showPopup("Please enter your verification code which you receive by SMS", this);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_verification, menu);
		return true;
	}

}
