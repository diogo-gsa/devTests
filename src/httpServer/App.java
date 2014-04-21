package httpServer;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.xml.ws.Dispatch;

import sun.org.mozilla.javascript.internal.ast.CatchClause;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class App {

    private static int teste = 1;
    private static int __PORT__ = 62491;
    
    
    public static void main(String[] args) throws Exception { 
        
       
        HttpServer server = HttpServer.create(new InetSocketAddress(__PORT__), 0);
       
        // Chart WebPage Init Handlers
        server.createContext("/", new ChartPageRequestHandler());
        server.createContext("/amcharts/", new LibsRequestHandler());
        server.createContext("/amcharts/images/", new ImagesRequestHandler());
        
        // Chart API Handlers
        // example:  /getLastReading?idSensor=library        
        server.createContext("/getLastReading", new getLastReading());
                 
        // example:  /getHistoricReading?idSensor=library                
        server.createContext("/getLibraryHistoricReading", new getLibraryHistoricReading());
        
        
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server is running at: "+server.getAddress());
               
    }
    
  //getLastReading(SensorId sensor, List<DatapointAddress> datapoints)
    static class getLibraryHistoricReading implements HttpHandler{
        public void handle(HttpExchange t) throws IOException {
            // example:  /getLastReading?idSensor=library
            //String parametersListURI = t.getRequestURI().getQuery();    // get the "idSensor=library" URL's part            
            //String sensorId = parametersListURI.split("=")[1];          //extract resource name (eg. amcharts.js)
            
            String getResult = "";
            String numberOfReadings = "20";
            try {
                getResult = sendHTTP_GET_Request("http://193.136.166.131:62490/library/last/"+numberOfReadings);
                dispacthRequest(t, getResult);                         
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            //String response = "{ \"sensorId(location)\": \""+sensorId+"\", \"ts\": \""+ts+"\", \"reading\": \""+value+"\"}";            
            //System.out.println("Stream Tuple >>"+" location="+sensorId+"  ts="+ts+"  value= "+value);  //DEBUG
            //dispacthRequest(t, response);         
        }
    }  
    
    //getLastReading(SensorId sensor, List<DatapointAddress> datapoints)
    static class getLastReading implements HttpHandler{
        public void handle(HttpExchange t) throws IOException {
            // example:  /getLastReading?idSensor=library
            String parametersListURI = t.getRequestURI().getQuery();    // get the "idSensor=library" URL's part            
            String sensorId = parametersListURI.split("=")[1];          //extract resource name (eg. amcharts.js)
            SensorDriver sensor = null;
            switch(sensorId){
                case "library"          : sensor = new SensorDriver("https://172.20.70.232/reading", "root", "root"); break;
                case "kernel_14"        : sensor = new SensorDriver("https://172.20.70.229/reading", "root", "root"); break;
                case "kernel_16"        : sensor = new SensorDriver("https://172.20.70.238/reading", "root", "root"); break;
                case "room_1.17"        : sensor = new SensorDriver("https://172.20.70.234/reading", "root", "root"); break;
                case "room_1.19"        : sensor = new SensorDriver("https://172.20.70.235/reading", "root", "root"); break;                
                case "UTA_A4"           : sensor = new SensorDriver("https://172.20.70.237/reading", "root", "root"); break;                
                case "amphitheater_A4"  : sensor = new SensorDriver("https://172.20.70.231/reading", "root", "root"); break;
                case "amphitheater_A5"  : sensor = new SensorDriver("https://172.20.70.233/reading", "root", "root"); break;
                case "laboratory_1.58"  : sensor = new SensorDriver("https://172.20.70.236/reading", "root", "root"); break;
                default                 : System.out.println("*** Error ***: This sensor does NOT exist");
            }            
            double value = sensor.getNewMeasure().getTotalPower();                        
            long ts = sensor.getNewMeasure().geTimestamp()*1000;            
            String response = "{ \"sensorId(location)\": \""+sensorId+"\", \"ts\": \""+ts+"\", \"reading\": \""+value+"\"}";            
            System.out.println("Stream Tuple >>"+" location="+sensorId+"  ts="+ts+"  value= "+value);  //DEBUG
            dispacthRequest(t, response);         
        }
    }  
        
    static class ChartPageRequestHandler implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String response = readFile("src/httpServer/chartTests.html");
            System.out.println("Loaded: "+"chartTests.html");            
            dispacthRequest(t, response);            
        }
    }
    
    static class LibsRequestHandler implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String requestURI = t.getRequestURI().getPath(); //*all* URI requests
            String requestedFile = requestURI.split("/")[2]; //extract resource name (eg. amcharts.js)
            String response = "";
            switch(requestedFile){
                case "style.css"    :
                case "amstock.js"   :
                case "serial.js"    :
                case "amcharts.js"  :   response = readFile("src/httpServer/amstockchartLib/amcharts/"+requestedFile); 
                                        System.out.println("Loaded: "+requestedFile); break;
            }
            dispacthRequest(t, response);
        }
    }
    
    static class ImagesRequestHandler implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {            
            String requestURI = t.getRequestURI().getPath(); //*all* URI requests
            String requestedFile = requestURI.split("/")[3]; //extract resource name (eg. amcharts.js)
            byte[] response = readImageGIF("src/httpServer/amstockchartLib/amcharts/images/"+requestedFile);
            System.out.println("Loaded: "+requestedFile); 
            dispacthRequest(t, response);
        
        }
    }
    
    
   private static void dispacthRequest(HttpExchange t, String response) throws IOException {
       t.sendResponseHeaders(200, response.length());
       OutputStream os = t.getResponseBody();
       os.write(response.getBytes());
       os.close();
       
   }
   
   private static void dispacthRequest(HttpExchange t, byte[] response) throws IOException {
       t.sendResponseHeaders(200, response.length);
       OutputStream os = t.getResponseBody();
       os.write(response);
       os.close();
   }
    
    
    private static String readFile(String filename){
        
        //String filename = "src/httpServer/index.html";
        BufferedReader br = null;
        String response = null;
        StringBuilder sb = new StringBuilder();
        
        try {
            //System.out.println(System.getProperty("user.dir")); //DEBUG 
            br = new BufferedReader(new FileReader(filename));         
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            response = sb.toString();
        }catch(IOException e){
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }
    
    private static byte[] readImageGIF(String filename){
       
        ByteArrayOutputStream baos = null;
        try {
            File fnew=new File(filename);
            BufferedImage originalImage=ImageIO.read(fnew);
            baos=new ByteArrayOutputStream();
            ImageIO.write(originalImage, "gif", baos );
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] imageInByte=baos.toByteArray();
        return imageInByte;
        
    }
    
    private static String sendHTTP_GET_Request(String url) throws Exception {
        
        String USER_AGENT = "Mozilla/5.0";
        
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
        // optional default is GET
        con.setRequestMethod("GET");
 
        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
 
        int responseCode = con.getResponseCode();
        //System.out.println("\nSending 'GET' request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);
 
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
 
        while ((inputLine = in.readLine()) != null) {
            inputLine = inputLine + "\n";
            response.append(inputLine);
        }
        in.close();
 
        //print result
        //System.out.println(response.toString());
        return response.toString();
 
    }
    
}