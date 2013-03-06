package be.khleuven.kortlevenheylen.securesms.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CheckNumber {

    /**
     * @param args the command line arguments
     */
    public static boolean sendNumber(String number) {
    	boolean ok = false;
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };
            
            //Start connection
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            //HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) new URL("https://secure.jonathan.vlan77.be/check_number.php?number=" + number).openConnection();
            httpsUrlConnection.setDoOutput(true);
            httpsUrlConnection.setRequestMethod("GET");

            httpsUrlConnection.setSSLSocketFactory(sc.getSocketFactory());
            
            //Read output script
            InputStream is = httpsUrlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader in = new BufferedReader(new InputStreamReader(httpsUrlConnection.getInputStream()));
            System.out.println(in.readLine());

            in.close();
            ok = true;
        } catch (KeyManagementException ex) {
            Logger.getLogger(CheckNumber.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CheckNumber.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CheckNumber.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ok;
    }
}
