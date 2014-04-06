package httpServer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

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
        
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server is running at: http://localhost:"+port+"/test");
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
    
    
}