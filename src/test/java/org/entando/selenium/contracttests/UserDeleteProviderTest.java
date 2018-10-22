package org.entando.selenium.contracttests;

import au.com.dius.pact.consumer.dsl.PactDslResponse;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.SetCookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.entando.selenium.pages.DTUserProfileTypePage;
import org.entando.selenium.tests.DTUserProfileTypeAddTest;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.Request;
import unfiltered.request.POST;

import javax.swing.text.html.parser.Entity;
import java.beans.Expression;
import java.util.HashMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.entando.selenium.contracttests.PactUtil.buildGetProfileTypes;

@Provider("UserDeleteProvider")
@PactFolder("target/pacts")
public class UserDeleteProviderTest {
    private static String accesToken;
    private static Header sessionId;

    @BeforeAll
    public static  void login() throws IOException {
        HttpPost post = new HttpPost( "http://localhost:8080/entando/OAuth2/access_token");
        post.addHeader("Origin", "http://localhost:5000");
        post.addHeader("Accept-Encoding","gzip, deflate, br");
        post.addHeader("Host","localhost:8080");
        post.addHeader("Accept-Language","en-GB,en-US;q=0.9,en;q=0.8");
        post.addHeader("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        post.addHeader("Referer","http://localhost:5000/");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded");
        post.addHeader("Connection", "keep-alive");
        post.addHeader("Accept", "*/*");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username","admin"));
        params.add(new BasicNameValuePair("password","adminadmin"));
        params.add(new BasicNameValuePair("grant_type","password"));
        params.add(new BasicNameValuePair("client_id","true"));
        params.add(new BasicNameValuePair("client_secret","true"));
        post.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = HttpClients.createDefault().execute(post);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        response.getEntity().writeTo(outstream);
        accesToken=new JSONObject(new String(outstream.toByteArray())).getString("access_token");
        sessionId=response.getFirstHeader("Set-Cookie");

        /*HttpPost postProfileType = new HttpPost( "http://localhost:8080/entando/api/profileTypes");

        String postData = "{\"code\": \"AXA\",\"name\": \"axa profile\",\"status\": \"0\"}";
        postProfileType.setEntity(new StringEntity(postData));
        postProfileType.addHeader("Authorization", "Bearer " + accesToken);
        postProfileType.addHeader("Origin", "http://localhost:5000");
        postProfileType.addHeader("Accept-Encoding","gzip, deflate, br");
        postProfileType.addHeader("Host","localhost:8080");
        postProfileType.addHeader("Accept-Language","en-GB,en-US;q=0.9,en;q=0.8");
        postProfileType.addHeader("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        postProfileType.addHeader("Referer","http://localhost:5000/");
        postProfileType.addHeader("Content-Type", "application/json");
        postProfileType.addHeader("Connection", "keep-alive");*/
        //postProfileType.addHeader("Accept", "*/*");

        //CloseableHttpResponse response2 = HttpClients.createDefault().execute(postProfileType);
        //System.out.println("\n" + response2 + "\n");

        /*HttpGet getProfileType = new HttpGet("http://localhost:8080/entando/api/profileTypes");
        getProfileType.addHeader("Authorization", "Bearer " + accesToken);
        getProfileType.addHeader("Origin", "http://localhost:5000");
        getProfileType.addHeader("Accept-Encoding","gzip, deflate, br");
        getProfileType.addHeader("Host","localhost:8080");
        getProfileType.addHeader("Accept-Language","en-GB,en-US;q=0.9,en;q=0.8");
        getProfileType.addHeader("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        getProfileType.addHeader("Referer","http://localhost:5000/");
        getProfileType.addHeader("Content-Type", "application/json");
        getProfileType.addHeader("Connection", "keep-alive");*/
        //getProfileType.addHeader("Accept", "*/*");
        /*List<NameValuePair> params2 = new ArrayList<NameValuePair>();
        params2.add(new BasicNameValuePair("page","1"));
        params2.add(new BasicNameValuePair("pageSize","10"));

        CloseableHttpResponse response3 = HttpClients.createDefault().execute(getProfileType);
        System.out.println("\n" + response3 + "\n");*/

    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    public void testTemplate(PactVerificationContext context, HttpRequest request) {
        // This will add a header to the request
        request.addHeader("Authorization", "Bearer " + accesToken);
        request.setHeader(sessionId);
        context.verifyInteraction();

    }

    @BeforeEach
    void before(PactVerificationContext context) throws MalformedURLException {
        context.setTarget(HttpTestTarget.fromUrl(new URL("http://localhost:8080")));
    }
}