/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bb4.social.twitter;

public class OAuthTokenSecret
{
    String userAccessToken;
    String userAccessSecret;

    public String getAccessSecret() {
        return userAccessSecret;
    }

    public void setAccessSecret(String AccessSecret) {
        this.userAccessSecret = AccessSecret;
    }

    public String getAccessToken() {
        return userAccessToken;
    }

    public void setAccessToken(String AccessToken) {
        this.userAccessToken = AccessToken;
    }

    public OAuthTokenSecret(String token,String secret)
    {
        this.setAccessToken(token);
        this.setAccessSecret(secret);
    }

    @Override
    public String toString()
    {
       return "Access Token: "+getAccessToken()+" Access Secret: "+getAccessSecret();
    }
}
