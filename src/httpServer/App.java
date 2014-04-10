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
    
    
    public static void main(String[] args) throws Exception { 
        
        int foo = 1;
        int port = 62490;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/web", new WebPageHandler());
        server.createContext("/chart", new chartHandler());
        server.createContext("/getRecentData", new randomStreamGen());

        server.createContext("/amcharts/amcharts.js", new chartLib1Handler());
        server.createContext("/amcharts/serial.js", new chartLib2Handler());
        server.createContext("/amcharts/amstock.js", new chartLib3Handler());
        server.createContext("/amcharts/style.css", new chartCSSHandler());
        server.createContext("/amcharts/images/dragIcon.gif", new image1Handler());
        
        
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server is running at: http://localhost:"+port+"/test");
               
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
            
            BufferedReader br = null;
            String response = null;
            try {
            System.out.println("AA");
            System.out.println(System.getProperty("user.dir")); 
            br = new BufferedReader(new FileReader("src/httpServer/index.html"));
            System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
            
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                response = sb.toString();
            }catch(FileNotFoundException e){
                System.out.println("CC");
                e.printStackTrace();
                System.out.println("DD");
            } finally {
                br.close();
            }
            System.out.println("EE");
          
                    
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    static class chartHandler implements HttpHandler{
        
        public void handle(HttpExchange t) throws IOException {
            
            BufferedReader br = null;
            String response = null;
            try {
                br = new BufferedReader(new FileReader("src/httpServer/chartTests.html"));
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
    
    static class chartLib1Handler implements HttpHandler{
        
        public void handle(HttpExchange t) throws IOException {
            
            System.out.println("Aqui");
            BufferedReader br = null;
            String response = null;
            try {
                br = new BufferedReader(new FileReader("src/httpServer/amstockchartLib/amcharts/amcharts.js"));
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
    
    static class chartLib2Handler implements HttpHandler{
        
        public void handle(HttpExchange t) throws IOException {
            
            System.out.println("Aqui");
            BufferedReader br = null;
            String response = null;
            try {
                br = new BufferedReader(new FileReader("src/httpServer/amstockchartLib/amcharts/serial.js"));
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
    
    static class chartLib3Handler implements HttpHandler{
        
        public void handle(HttpExchange t) throws IOException {
            
            System.out.println("Aqui");
            BufferedReader br = null;
            String response = null;
            try {
                br = new BufferedReader(new FileReader("src/httpServer/amstockchartLib/amcharts/amstock.js"));
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
    
    static class chartCSSHandler implements HttpHandler{
        
        public void handle(HttpExchange t) throws IOException {
            
            System.out.println("Aqui");
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
    
    static class image1Handler implements HttpHandler{
        
        public void handle(HttpExchange t) throws IOException {
            
            System.out.println("Aqui");
            BufferedReader br = null;
            String response = null;
            try {
                br = new BufferedReader(new FileReader("src/httpServer/amstockchartLib/amcharts/images/dragIcon.gif"));
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
    
    
    
}