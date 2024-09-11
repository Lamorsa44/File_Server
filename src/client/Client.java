package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Client {

    private final String BASE = "http://localhost:42069/server/data/";
    private Scanner scanner = new Scanner(System.in);
    private HttpClient CLIENT;
    private HttpRequest request;
    private HttpResponse response;

    public Client() {
        if (CLIENT == null) {
            CLIENT = HttpClient.newHttpClient();
        }
    }

    synchronized public void get() throws IOException, InterruptedException {

        System.out.println("Enter filename: ");

        String fileName = scanner.next();

        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + fileName))
                .GET()
                .build();

        if (fileName.endsWith(".txt")) {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        }
        else
            response = CLIENT.send(request, HttpResponse.BodyHandlers.discarding());

        System.out.println("The request was sent.");

        if (response.statusCode() == 200) {
            if (response.body() != null)
                System.out.printf("The content of the file is: %s\n", response.body());
            else
                System.out.println("The file is empty.");
        } else {
            System.out.println("The response says that the file was not found!");
        }
    }

    synchronized public void post() throws IOException, InterruptedException {

        System.out.println("Enter filename: ");

        String fileName = scanner.nextLine();

        if (fileName.endsWith(".txt")) {

            String content;
            System.out.println("Enter file content: ");

            content = scanner.nextLine();

            request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + fileName))
                    .POST(HttpRequest.BodyPublishers.ofString(content))
                    .build();
        } else {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + fileName))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
        }

        response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("The request was sent.");

        if (response.statusCode() == 200) {
            System.out.println("The response says that file was created!");
        } else {
            System.out.println("The response says that creating the file was forbidden!");
        }
    }

    synchronized public void delete() throws IOException, InterruptedException {

        System.out.println("Enter filename: ");

        String fileName = scanner.next();
        HttpRequest request;
        HttpResponse<String> response;

        request = HttpRequest.newBuilder()
                .uri(URI.create(BASE + fileName))
                .DELETE()
                .build();

        response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("The request was sent.");

        if (response.statusCode() == 200) {
            System.out.println("The response says that the file was successfully deleted!");
        } else {
            System.out.println("The response says that the file was not found!");
        }
    }

    synchronized public void exit() throws IOException, InterruptedException {

        HttpRequest request;

        request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:42069/exit"))
                .GET()
                .build();

        CLIENT.send(request, HttpResponse.BodyHandlers.discarding());

        System.out.println("The request was sent.");
    }
}
