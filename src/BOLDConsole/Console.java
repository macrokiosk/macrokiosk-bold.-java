package BOLDConsole;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Supplier;

public class Console {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    private List<PartsSpecification> partsSpecificationList = new ArrayList<>();
    private String boundary = UUID.randomUUID().toString();
    
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
        
        //SENDING MMS VIA HTTP POST
        System.out.println("SENDING MMS VIA HTTP POST");
        obj.sendMMSPost();                
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
    
    private void sendMMSPost() throws Exception {
        Gson gson = new Gson();
        String recepient = gson.toJson(new String[] {"60103456789","60123456789"});
        String contentURL = gson.toJson("http://res.cloudinary.com/demo/image/upload/v1525209117/folder1/folder2/sample.jpg");
        
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS");  
        String strDate = dateFormat.format(date);
        String url = "http://mms.etracker.cc/MMSWebAPI/api/BulkMMS/" + strDate;
        
        Console publisher = new Console();
            publisher.addPart("user", "user");
            publisher.addPart("password", "password");
            publisher.addPart("serviceid", "serviceid");
            publisher.addPart("subject", "MMS Title");
            publisher.addPart("text", "MMS Text Message");
            publisher.addPart("recipients", recepient);
            publisher.addPart("iscontenturi", "1");
            publisher.addPart("content", contentURL);
            publisher.addPart("multimediafiletype", "0");

        HttpClient client = HttpClient.newBuilder().build();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "multipart/form-data; boundary=\"" + publisher.getBoundary() + "\"")
            .header("Accept","multipart/form-data")
            .timeout(Duration.ofMinutes(1))               
            .POST(publisher.build())
            .build();         
                
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofLines());
        System.out.println("Status code: " + response.statusCode());
    }
    
    //MMS Multipart Data Constructor 
    public HttpRequest.BodyPublisher build() {
        if (partsSpecificationList.size() == 0) {
            throw new IllegalStateException("Must have at least one part to build multipart message.");
        }
        addFinalBoundaryPart();
        return HttpRequest.BodyPublishers.ofByteArrays(PartsIterator::new);
    }

    public String getBoundary() {
        return boundary;
    }

    public Console addPart(String name, String value) {
        PartsSpecification newPart = new PartsSpecification();
        newPart.type = PartsSpecification.TYPE.STRING;
        newPart.name = name;
        newPart.value = value;
        partsSpecificationList.add(newPart);
        return this;
    }

    private void addFinalBoundaryPart() {
        PartsSpecification newPart = new PartsSpecification();
        newPart.type = PartsSpecification.TYPE.FINAL_BOUNDARY;
        newPart.value = "--" + boundary + "--";
        partsSpecificationList.add(newPart);
    }

    static class PartsSpecification {

        public enum TYPE {
            STRING, FILE, STREAM, FINAL_BOUNDARY
        }

        PartsSpecification.TYPE type;
        String name;
        String value;
        Path path;
        Supplier<InputStream> stream;
        String filename;
        String contentType;

    }

    class PartsIterator implements Iterator<byte[]> {

        private Iterator<PartsSpecification> iter;
        private InputStream currentFileInput;

        private boolean done;
        private byte[] next;

        PartsIterator() {
            iter = partsSpecificationList.iterator();
        }

        @Override
        public boolean hasNext() {
            if (done) return false;
            if (next != null) return true;
            try {
                next = computeNext();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            if (next == null) {
                done = true;
                return false;
            }
            return true;
        }

        @Override
        public byte[] next() {
            if (!hasNext()) throw new NoSuchElementException();
            byte[] res = next;
            next = null;
            return res;
        }

        private byte[] computeNext() throws IOException {
            if (currentFileInput == null) {
                if (!iter.hasNext()) return null;
                PartsSpecification nextPart = iter.next();
                if (PartsSpecification.TYPE.STRING.equals(nextPart.type)) {
                    String part =
                            "--" + boundary + "\r\n" +
                            "Content-Type: text/plain; charset=utf-8\r\n" +
                            "Content-Disposition: form-data; name=" + nextPart.name + "\r\n\r\n" +
                            nextPart.value + "\r\n";
                    return part.getBytes(StandardCharsets.UTF_8);
                }
                if (PartsSpecification.TYPE.FINAL_BOUNDARY.equals(nextPart.type)) {
                    return nextPart.value.getBytes(StandardCharsets.UTF_8);
                }
                String filename;
                String contentType;
                if (PartsSpecification.TYPE.FILE.equals(nextPart.type)) {
                    Path path = nextPart.path;
                    filename = path.getFileName().toString();
                    contentType = Files.probeContentType(path);
                    if (contentType == null) contentType = "application/octet-stream";
                    currentFileInput = Files.newInputStream(path);
                } else {
                    filename = nextPart.filename;
                    contentType = nextPart.contentType;
                    if (contentType == null) contentType = "application/octet-stream";
                    currentFileInput = nextPart.stream.get();
                }
                String partHeader =
                        "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=" + nextPart.name + "; filename=" + filename + "\r\n" +
                        "Content-Type: " + contentType + "\r\n\r\n";
                return partHeader.getBytes(StandardCharsets.UTF_8);
            } else {
                byte[] buf = new byte[8192];
                int r = currentFileInput.read(buf);
                if (r > 0) {
                    byte[] actualBytes = new byte[r];
                    System.arraycopy(buf, 0, actualBytes, 0, r);
                    return actualBytes;
                } else {
                    currentFileInput.close();
                    currentFileInput = null;
                    return "\r\n".getBytes(StandardCharsets.UTF_8);
                }
            }
        }
    }

    
}
