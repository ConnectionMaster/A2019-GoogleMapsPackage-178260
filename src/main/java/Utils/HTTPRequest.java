package Utils;
import org.apache.http.HttpEntity;
import java.io.FileOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

public class HTTPRequest {

    public static String GET(String url) throws IOException {
        URL urlForGET = new URL(url);
        String readLine;
        String output="";
        HttpURLConnection connection = (HttpURLConnection) urlForGET.openConnection();
        connection.setRequestMethod("GET");
        //connection.setRequestProperty("Content-Type", "application/json");
        //connection.setRequestProperty("Authorization", auth);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            output = response.toString();
            //return null;
        }
        return output;
    }

    public static void GETpng(String url, String path) throws IOException{
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet(url);

        request.addHeader("Accept", "*/*");
        try (CloseableHttpResponse response =  httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (FileOutputStream outstream = new FileOutputStream(path)) {
                    entity.writeTo(outstream);
                }
            }
        }

    }

    public static String POST(String auth, String url, String body, String DRKey) throws IOException {
        URL urlForGetRequest = new URL(url);
        String readLine;
        String output="";
        HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", auth);
        connection.setRequestProperty("datarobot-key", DRKey);
        //Include JSON Body in POST request
        connection.setDoOutput(true);
        OutputStream outStream = connection.getOutputStream();
        OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, StandardCharsets.UTF_8);
        outStreamWriter.write(body);
        outStreamWriter.flush();
        outStreamWriter.close();
        outStream.close();
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            output = response.toString();
        }
        return output;
    }
}
