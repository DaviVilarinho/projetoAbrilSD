package ufu.davigabriel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    private static int PORTAL_SERVERS = 5;

    public static void main(String[] args) {
        List<ProcessBuilder> servers = IntStream.range(0, PORTAL_SERVERS)
                .mapToObj(value -> new ProcessBuilder(
                        "./gradlew",
                        "run",
                        "-PmainClass=ufu.davigabriel.server.AdminPortalServer",
                        "--args=\"" + value + "\"")
                ).toList();
        servers.forEach(ProcessBuilder::inheritIO);
        List<Process> processes = new ArrayList<>();
        try {
            servers.forEach(server -> {
                try {
                    processes.add(server.start());
                } catch (IOException e) {
                    System.out.println("Processo falhou " + e.getMessage());
                }
            });
            processes.forEach(process -> {
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            processes.forEach(Process::destroy);
        }
    }
}