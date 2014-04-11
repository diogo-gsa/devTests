package httpServer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class App {

    private static int teste = 1;
    private static int __PORT__ = 62490;
    
    
    public static void main(String[] args) throws Exception { 
        
       
        HttpServer server = HttpServer.create(new InetSocketAddress(__PORT__), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/web", new WebPageHandler());
        server.createContext("/chart", new chartHandler());
        server.createContext("/getRecentData", new randomStreamGen());

        server.createContext("/amcharts/", new chartLib1Handler());
        server.createContext("/amcharts/images/", new image1Handler());
        
        
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server is running at: "+server.getAddress());
               
    }
 

    static class randomStreamGen implements HttpHandler{
        public void handle(HttpExchange t) throws IOException {
            long ts = System.currentTimeMillis();
            //int value = (new Random()).nextInt((925 - 875) + 1) + 875; 
            
            Sensor sensorLibrary = new Sensor("https://172.20.70.232/reading", "root", "root");
            
            double value = sensorLibrary.getNewMeasure().getTotalPower();
                        
            System.out.println("Stream Tuple >> ts= "+ts+"|"+"value= "+value);  
            String response = "{ \"ts\": \""+ts+"\", \"reading\": \""+value+"\"}";
            

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }  
    
    static class MyHandler implements HttpHandler {
        
        public void handle(HttpExchange t) throws IOException {  
            String response = "{ \"id\":   \""+teste+"\"}";
            teste++;
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class WebPageHandler implements HttpHandler{
        public void handle(HttpExchange t) throws IOException {
            String response = readFile("src/httpServer/index.html");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class chartHandler implements HttpHandler{        
        public void handle(HttpExchange t) throws IOException {
            String response = readFile("src/httpServer/chartTests.html");                   
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class chartLib1Handler implements HttpHandler{        
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
            
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class image1Handler implements HttpHandler{
        
        public void handle(HttpExchange t) throws IOException {
            
            System.out.println("*****************************************************");
            String requestURI = t.getRequestURI().getPath(); //*all* URI requests
            String requestedFile = requestURI.split("/")[3]; //extract resource name (eg. amcharts.js)
            
            System.out.println("--:"+requestURI);
            System.out.println("-->"+requestedFile);        
            
            String response = readFile("src/httpServer/amstockchartLib/amcharts/images/"+requestedFile);
            System.out.println("Loaded: "+requestedFile);
            
            
            //TODO LER IMAGEM EM BINARIO EM VEZ DE STRING COMO ESTAS A FAZER AGORA
            // TODO AHUSTAR O CONTENT TYPE
            t.getResponseHeaders().set("Content-Type", "application/gif"); //<------------------------------------------ Estas Aqui
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    
    
    static class chartCSSHandler implements HttpHandler{
        
        public void handle(HttpExchange t) throws IOException {
            
            System.out.println("Aqui[17]");
            BufferedReader br = null;
            String response = null;
            try {
                br = new BufferedReader(new FileReader("src/httpServer/amstockchartLib/amcharts/style.css"));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }   
                response = sb.toString();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            } finally {
                br.close();
            }                    
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
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
    
    
}