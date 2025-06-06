package com.scm.helpers;


import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Helper {

    public static String getEmailOfLoggedInUser(Authentication authentication)
    {
        if(authentication instanceof OAuth2AuthenticationToken)
        {
            var aOAuth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
            var clientId = aOAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

            var oauth2User = (OAuth2User)authentication.getPrincipal();
            String userName = "";

            if(clientId.equalsIgnoreCase("google"))
            {
                System.out.println("getting email from google");
                userName = oauth2User.getAttribute("email").toString();
            }
            else if(clientId.equalsIgnoreCase("github"))
            {
                System.out.println("getting email from github");
                userName = oauth2User.getAttribute("email")!=null? oauth2User.getAttribute("email"):oauth2User.getAttribute("login").toString()+"@gmail.com";
            }
            return userName;
        }
        else
        {
            System.out.println("getting data from external source");
            return authentication.getName();
        }
       
    }

    public static String getLinkForEmailVerification(String emailToken)
    {
        String link = "http://localhost:8080/auth/verify-email?token="+ emailToken;
        
        return link;
    }
}
