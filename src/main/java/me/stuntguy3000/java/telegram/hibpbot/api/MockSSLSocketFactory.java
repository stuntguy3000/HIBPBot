package me.stuntguy3000.java.telegram.hibpbot.api;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class MockSSLSocketFactory extends SSLSocketFactory {

    private static final X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
        //@Override
        public void verify(String host, SSLSocket ssl) throws IOException {
            // Do nothing
        }

        //@Override
        public void verify(String host, X509Certificate cert) throws SSLException {
            //Do nothing
        }

        //@Override
        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            //Do nothing
        }

        //@Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };
    private static final TrustStrategy trustStrategy = new TrustStrategy() {
        //@Override
        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            return true;
        }
    };
    public MockSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(trustStrategy, hostnameVerifier);
    }
}