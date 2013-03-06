package be.khleuven.kortlevenheylen.securesms.model;

import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Uploader {

    /**
     * @param args the command line arguments
     */
    public static void verifyCode(String number, String code) {

        try {
            //generate keypair
            String path = "";
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.genKeyPair();

            CertificateFileWriter.SaveKeyPair(path, keyPair);

            //KeyPair loadedKeyPair = CertificateFileReader.LoadKeyPair(path, "RSA");
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
            HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) new URL("https://secure.jonathan.vlan77.be/upload_key.php?number=" + number + "&code=" + code).openConnection();
            httpsUrlConnection.setDoOutput(true);
            httpsUrlConnection.setRequestMethod("POST");
            OutputStream os = httpsUrlConnection.getOutputStream();
            
            //Send Public key
            PublicKey publicKey = keyPair.getPublic();
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            os.write(x509EncodedKeySpec.getEncoded());
            os.close();
            
            //Read output script
            BufferedReader in = new BufferedReader(new InputStreamReader(httpsUrlConnection.getInputStream()));
            String s;
            while ((s = in.readLine()) != null) {
                System.out.println(s);
            }
            in.close();

        } catch (KeyManagementException ex) {
            Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
