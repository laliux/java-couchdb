/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bb4.social.twitter.utils;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;
import java.io.IOException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author Eduardo Zarate
 */
public class TweetSaver implements Runnable{
    private final JSONArray data;
    private final String twitterdb;
    private final Boolean debug;
    
    public TweetSaver(String twitterdb, JSONArray data){
        this(twitterdb, data, false);
    }
    
    public TweetSaver(String twitterdb, JSONArray data, Boolean debug){
        this.twitterdb = twitterdb;
        this.data = data;
        this.debug = debug;
    }
    
    @Override
    public void run() {
        Session s = new Session("localhost",5984);
        Database db = s.getDatabase(twitterdb);
        
        for(int i=0; i<data.size(); i++){
            JSONObject obj  = data.getJSONObject(i);
            if(debug){
                System.out.print(obj.get("timestamp"));
                System.out.print(" : ");
                System.out.print(obj.get("created_at"));
                
                
                System.out.print(" : ");
                System.out.print(obj.get("id"));
                System.out.print(" : ");
                System.out.println(obj.get("text"));
                
            }
            
            Document doc = new Document( obj);

            
            if( db.saveDocument(doc) == false ){
                try{
                    Document source = db.getDocument(doc.getId());
                    doc.setRev(source.getRev());
                    
                    db.saveDocument(doc, doc.getId());
                }catch(IOException e){
                    //System.out.println(e);
                }
            }

        }
    }
    
}
