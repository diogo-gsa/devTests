package httpServer;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.xml.ws.Dispatch;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class App {

    private static int teste = 1;
    private static int __PORT__ = 62490;
    
    
    public static void main(String[] args) throws Exception { 
        
       
        HttpServer server = HttpServer.create(new InetSocketAddress(__PORT__), 0);
       
        // Chart WebPage Init Handlers
        server.createContext("/chart", new ChartPageRequestHandler());
        server.createContext("/amcharts/", new LibsRequestHandler());
        server.createContext("/amcharts/images/", new ImagesRequestHandler());
        
        // Chart API Handlers
        server.createContext("/getRecentData", new GetRecentDataRequestHandler());

         
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server is running at: "+server.getAddress());
               
    }
 

    static class GetRecentDataRequestHandler implements HttpHandler{
        public void handle(HttpExchange t) throws IOException {
            Sensor sensorLibrary = new Sensor("https://172.20.70.232/reading", "root", "root");            
            double value = sensorLibrary.getNewMeasure().getTotalPower();                        
            long ts = sensorLibrary.getNewMeasure().geTimestamp()*1000;            
            String response = "{ \"ts\": \""+ts+"\", \"reading\": \""+value+"\"}";            
            //System.out.println("Stream Tuple >> ts= "+ts+"|"+"value= "+value);  //DEBUG
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
    
    
}