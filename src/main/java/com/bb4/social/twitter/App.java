
package com.bb4.social.twitter;

import net.sf.json.JSONObject;

/**
 *
 * @author Eduardo Zarate @iLaliux
 */
public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        TwitterSetup twitterSetup = new TwitterSetup();
        Boolean debug = twitterSetup.inDebug();
        
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("--history")) {
                if (args[1] != null) {
                    String username = args[1];
                    TwitterApi api = new TwitterApi(twitterSetup, debug);

                    JSONObject user = api.getProfile(username);
                    if (user != null) {
                        System.out.print("Proccesing data for " + user.getString("screen_name") + ", id: " + user.getString("id") + ", name: ");
                        System.out.println(user.getString("name"));

                        Integer nTweets = api.getStatuses(username);
                        System.out.println(nTweets + " tweets.");
                    }

                }
            }
            if (args[0].equalsIgnoreCase("-s") || args[0].equalsIgnoreCase("--search")) {
                if (args[1] != null) {
                    String query = args[1];
                    TwitterApi api = new TwitterApi(twitterSetup, debug);

                    long max_id = 0;
                    if(args.length >= 4 && args[2].startsWith("--max")){
                        max_id = Long.parseLong(args[3]);
                    }
                    
                    Integer nTweets = api.getSearchResults(query, max_id);
                    System.out.println(nTweets + " tweets... using max_id=" + max_id);
                }
            }
            if (args[0].equalsIgnoreCase("-r") || args[0].equalsIgnoreCase("--rate-limit")) {
                if (args[1] != null) {
                    String query = args[1];
                    TwitterApi api = new TwitterApi(twitterSetup, debug);

                    JSONObject result = api.getRateLimitStatus(query);
                    System.out.println(result);
                }
            }            
        } else {
            System.out.println("\nUsar java -jar target/java-couchdb-1.0-SNAPSHOT-jar-with-dependencies.jar [parametros] [opciones]");
            System.out.println("Por ejemplo,");
            System.out.println("java -jar target/java-couchdb-1.0-SNAPSHOT-jar-with-dependencies.jar --rate-limit " + APIType.USER_TIMELINE );
            System.out.println("\n");
        }
    }

}
