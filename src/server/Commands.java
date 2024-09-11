package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class CommandAdd implements Command {
    File file;
    boolean successful = false;
    String content;

    public CommandAdd(String file, String path) {
        this.file = new File(path + "\\" + file);
    }

    public CommandAdd(String file, String path, String content) {
        this.file = new File(path + "\\" + file);
        this.content = content;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public boolean execute() {
        try {
            if (!file.exists() && file.createNewFile()) {
                if (content != null) {
                    Files.writeString(file.toPath(), content);
                }
                System.out.printf("The file %s added successfully\n", file.getName());
                successful = true;
            } else {
                System.out.printf("Cannot add the file %s\n", file.getName());
            }
        } catch (IOException e) {
            System.out.println("ERROR");
        }
        return successful;
    }
}

class CommandGet implements Command {
    File file;
    boolean successful = false;

    public CommandGet(String file, String path) {
        this.file = new File(path + "\\" + file);
    }

    @Override
    public boolean execute() {
        if (file.exists()) {
            System.out.println("The file " + file.getName() + " was sent");
            successful = true;
        } else {
            System.out.println("The file " + file.getName() + " not found");
        }
        return successful;
    }

    public boolean isSuccessful() {
        return successful;
    }
}

class CommandDelete implements Command {
    File file;
    boolean successful = false;

    public CommandDelete(String file, String path) {
        this.file = new File(path + "\\" + file);
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public boolean execute() {
        if (file.delete()) {
            System.out.println("The file " + file.getName() + " was deleted");
            successful = true;
        } else {
            System.out.println("The file " + file.getName() + " not found");
        }
        return successful;
    }
}

class Executer {
    Command command;

    public Executer() {
    }

    public boolean execute() {
        return command.execute();
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}

interface Command {
    boolean execute();
}