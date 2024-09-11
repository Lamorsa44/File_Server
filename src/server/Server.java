package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    private final static String BASE = "src/server/data";
    private static HttpServer server;
    private final static Executer executer = new Executer();

    public static void main(String[] args) throws IOException {

        prepareDirectory(BASE);

        server = HttpServer.create(new InetSocketAddress(42069), 0);
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext("/server/data", new MyHandler());
        server.createContext("/server/data/", new MyHandler());
        server.createContext("/exit", new exitHandler());

        server.start();

        System.out.println("Server started");
    }

    static class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Scanner scanner = new Scanner(exchange.getRequestBody());
            String method = exchange.getRequestMethod();
            var tmp = exchange.getRequestURI().getPath().split("/");
            String item = tmp[tmp.length - 1];
            String result;

            switch (method) {
                case "GET" -> {
                    int status;
                    boolean isValid = false;

                    executer.setCommand(new CommandGet(item, BASE));
                    isValid = executer.execute();

                    if (isValid) {
                        if (item.endsWith(".txt")) {
                            Path path = Path.of(BASE + "/" + item);
                            result = Files.size(path) == 0 ?
                                     "Worales" : Files.readString(path);
                        }
                        else {
                            result = "No txt file";
                        }
                        status = 200;
                    }
                    else {
                        result = "Not found";
                        status = 404;
                    }

                    sendResponse(exchange, result, status);
                }
                case "POST" -> {
                    int status;
                    boolean isValid = false;
                    boolean hasText = scanner.hasNext();

                    if (hasText) {
                        executer.setCommand(new CommandAdd(item, BASE, scanner.nextLine()));
                        isValid = executer.execute();
                    } else {
                        executer.setCommand(new CommandAdd(item, BASE));
                        isValid = executer.execute();
                    }

                    if (isValid) {
                        result = "Created";
                        status = 200;
                    } else {
                        result = "File already exists";
                        status = 403;
                    }
                    sendResponse(exchange, result, status);
                }
                case "DELETE" -> {
                    int status;
                    boolean isValid = false;

                    executer.setCommand(new CommandDelete(item, BASE));
                    isValid = executer.execute();

                    if (isValid) {
                        result = "Created";
                        status = 200;
                    } else {
                        result = "Not found";
                        status = 404;
                    }
                    sendResponse(exchange, result, status);
                }
            }
        }
    }

    static class exitHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Server closing");

            sendResponse(exchange, "200", 200);
            server.stop(1);

            if (server.getExecutor() instanceof ExecutorService executorService) {
                executorService.shutdown();
            }

        }
    }

    private static void sendResponse(HttpExchange exchange, String response, int status) throws IOException {
        exchange.sendResponseHeaders(status, response.length());
        OutputStream out = exchange.getResponseBody();
        if (!response.isEmpty())
            out.write(response.getBytes());
        out.close();
    }

    static void prepareDirectory(String nameDirectory) throws IOException {
        Path directory = Paths.get(nameDirectory);

        if (Files.notExists(directory)) {
            Files.createDirectories(directory);
        }
    }
}

