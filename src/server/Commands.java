package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class CommandAdd implements Command {
    File file;
    boolean successful = false;
    byte[] content;

    public CommandAdd(String file, String path) {
        this.file = new File(path + "\\" + file);
    }

    public CommandAdd(String file, String path, byte[] content) {
        this.file = new File(path + "\\" + file);
        this.content = content;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public File getFile() {
        return file;
    }

    @Override
    public boolean execute() {
        try {
            if (!file.exists() && file.createNewFile()) {
                if (content != null) {
                    Files.write(file.toPath(), content);
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

class CommandAddById implements Command {
    File file;
    boolean successful = false;
    byte[] content;

    public CommandAddById(String file, String path) throws IOException {
        if (file == null || file.isEmpty()) {
            file = "Default" + Files.list(Path.of(path)).sorted().toList().size() + 1;
        }

        this.file = new File(path + "\\" + file);
    }

    public CommandAddById(String file, String path, byte[] content) throws IOException {
        if (file == null || file.isEmpty()) {
            file = "Default" + Files.list(Path.of(path)).sorted().toList().size() + 1;
        }

        this.file = new File(path + "\\" + file);
        this.content = content;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public File getFile() {
        return file;
    }

    @Override
    public boolean execute() {
        try {
            if (!file.exists() && file.createNewFile()) {
                if (content != null) {
                    Files.write(file.toPath(), content);
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

class CommandGetById implements Command {
    File file;
    boolean successful = false;

    public CommandGetById(String id, String path) {
        try (var stream = Files.list(Path.of(path))) {
            var list = stream.sorted().toList();
            if (list.size() >= Integer.parseInt(id)) {
                this.file = list.get(Integer.parseInt(id) - 1).toFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public File getFile() {
        return file;
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

    public File getFile() {
        return file;
    }
}

class CommandDeleteById implements Command {
    File file;
    boolean successful = false;

    public CommandDeleteById(String id, String path) {
        try (var stream = Files.list(Path.of(path))) {
            var list = stream.sorted().toList();
            if (list.size() >= Integer.parseInt(id)) {
                this.file = list.get(Integer.parseInt(id) - 1).toFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public File getFile() {
        return file;
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

    public File getFile() {
        return file;
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