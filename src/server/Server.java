package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    private final static String BASE = "src/server/data/";
    private static HttpServer server;
    private final static Executer executer = new Executer();


    public static void main(String[] args) throws IOException {


        prepareDirectory(BASE);

        server = HttpServer.create(new InetSocketAddress(42069), 0);
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext("/server/data/", new MyHandler());
        server.createContext("/server/data/id", new idHandler());
        server.createContext("/exit", new exitHandler());

        server.start();

        System.out.println("Server started");
    }

    static class idHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            DataInputStream dataInputStream = new DataInputStream(exchange.getRequestBody());
            String method = exchange.getRequestMethod();
            var tmp = exchange.getRequestURI().getPath().split("/");
            String item = tmp[tmp.length - 1];
            byte[] result;

            switch (method) {
                case "GET" -> {
                    int status;
                    boolean isValid = false;
                    var command = new CommandGetById(item, BASE);
                    executer.setCommand(command);
                    isValid = executer.execute();


                    if (isValid) {
                        result = Files.readAllBytes(command.getFile().toPath());
                        status = 200;
                    }
                    else {
                        result = "diam".getBytes();
                        status = 404;
                    }

                    sendResponse(exchange, result, status);
                }
                case "POST" -> {
                    int status;
                    boolean isValid = false;
                    boolean hasContent = dataInputStream.available() > 0;
                    CommandAddById command;

                    if (hasContent) {
                        byte[] content = dataInputStream.readAllBytes();
                        command = new CommandAddById(item, BASE, content);
                        executer.setCommand(command);
                        isValid = executer.execute();
                    } else {
                        command = new CommandAddById(item, BASE);
                        executer.setCommand(command);
                        isValid = executer.execute();
                    }

                    if (isValid) {
                        result = nextId(command.getFile().toPath()).getBytes();
                        status = 200;
                    } else {
                        result = "File already exists".getBytes();
                        status = 403;
                    }
                    sendResponse(exchange, result, status);
                }
                case "DELETE" -> {
                    int status;
                    boolean isValid = false;
                    var command = new CommandDeleteById(item, BASE);
                    executer.setCommand(command);
                    isValid = executer.execute();

                    if (isValid) {
                        result = "Deleted".getBytes();
                        status = 200;
                    } else {
                        result = "Not found".getBytes();
                        status = 404;
                    }
                    sendResponse(exchange, result, status);
                }
            }
        }
    }

    static class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            DataInputStream dataInputStream = new DataInputStream(exchange.getRequestBody());
            String method = exchange.getRequestMethod();
            var tmp = exchange.getRequestURI().getPath().split("/");
            Optional<String> item = Optional.ofNullable(tmp[tmp.length - 1]);
            byte[] result;

            switch (method) {
                case "GET" -> {
                    int status;
                    boolean isValid = false;
                    var command = new CommandGet(item.orElse(""), BASE);
                    executer.setCommand(command);
                    isValid = executer.execute();


                    if (isValid) {
                        result = Files.readAllBytes(command.getFile().toPath());
                        status = 200;
                    }
                    else {
                        result = new byte[0];
                        status = 404;
                    }

                    sendResponse(exchange, result, status);
                }
                case "POST" -> {
                    int status;
                    boolean isValid = false;
                    boolean hasContent = dataInputStream.available() > 0;
                    CommandAdd command;

                    if (hasContent) {
                        byte[] content = dataInputStream.readAllBytes();
                        command = new CommandAdd(item.orElse(""), BASE, content);
                        executer.setCommand(command);
                        isValid = executer.execute();
                    } else {
                        command = new CommandAdd(item.orElse(""), BASE);
                        executer.setCommand(command);
                        isValid = executer.execute();
                    }

                    if (isValid) {
                        result = nextId(command.getFile().toPath()).getBytes();
                        status = 200;
                    } else {
                        result = "File already exists".getBytes();
                        status = 403;
                    }
                    sendResponse(exchange, result, status);
                }
                case "DELETE" -> {
                    int status;
                    boolean isValid = false;
                    var command = new CommandDelete(item.orElse(""), BASE);
                    executer.setCommand(command);
                    isValid = executer.execute();

                    if (isValid) {
                        result = "Deleted".getBytes();
                        status = 200;
                    } else {
                        result = "Not found".getBytes();
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

            sendResponse(exchange, "200".getBytes(), 200);
            server.stop(1);

            if (server.getExecutor() instanceof ExecutorService executorService) {
                executorService.shutdown();
            }

        }
    }

    private static void sendResponse(HttpExchange exchange, byte[] response, int status) throws IOException {
        exchange.sendResponseHeaders(status, response.length);
        OutputStream out = exchange.getResponseBody();
        if (response.length != 0)
            out.write(response);
        out.close();
    }

    static void prepareDirectory(String nameDirectory) throws IOException {
        Path directory = Paths.get(nameDirectory);

        if (Files.notExists(directory)) {
            Files.createDirectories(directory);
        }
    }

    static String nextId(Path path) throws IOException {
        return String.valueOf(Files.list(Path.of(BASE)).toList().indexOf(path) + 1);
    }
}