package client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        Client client = new Client();
        String request;
        boolean on = true;
        prepareDirectory("src/server/data");

        while (on) {
            System.out.println("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");

            request = scanner.next();

            switch (request) {
                case "1" -> {
                    client.get();
                }
                case "2" -> {
                    client.post();
                }
                case "3" -> {
                    client.delete();
                }
                case "exit" -> {
                    client.exit();
                    on = false;
                }
            }
        }
    }

    static void prepareDirectory(String nameDirectory) throws IOException {
        Path directory = Paths.get(nameDirectory);

        if (Files.notExists(directory)) {
            Files.createDirectories(directory);
        }
    }
}
