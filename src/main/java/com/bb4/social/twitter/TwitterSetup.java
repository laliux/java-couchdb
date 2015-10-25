package com.bb4.social.twitter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Eduardo Zarate aka @iLaliux
 */
public class TwitterSetup {

    public static final String REQUEST_TOKEN_URL = "https://twitter.com/oauth/request_token";
    public static final String AUTHORIZE_URL = "https://twitter.com/oauth/authorize";
    public static final String ACCESS_TOKEN_URL = "https://twitter.com/oauth/access_token";

    private Properties prop;

    public TwitterSetup() {
        prop = new Properties();

        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public OAuthTokenSecret getOAuthTokenSecret() {
        String accessToken = prop.getProperty("accessToken");
        String accessSecret = prop.getProperty("accessSecret");

        OAuthTokenSecret tokensecret = new OAuthTokenSecret(accessToken, accessSecret);
        return tokensecret;
    }

    public String getConsumerSecret() {
        return prop.getProperty("consumerSecret");
    }

    public String getConsumerKey() {
        return prop.getProperty("consumerKey");
    }

    public String getCouchdb() {
        return prop.getProperty("couchdb");
    }
    
    public Boolean inDebug(){
        return prop.getProperty("debug") != null && prop.getProperty("debug").equalsIgnoreCase("true");
    }
}
