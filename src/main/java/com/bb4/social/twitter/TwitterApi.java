package com.bb4.social.twitter;

import com.bb4.social.twitter.utils.DateUtils;
import com.bb4.social.twitter.utils.TweetSaver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 *
 * @author Eduardo Zarate aka @iLaliux
 */
public class TwitterApi {

    BufferedWriter outFileWriter;
    OAuthTokenSecret oAuthTokens;

    OAuthConsumer consumer;
    
    String twitterdb;
    Boolean debug;

    public TwitterApi(TwitterSetup twitterSetup){
         this(twitterSetup, false);
     }
    
     
    public TwitterApi(TwitterSetup twitterSetup, Boolean debug) {
        this.twitterdb = twitterSetup.getCouchdb();
        this.oAuthTokens = twitterSetup.getOAuthTokenSecret();
        this.debug = debug;

        consumer = new DefaultOAuthConsumer(twitterSetup.getConsumerKey(), twitterSetup.getConsumerSecret());
        consumer.setTokenWithSecret(oAuthTokens.getAccessToken(), oAuthTokens.getAccessSecret());

    }

    /**
     * Fetches tweets matching a query
     *
     * @param query for which tweets need to be fetched
     * @return an array of status objects
     */
    public int getSearchResults(String query, long max_id) {
        BufferedReader bRead = null;
        //Get the maximum number of tweets possible in a single page 200
        int tweetcount = 100;

        //Include include_entities 
        boolean include_entities = true;

        int total = 0;
        int block = 1;

        try {
            System.out.println("Processing tweets about \"" + query + "\"");
            long maxid = max_id;
            while (true) {
                URL url = null;
                if (maxid == 0) {
                    url = new URL("https://api.twitter.com/1.1/search/tweets.json?q=" + query + "&result_type=mixed&include_entities=" + include_entities + "&count=" + tweetcount);
                } else {
                    //use max_id to get the tweets in the next page. Use max_id-1 to avoid getting redundant tweets.
                    url = new URL("https://api.twitter.com/1.1/search/tweets.json?q=" + query + "&result_type=mixed&include_entities=" + include_entities + "&count=" + tweetcount + "&max_id=" + (maxid - 1));
                }
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setReadTimeout(5000);
                consumer.sign(huc);
                huc.connect();
                if (huc.getResponseCode() == 400 || huc.getResponseCode() == 404) {
                    System.out.println(huc.getResponseCode());
                    break;
                } else if (huc.getResponseCode() == 500 || huc.getResponseCode() == 502 || huc.getResponseCode() == 503) {
                    try {
                        System.out.println(huc.getResponseCode());
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TwitterApi.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else // Step 3: If the requests have been exhausted, then wait until the quota is renewed
                if (huc.getResponseCode() == 429) {
                    try {
                        huc.disconnect();
                        long time = this.getWaitTime(APIType.SEARCH);
                        System.out.println("Wating for " + (time / (1000 * 60 ) ) + " mins .. ");
                        Thread.sleep(time);
                        continue;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getInputStream()));
                StringBuilder content = new StringBuilder();
                String temp = "";
                while ((temp = bRead.readLine()) != null) {
                    content.append(temp);
                }
                try {
                    JSONArray statuses = new JSONArray();
                    JSONObject json = (JSONObject) JSONSerializer.toJSON(content.toString());
                    JSONArray statusarr = json.getJSONArray("statuses");
                    
                    if (statusarr.isEmpty()) {
                        break;
                    }
                    for (int i = 0; i < statusarr.size(); i++) {
                        JSONObject jobj = statusarr.getJSONObject(i);
                        Date created_at = DateUtils.getDate(jobj.getString("created_at"));
                        if (created_at != null) {
                            jobj.put("timestamp", created_at.getTime());
                        }

                        jobj.put("type", "tweet");
                        //El current timestamp
                        jobj.put("current_timestamp", new Date().getTime());
                        //La categoria
                        jobj.put("category", query);

                        statuses.add(jobj);
                        //Get the max_id to get the next batch of tweets
                        //if(!jobj.isNull("id"))
                        if (!(jobj.get("id") == null)) {
                            maxid = jobj.getLong("id");
                        }
                    }
                    
                    total += statuses.size();

                    new Thread(new TweetSaver(twitterdb, statuses, debug)).start();

                    System.out.println("Processing block " + block + " with " + statuses.size() + " tweets... ");
                    block++;
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (OAuthCommunicationException ex) {
            ex.printStackTrace();
        } catch (OAuthMessageSignerException ex) {
            ex.printStackTrace();
        } catch (OAuthExpectationFailedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return total;
    }

    /**
     * Retrieved the status messages of a user
     *
     * @param username the name of the user whose status messages need to be
     * retrieved
     * @return a list of status messages
     */
    public int getStatuses(String username) {
        BufferedReader bRead = null;
        //Get the maximum number of tweets possible in a single page 200
        int tweetcount = 200;
        //Include include_rts because it is counted towards the limit anyway.
        boolean include_rts = true;

        int total = 0;
        int block = 1;

        try {
            System.out.println("Processing status messages of " + username);
            long maxid = 0;
            while (true) {
                URL url = null;
                if (maxid == 0) {
                    url = new URL("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + username + "&include_rts=" + include_rts + "&count=" + tweetcount);
                } else {
                    //use max_id to get the tweets in the next page. Use max_id-1 to avoid getting redundant tweets.
                    url = new URL("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + username + "&include_rts=" + include_rts + "&count=" + tweetcount + "&max_id=" + (maxid - 1));
                }
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setReadTimeout(5000);
                consumer.sign(huc);
                huc.connect();
                if (huc.getResponseCode() == 400 || huc.getResponseCode() == 404) {
                    System.out.println(huc.getResponseCode());
                    break;
                } else if (huc.getResponseCode() == 500 || huc.getResponseCode() == 502 || huc.getResponseCode() == 503) {
                    try {
                        System.out.println(huc.getResponseCode());
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TwitterApi.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else // Step 3: If the requests have been exhausted, then wait until the quota is renewed
                if (huc.getResponseCode() == 429) {
                    try {
                        huc.disconnect();
                        long time = this.getWaitTime("/statuses/user_timeline");
                        System.out.println("Wating for " + (time / (1000 * 60 ) ) + " mins .. ");
                        Thread.sleep(time);
                        continue;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getInputStream()));
                StringBuilder content = new StringBuilder();
                String temp = "";
                while ((temp = bRead.readLine()) != null) {
                    content.append(temp);
                }
                try {
                    //JSONArray statusarr = new JSONArray(content.toString());
                    JSONArray statuses = new JSONArray();
                    JSONArray statusarr = (JSONArray) JSONSerializer.toJSON(content.toString());
                    if (statusarr.isEmpty()) {
                        break;
                    }
                    for (int i = 0; i < statusarr.size(); i++) {
                        JSONObject jobj = statusarr.getJSONObject(i);
                        Date created_at = DateUtils.getDate(jobj.getString("created_at"));
                        if (created_at != null) {
                            jobj.put("timestamp", created_at.getTime());
                        }

                        jobj.put("type", "tweet");
                        //El current timestamp
                        jobj.put("current_timestamp", new Date().getTime());
                        //La categoria
                        jobj.put("category", username);

                        statuses.add(jobj);
                            //statuses.put(jobj);
                        //Get the max_id to get the next batch of tweets
                        //if(!jobj.isNull("id"))
                        if (!(jobj.get("id") == null)) {
                            maxid = jobj.getLong("id");
                        }
                    }

                    total += statuses.size();

                    new Thread(new TweetSaver(twitterdb, statuses)).start();

                    System.out.println("Processing block " + block + " with " + statuses.size() + " tweets... ");
                    block++;
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (OAuthCommunicationException ex) {
            ex.printStackTrace();
        } catch (OAuthMessageSignerException ex) {
            ex.printStackTrace();
        } catch (OAuthExpectationFailedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return total;
    }

    /**
     * Retrieves the wait time if the API Rate Limit has been hit
     *
     * @param api the name of the API currently being used
     * @return the number of milliseconds to wait before initiating a new
     * request
     */
    public long getWaitTime(String api) {
        JSONObject jobj = this.getRateLimitStatus();
        if (jobj != null) {
            try {
                if (!(jobj.get("resources") == null)) //if(!jobj.isNull("resources"))
                {
                    JSONObject resourcesobj = jobj.getJSONObject("resources");
                    JSONObject apilimit = null;
                    if (api.equals(APIType.USER_TIMELINE)) {
                        JSONObject statusobj = resourcesobj.getJSONObject("statuses");
                        apilimit = statusobj.getJSONObject(api);
                    } else if (api.equals(APIType.FOLLOWERS)) {
                        JSONObject followersobj = resourcesobj.getJSONObject("followers");
                        apilimit = followersobj.getJSONObject(api);
                    } else if (api.equals(APIType.FRIENDS)) {
                        JSONObject friendsobj = resourcesobj.getJSONObject("friends");
                        apilimit = friendsobj.getJSONObject(api);
                    } else if (api.equals(APIType.USER_PROFILE)) {
                        JSONObject userobj = resourcesobj.getJSONObject("users");
                        apilimit = userobj.getJSONObject(api);
                    } else if (api.equalsIgnoreCase(APIType.SEARCH)){
                          JSONObject userobj = resourcesobj.getJSONObject("search");
                        apilimit = userobj.getJSONObject(api);
                    }
                    int numremhits = apilimit.getInt("remaining");
                    if (numremhits <= 1) {
                        long resettime = apilimit.getInt("reset");
                        resettime = resettime * 1000; //convert to milliseconds
                        return resettime;
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return 0;
    }

    public JSONObject getRateLimitStatus(String api) {
        JSONObject jobj = this.getRateLimitStatus();
        if (jobj != null) {
            try {
                if (!(jobj.get("resources") == null)) //if(!jobj.isNull("resources"))
                {
                    JSONObject resourcesobj = jobj.getJSONObject("resources");
                    if (api.equals(APIType.USER_TIMELINE)) {
                        return resourcesobj.getJSONObject("statuses").getJSONObject(api);
                    } else if (api.equals(APIType.FOLLOWERS)) {
                        return resourcesobj.getJSONObject("followers").getJSONObject(api);
                    } else if (api.equals(APIType.FRIENDS)) {
                        return resourcesobj.getJSONObject("friends").getJSONObject(api);
                    } else if (api.equals(APIType.USER_PROFILE)) {
                        return resourcesobj.getJSONObject("users").getJSONObject(api);
                    } else if (api.equalsIgnoreCase(APIType.SEARCH)){
                          return resourcesobj.getJSONObject("search").getJSONObject(api);
                    }
                    /*
                    int numremhits = apilimit.getInt("remaining");
                    if (numremhits <= 1) {
                        long resettime = apilimit.getInt("reset");
                        resettime = resettime * 1000; //convert to milliseconds
                        return resettime;
                    } */
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Retrieves the rate limit status of the application
     *
     * @return
     */
    public JSONObject getRateLimitStatus() {
        try {
            URL url = new URL("https://api.twitter.com/1.1/application/rate_limit_status.json");
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setReadTimeout(5000);
            consumer.sign(huc);
            huc.connect();
            BufferedReader bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getContent()));
            StringBuffer page = new StringBuffer();
            String temp = "";
            while ((temp = bRead.readLine()) != null) {
                page.append(temp);
            }
            bRead.close();
            JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(page.toString());
            return jsonObject;
            //return (new JSONObject(page.toString()));
        } catch (JSONException ex) {
            Logger.getLogger(TwitterApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OAuthCommunicationException ex) {
            Logger.getLogger(TwitterApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OAuthMessageSignerException ex) {
            Logger.getLogger(TwitterApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OAuthExpectationFailedException ex) {
            Logger.getLogger(TwitterApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TwitterApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Retrives the profile information of the user
     *
     * @param username of the user whose profile needs to be retrieved
     * @return the profile information as a JSONObject
     */
    public JSONObject getProfile(String username) {
        BufferedReader bRead = null;
        JSONObject profile = null;
        try {
            System.out.println("Processing profile of " + username);
            boolean flag = true;
            URL url = new URL("https://api.twitter.com/1.1/users/show.json?screen_name=" + username);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setReadTimeout(5000);
            // Step 2: Sign the request using the OAuth Secret
            consumer.sign(huc);
            huc.connect();
            if (huc.getResponseCode() == 404 || huc.getResponseCode() == 401) {
                System.out.println(huc.getResponseMessage());
            } else if (huc.getResponseCode() == 500 || huc.getResponseCode() == 502 || huc.getResponseCode() == 503) {
                try {
                    huc.disconnect();
                    System.out.println(huc.getResponseMessage());
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } else // Step 3: If the requests have been exhausted, then wait until the quota is renewed
            if (huc.getResponseCode() == 429) {
                try {
                    huc.disconnect();
                    Thread.sleep(this.getWaitTime("/users/show/:id"));
                    flag = false;
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            if (!flag) {
                //recreate the connection because something went wrong the first time.
                huc.connect();
            }
            StringBuilder content = new StringBuilder();
            if (flag) {
                bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getContent()));
                String temp = "";
                while ((temp = bRead.readLine()) != null) {
                    content.append(temp);
                }
            }
            huc.disconnect();
            try {
                profile = (JSONObject) JSONSerializer.toJSON(content.toString());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        } catch (OAuthCommunicationException ex) {
            ex.printStackTrace();
        } catch (OAuthMessageSignerException ex) {
            ex.printStackTrace();
        } catch (OAuthExpectationFailedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return profile;
    }
}
