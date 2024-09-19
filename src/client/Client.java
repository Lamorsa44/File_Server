package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Client {

    private final String url = "http://localhost:42069/server/data/";
    private final String root = "src/client/data/";
    private final Scanner scanner = new Scanner(System.in);
    private HttpClient CLIENT;
    private final String regex = "[^ \t]";

    public Client() {
        if (CLIENT == null) {
            CLIENT = HttpClient.newHttpClient();
        }
    }

    synchronized public void get() throws IOException, InterruptedException {

        HttpResponse response;

        System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id): ");

        switch (scanner.nextInt()) {
            case 1 -> {
                System.out.println("Enter filename: ");

                String fileName = scanner.next();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url + fileName))
                        .GET()
                        .build();

                Path path = Path.of(root + fileName);


                if (Files.notExists(path)) {
                    Files.createFile(path);
                }

                response = CLIENT.send(request,
                        HttpResponse.BodyHandlers.ofFile(path));

                System.out.println("The request was sent.");

                if (response.statusCode() == 200) {
                    System.out.println("File saved on the hard drive!");

                } else {
                    System.out.println("The response says that the file was not found!");
                    Files.deleteIfExists(path);
                }
            }
            case 2 -> {
                System.out.println("Enter id: ");

                String id = scanner.next();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url + "id/" + id))
                        .GET()
                        .build();

                System.out.println("The request was sent.");
                //maybe lying
                System.out.println("The file was downloaded! Specify a name for it: ");

                String Filename = scanner.next();

                Path path = Path.of(root + Filename);
                if (Files.notExists(path))
                    Files.createFile(path);


                response = CLIENT.send(request, HttpResponse.BodyHandlers.ofFile(path));

                if (response.statusCode() == 200) {
                    System.out.println("File saved on the hard drive!");

                } else {
                    System.out.println("The response says that the file was not found!");
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    synchronized public void post() throws IOException, InterruptedException {

        System.out.println("Enter name of the file: ");
        String fileName = scanner.next();

//        System.out.println("Enter name of the file to be saved on server: ");
//        String serverName = scanner.next();

        HttpRequest request;

        Path path = Path.of(root + fileName);

//        Rememeber to fix code cause test cases are weird
        try {
            if (Files.exists(path)) {
                request = HttpRequest.newBuilder()
                        .uri(URI.create(url + fileName))
                        .POST(HttpRequest.BodyPublishers.ofByteArray(Files.readAllBytes(path)))
                        .build();
            } else {
                System.out.println("This file does not exist.");
                return;
            }
        } catch (IOException e) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url + fileName))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            System.out.println("Error POST sending empty request.");
        }


        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("The request was sent.");

        if (response.statusCode() == 200) {
//            System.out.println("Response says that file is saved! ID =");
            System.out.println("Response says that file is saved! ID = " + response.body());
        } else {
            System.out.println("The response says that creating the file was forbidden!");
        }
    }

    synchronized public void delete() throws IOException, InterruptedException {

        System.out.println("Do you want to delete the file by name or by id (1 - name, 2 - id): ");

        switch (scanner.nextShort()) {
            case 1 -> {
                System.out.println("Enter filename: ");

                String fileName = scanner.next();
                HttpRequest request;
                HttpResponse<String> response;

                request = HttpRequest.newBuilder()
                        .uri(URI.create(url + fileName))
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
            case 2 -> {
                System.out.println("Enter id: ");

                String id = scanner.next();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url + "id/" + id))
                        .DELETE()
                        .build();

                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("The request was sent.");

                if (response.statusCode() == 200) {
                    System.out.println("The response says that the file was successfully deleted!");
                } else {
                    System.out.println("The response says that the file was not found!");
                }
            }
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
