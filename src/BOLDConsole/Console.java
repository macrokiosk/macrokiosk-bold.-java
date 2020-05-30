package BOLDConsole;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Console {
     private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    
    public static void main(String[] args) throws Exception {
                
        Console obj = new Console();
        
        //SENDING SMS VIA REST HTTP GET
        System.out.println("Send HTTP GET request");
        obj.sendGet();

        //SENDING SMS VIA SOAP WEB SERVICE
        System.out.println("Send HTTP POST request");
        obj.sendPost();
                
    }
    
    private void sendGet() throws Exception {
        String requestUri1 = String.format("https://www.etracker.cc/bulksms/send?user=%1$s&pass=%2$s&type=%3$s&to=%4$s&from=%5$s&text=%6$s&servid=%7$s",
                    "username",
                    "password",
                    "0",
                    "60123456789",
                    "from",
                    "test",
                    "serviceid");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(requestUri1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());

    }

    private void sendPost() throws Exception {

        //Build the request json
        String json = "{\"user\":\"username\"," +
            "\"pass\":\"password\"," +
            "\"type\":\"0\"," +
            "\"to\":\"60123456789\"," +
            "\"from\":\"from\"," +
            "\"text\":\"test\"," +
            "\"servid\":\"serviceid\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://www.etracker.cc/bulksms/send"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());

    }    
}
