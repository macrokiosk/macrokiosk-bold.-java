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
        System.out.println("SENDING SMS VIA REST HTTP GET");
        obj.sendSMSGet();

        //SENDING SMS VIA REST HTTP POST
        System.out.println("SENDING SMS VIA REST HTTP POST");
        obj.sendSMSPost();
        
        //SENDING EMAIL VIA HTTP POST
        System.out.println("SENDING EMAIL VIA HTTP POST");
        obj.sendEmailPost();
                
    }
    
    private void sendSMSGet() throws Exception {
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

    private void sendSMSPost() throws Exception {

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
    
    private void sendEmailPost() throws Exception {
        
        //Build the request json
        String json = "{\"to\":[{\"name\":\"Recipient\",\"email\":\"Recipient@macrokiosk.com\"}],"+ 
            "\"sender\":{\"name\":\"Sender\",\"email\":\"Sender@macrokiosk.com\"},"+
            "\"htmlContent\":\"Email Test Content\"," +
            "\"subject\":\"Email Test Subject\"," +
            "\"replyTo\":{\"name\":\"ReplyTo\",\"email\":\"ReplyTo@macrokiosk.com\"}," +
            "\"unsubscribeLink\":1," +
            "\"username\":\"username\"," +
            "\"pass\":\"password\"," +
            "\"serviceId\":\"serviceId\","+
            "\"IsHashed\":false}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://www.etracker.cc/BulkEmail/Send"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}
