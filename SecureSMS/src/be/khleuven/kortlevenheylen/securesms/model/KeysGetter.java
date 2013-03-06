package be.khleuven.kortlevenheylen.securesms.model;

import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class KeysGetter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) new URL("https://secure.jonathan.vlan77.be/get_keys.php?number=0032474314805").openConnection();
            httpsUrlConnection.setDoOutput(true);
            httpsUrlConnection.setRequestMethod("POST");
            OutputStream os = httpsUrlConnection.getOutputStream();
            File file = new File("contacts.xml");
            Long size = file.length();
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));

            for (long i = 0; i < size; i++) {
                os.write(fis.read());
            }
            fis.close();
            os.close();

            InputStream inputStream = httpsUrlConnection.getInputStream();

            // write the inputStream to a FileOutputStream
            OutputStream out = new FileOutputStream(new File("public_keys.zip"));

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            inputStream.close();
            out.flush();
            out.close();
            
        } catch (IOException ex) {
            Logger.getLogger(KeysGetter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(KeysGetter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KeysGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
