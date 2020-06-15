package BOLDTag;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Tag {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    
    public static void main(String[] args) throws Exception {
        Tag obj = new Tag();
        //Logon API - To get Token
        System.out.println("TO GET TOKEN VIA REST HTTP POST");
        String token = obj.getToken();
        
        //Dispatch API - To send SMS
        if(token != "")
        {
            System.out.println("TO SEND SMS VIA REST HTTP POST");
            obj.sendSMS(token);
        }
    }
    
    private String getToken() throws Exception { 
        //Build the request json
        Gson gson = new Gson();
        Credential cre = new Credential("username","password");
        String json = gson.toJson(cre);
                
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://host.boldtag.net/api/ext/logon"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());       
    
        Token tokenResponse = gson.fromJson(response.body(), Token.class);
        if(tokenResponse != null)
        {
            String tokenValue = tokenResponse.token;
            return tokenValue;
        }
        else
            return "";
    }
    
    private void sendSMS(String Token) throws Exception { 
        Gson gson = new Gson();
        String ActivityKey = "7Oub6Apzu4EGvI3XD9OkmQyncXToEAAA";
        Recepient recp = new Recepient("Max","60123456789");
        Send send = new Send(ActivityKey, recp );
        String json = gson.toJson(send);
        
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://host.boldtag.net/api/ext/Dispatch"))
                .header("Content-Type", "application/json")
                .setHeader("token", Token)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
        
    }


}


