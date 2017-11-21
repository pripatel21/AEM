package com.dish.components.core.services;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpHeaders;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by narreddy on 11/13/17.
 */
public class JSONArraySample {

    public void emailJson() {
        try {
            JSONObject emailJson = new JSONObject();
            emailJson.put("clickthroughCount", String.valueOf((Object) null));

            JSONArray jsonArray = new JSONArray();
            JSONObject contacts = new JSONObject();
            contacts.put("emailAddress", "narreddy@lexmark.com");
            contacts.put("id", "1411");
            jsonArray.put(contacts);
            emailJson.put("contacts", jsonArray);

            emailJson.put("openCount", String.valueOf((Object) null));
            emailJson.put("sendFromUserId", String.valueOf((Object) null));

            JSONObject emailBody = new JSONObject();
            emailBody.put("type", "RawHtmlContent");
            emailBody.put("html", "<html><head></head><body>sample</body></html>");

            JSONObject htmlContent = new JSONObject();
            htmlContent.put("htmlContent", emailBody);
            htmlContent.put("id", String.valueOf((Object) null));
            htmlContent.put("isPlainTextEditable", false);
            htmlContent.put("name", "Sample Email");
            htmlContent.put("plainText", String.valueOf((Object) null));
            htmlContent.put("sendPlainTextOnly", false);
            htmlContent.put("subject", "Sample");
            emailJson.put("email", htmlContent);

            emailJson.put("endAt", "0001-01-01T05:00:00Z");
            emailJson.put("failedSendCount", String.valueOf((Object) null));
            emailJson.put("name", "Sample Deployment");
            emailJson.put("sentSubject", String.valueOf((Object) null));
            emailJson.put("successfulSendCount", String.valueOf((Object) null));
            emailJson.put("type", "EmailInlineDeployment");
            System.out.println(emailJson.toString());
        } catch(JSONException je) {
            je.printStackTrace();
        }
    }

    public static String base64Encoding() {
        String authHeader = "Lexmark" + '\\' + "PSWWeb.Ops"
                + ':' + "Campa1gn$";
        String encodedHeader = new String(Base64.encodeBase64(authHeader.getBytes()));
        System.out.println(encodedHeader);
        return encodedHeader;
    }

    public static String getFromEloqua() throws Exception {
        String result = null;
        String getUrl = "https://secure.eloqua.com/api/REST/2.0/data/contacts/list/11062";
        Integer statusCode = 0;
        HttpClient httpClient = new HttpClient();
        HttpMethod getRequest = null;

        try {
            getRequest = new GetMethod(getUrl);
            getRequest.addRequestHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            getRequest.addRequestHeader(HttpHeaders.AUTHORIZATION, "Basic " + base64Encoding());
            statusCode = httpClient.executeMethod(getRequest);
            String responseBody = getStringFromInputStream(getRequest.getResponseBodyAsStream());
            if (statusCode.toString().startsWith("2") || statusCode.toString().startsWith("3")) {
                org.apache.sling.commons.json.JSONObject res = new org.apache.sling.commons.json.JSONObject(responseBody);
                result = res.toString();
            } else {
                throw new Exception("Error in Getting data: Status Code :" + statusCode + "ID: " + getUrl + result);
            }
        } finally {
            getRequest.releaseConnection();
        }
        return result;
    }


    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static void main(String[] naren) throws Exception {
        /*String response = getFromEloqua();
        //System.out.println("Response JSON => " + response);
        JSONObject responseJSON = new JSONObject(response);
        String elements = responseJSON.get("elements").toString();
        JSONArray elementsArray = new JSONArray(elements);
        //System.out.println("Elements Array => " + elementsArray);
        JSONObject emailDetsJSON = null;
        JSONArray contactsArray = new JSONArray();
        boolean found = false;

        System.out.println("Array Length => " + elementsArray.length());
        for(int i=0; i<elementsArray.length(); i++) {
            emailDetsJSON = new JSONObject(elementsArray.get(i).toString());
            JSONObject contactANDIds = new JSONObject();
            //System.out.println(emailDetsJSON.toString());
            for(int j=0; j<emailDetsJSON.length(); j++) {
                //System.out.println(emailDetsJSON.get("id"));
                if(emailDetsJSON.has("id")) {
                    found = true;
                }
                if(emailDetsJSON.has("name")) {
                    found = true;
                }
                System.out.println("Found is => " + found);
                if(found) {
                    contactANDIds.put("id", emailDetsJSON.get("id"));
                    contactANDIds.put("email", emailDetsJSON.get("name"));
                    break;
                }
            }
            System.out.println(contactANDIds.toString());
            contactsArray.put(contactANDIds);
        }
        System.out.println(contactsArray.toString());

        String html = "<thead><tr><th>Original Name</th><th>Asset</th></thead>";
        System.out.println(html.replaceAll("\\/", "/"));

        String path = "/content/dam/lexmark/documents/data-sheet/y2017/Global-Marketing-Org-Charts-Rev-06182015.pdf";
        System.out.println(path.substring(path.lastIndexOf("/") + 1));

        syncTime();*/
        buildEmailMarkup();
    }

    public static void buildEmailMarkup() {

        HashMap<String, String> modifiedMap = new HashMap<String, String>();
        modifiedMap.put("/content/naren", "Name");

        StringBuilder emailMarkup = new StringBuilder();

        emailMarkup.append("<html><body><table>");
        emailMarkup.append("<thead><tr><th>Original Name</th><th>Status</th></tr></thead>");

        SortedSet<String> keys = new TreeSet<String>();
        keys.addAll(modifiedMap.keySet());

        for (String key : keys) {
            System.out.println("Keys => \n" + key);
            StringBuilder uri = new StringBuilder();

            emailMarkup.append("<tr><td>");
            emailMarkup.append("<a href='" + key + "'>");
            emailMarkup.append(modifiedMap.get(key));
            emailMarkup.append("</a>");
            emailMarkup.append("</td><td>");
            emailMarkup.append("New");
            emailMarkup.append("</td></tr>");
        }
        emailMarkup.append("</table></body></html>");
        String htmlEmailMarkup = emailMarkup.toString().replaceAll("\\/", "/");
        System.out.println("Email Markup => \n" + htmlEmailMarkup);

    }

    private static String syncTime() {
        String syncTime = null;
        Instant instant = Instant.now();
        System.out.println("Instant Time is => " + instant);
        syncTime = instant.minus(24, ChronoUnit.HOURS).toString();
        System.out.println("Sync Time is => " + syncTime);
        return syncTime;
    }
}
