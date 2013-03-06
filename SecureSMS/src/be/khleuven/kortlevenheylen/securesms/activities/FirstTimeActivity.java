package be.khleuven.kortlevenheylen.securesms.activities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import be.khleuven.kortlevenheylen.securesms.R;
import be.khleuven.kortlevenheylen.securesms.model.CheckNumber;
import be.khleuven.kortlevenheylen.securesms.model.StringOutputStream;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class FirstTimeActivity extends Activity {

	// private static final String DEBUG_TAG = "HttpExample";
	private Spinner spinner;
	private EditText number, password, confirmedPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_time);

		this.contactsToXML();
		Button next = (Button) findViewById(R.id.buttonNext);
		this.spinner = (Spinner) findViewById(R.id.spinner1);
		this.number = (EditText) findViewById(R.id.editTextPhoneNumber);
		this.confirmedPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
		this.password = (EditText) findViewById(R.id.editTextPassword);
	}

	private void contactsToXML() {
		ArrayList<String> updatedNumbers = this.updateNumbers();
		this.numbersToXML(updatedNumbers);
	}

	private ArrayList<String> updateNumbers() {
		ArrayList<String> updatedNumbers = new ArrayList<String>();
		Cursor contactsCursor = getContentResolver().query(
				android.provider.ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);

		Cursor phones = null;
		try {

			while (contactsCursor.moveToNext()) {
				String contactId = contactsCursor.getString(contactsCursor
						.getColumnIndex(ContactsContract.Contacts._ID));

				phones = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				while (phones.moveToNext()) {
					String phoneNumber = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
					phoneNumber = this.updatePhoneNumber(phoneNumber);
					updatedNumbers.add(phoneNumber);
				}
			}
		} catch (NullPointerException npe) {
			Log.e(this.getClass().toString(), "Error trying to get Contacts.");
		} finally {
			if (phones != null) {
				phones.close();
			}
			if (contactsCursor != null) {
				contactsCursor.close();
			}
		}
		return updatedNumbers;
	}

	private String updatePhoneNumber(String phoneNumber) {
		phoneNumber = phoneNumber.replace("+", "00");
		// alle non-digits deruit hale
		phoneNumber = phoneNumber.replaceAll("\\D+", "");
		if (phoneNumber.substring(0, 2).equals("04")) {
			// momenteel alleen rekening gehouden met belgie ->
			// later moet landcode opgehaald worden en geprefixed
			phoneNumber = "0032" + phoneNumber.substring(1);
		}
		return phoneNumber;
	}

	private void numbersToXML(ArrayList<String> numbers) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			// root element
			Element rootElement = doc.createElement("contacts");
			doc.appendChild(rootElement);

			for (String number : numbers) {
				// number elements
				Element numberElement = doc.createElement("number");
				// add phone number to number element
				numberElement.appendChild(doc.createTextNode(number));
				rootElement.appendChild(numberElement);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			OutputStream output = new StringOutputStream();
			StreamResult result = new StreamResult(output);

			transformer.transform(source, result);

			String xml = output.toString();
			// android.util.Log.i( "XMLHELPER", xml )

			int size = xml.getBytes().length;
			InputStream is = new ByteArrayInputStream(xml.getBytes());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			// FileOutputStream fOut;
			// try {
			// fOut = openFileOutput("contacts.xml", MODE_WORLD_READABLE);
			// OutputStreamWriter osw = new OutputStreamWriter(fOut);
			// // Write the string to the file
			// osw.write(xml);
			// /* ensure that everything is
			// * really written out and close */
			// osw.flush();
			// osw.close();
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	private boolean noEmptyFields() {
		
		return this.number.getText().toString().length() > 0 && password.getText().toString().length() > 0 && confirmedPassword.getText().toString().length() > 0;
	}
	
	public void handleInput(View view) {
		if (this.hasNetworkConnectivity()) {
			if (this.noEmptyFields()) {
				String number = this.updatePhoneNumber(this.number.getText().toString());
				//boolean numberIsSent = CheckNumber.sendNumber(number);
				boolean numberIsSent = true;
				boolean passwordsMatch = password.getText().toString().equals(confirmedPassword.getText().toString());

				if (numberIsSent && passwordsMatch) {
					String FILENAME = "myApp.txt";
					try {
						FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
					
						String out = "Number:" + number + "\n";
						out += "Country:" + this.spinner.getSelectedItem().toString() + "\n";
						out += "Password:" + this.password.getText().toString() + "\n";
						
						fos.write(out.getBytes());
						fos.close();
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Intent intent = new Intent(this, VerificationActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("phoneNumber", number);
					intent.putExtras(bundle);
					startActivity(intent);
				}
				
			}
			else {
				// popup da zegt da ze alles moete invulle
				this.showPopup("Please fill in all fields");
			}
				

		} else {
			this.showPopup("Networkconnectivity is required to proceed");
		}
	}

	private void showPopup(String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set dialog message
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					dialog.cancel();
				}
			});
		
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	private boolean hasNetworkConnectivity() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			// fetch data
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_first_time, menu);
		return true;
	}

	// Uses AsyncTask to create a task away from the main UI thread. This task
	// takes a
	// URL string and uses it to create an HttpUrlConnection. Once the
	// connection
	// has been established, the AsyncTask downloads the contents of the webpage
	// as
	// an InputStream. Finally, the InputStream is converted into a string,
	// which is
	// displayed in the UI by the AsyncTask's onPostExecute method.
	private class DownloadWebpageText extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {
			// params comes from the execute() call: params[0] is the url.
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}

		// @Override
		// protected void onPostExecute(Result result) {
		// textView.setText(result);
		// }

		// Given a URL, establishes an HttpUrlConnection and retrieves
		// the web page content as a InputStream, which it returns as
		// a string.
		private String downloadUrl(String myurl) throws IOException {
			InputStream inputStreamSite = null;
			// Only display the first 500 characters of the retrieved
			// web page content.
			int len = 500;
			String contentAsString = "error";

			URL url = new URL(myurl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

			// conn.setReadTimeout(10000 /* milliseconds */);
			// conn.setConnectTimeout(15000 /* milliseconds */);
			// conn.setRequestMethod("GET");
			// conn.setDoInput(true);
			// // Starts the query
			// conn.connect();
			// int contentlength = conn.getContentLength();
			// int response = conn.getResponseCode();
			// Log.d(DEBUG_TAG, "The response is: " + response);
			// inputStreamSite = conn.getInputStream();

			// Makes sure that the InputStream is closed after the app is
			// finished using it.
			return contentAsString;

		}

		private void trustAllHosts() {

			X509TrustManager easyTrustManager = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					// Oh, I am easy!
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					// Oh, I am easy!
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

			};

			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { easyTrustManager };

			// Install the all-trusting trust manager
			try {
				SSLContext sc = SSLContext.getInstance("TLS");

				sc.init(null, trustAllCerts, new java.security.SecureRandom());

				HttpsURLConnection.setDefaultSSLSocketFactory(sc
						.getSocketFactory());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Reads an InputStream and converts it to a String.
		public String readIt(InputStream stream, int len) throws IOException,
				UnsupportedEncodingException {
			Reader reader = null;
			reader = new InputStreamReader(stream, "UTF-8");
			char[] buffer = new char[len];
			reader.read(buffer);
			return new String(buffer);
		}

	} // einde inner class

}
