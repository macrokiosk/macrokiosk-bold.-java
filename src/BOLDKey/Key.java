package BOLDKey;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Key {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    
    public static void main(String[] args) throws Exception {
                
        Key obj = new Key();
        
        //SENDING BOLD.Key SMS OTP VIA REST HTTP GET
        System.out.println("SENDING BOLD.Key SMS OTP VIA REST HTTP GET");
        obj.sendOTPGet();

        //VALIDATE BOLD.Key SMS OTP VIA REST HTTP GET
        System.out.println("VALIDATE BOLD.Key SMS OTP VIA REST HTTP GET");
        obj.validateOTPGet();
        
        //SENDING BOLD.Key SMS OTP VIA REST HTTP POST
        System.out.println("SENDING BOLD.Key SMS OTP VIA REST HTTP POST");
        obj.sendOTPPost();
        
        //VALIDATE BOLD.Key SMS OTP VIA REST HTTP POST
        System.out.println("VALIDATE BOLD.Key SMS OTP VIA REST HTTP POST");
        obj.validateOTPPost();
                
    }
    
    private void sendOTPGet() throws Exception {
        String requestUri1 = String.format("https://secure.etracker.cc/MobileOTPAPI/OTPGenerateAPI.aspx?user=%1$s&pass=%2$s&type=%3$s&to=%4$s&from=%5$s&text=%6$s&servid=%7$s",
                    "username",
                    "password",
                    "0",
                    "60123456789",
                    "from",
                    "BOLD.Key+SMS+OTP+%3cOTPCode%3e",
                    "serviceid");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(requestUri1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
    
    private void validateOTPGet() throws Exception {   
        String requestUri1 = String.format("https://secure.etracker.cc/MobileOTPAPI/OTPVerifyAPI.aspx?user=%1$s&pass=%2$s&to=%3$s&from=%4$s&servid=%5$s&pincode=%6$s",
                    "username",
                    "password",
                    "60123456789",
                    "from",
                    "servid",
                    "1111");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(requestUri1))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
    
    private void sendOTPPost() throws Exception { 
        //Build the request json
        String json = "{\"user\":\"username\"," +
            "\"pass\":\"password\"," +
            "\"type\":\"0\"," +
            "\"to\":\"60123456789\"," +
            "\"from\":\"from\"," +
            "\"text\":\"BOLD.Key+SMS+OTP+%3cOTPCode%3e\"," +
            "\"servid\":\"serviceid\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://secure.etracker.cc/MobileOTPAPI/SMSOTP/OTPGenerate"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
    
    private void validateOTPPost() throws Exception {     
        //Build the request json
        String json = "{\"user\":\"username\"," +
            "\"pass\":\"password\"," +
            "\"to\":\"60123456789\"," +
            "\"from\":\"from\"," +
            "\"servid\":\"servid\"," +
            "\"pincode\":\"1111\"}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://secure.etracker.cc/MobileOTPAPI/SMSOTP/OTPVerify"))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}
