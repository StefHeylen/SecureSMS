package be.khleuven.kortlevenheylen.securesms.activities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import be.khleuven.kortlevenheylen.securesms.R;
import be.khleuven.kortlevenheylen.securesms.R.layout;
import be.khleuven.kortlevenheylen.securesms.R.menu;
import be.khleuven.kortlevenheylen.securesms.model.Uploader;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class VerificationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification);
	}

	public void verifyCode(View view) {
		String number;
		String code; 
		
		try {
			FileInputStream fis = openFileInput("myApp.txt");
			StringBuffer fileContent = new StringBuffer("");

			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) != -1) {
			    fileContent.append(new String(buffer));
			}
			System.out.println(fileContent.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Uploader.verifyCode(number, code);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_verification, menu);
		return true;
	}

}
