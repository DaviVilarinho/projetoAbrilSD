package ufu.davigabriel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static int PORTAL_SERVERS = 2;

    public static void main(String[] args) throws InterruptedException{
        List<ProcessBuilder> adminPortalServers = IntStream.range(0, PORTAL_SERVERS)
                .mapToObj(value -> new ProcessBuilder(
                        "./gradlew",
                        "run",
                        "-PmainClass=ufu.davigabriel.server.AdminPortalServer",
                        "--args=\"" + value + "\"")
                ).toList();
        List<ProcessBuilder> orderPortalServers = IntStream.range(0, PORTAL_SERVERS)
                .mapToObj(value -> new ProcessBuilder(
                        "./gradlew",
                        "run",
                        "-PmainClass=ufu.davigabriel.server.OrderPortalServer",
                        "--args=\"" + value + "\"")
                ).toList();
        List<List<ProcessBuilder>> portalServers = List.of(orderPortalServers, adminPortalServers);
        portalServers.forEach(portalServer -> portalServer.forEach(ProcessBuilder::inheritIO));
        List<Process> processes = new ArrayList<>();
        try {
            portalServers.forEach(portalServer ->
                    portalServer.forEach(server -> {
                        try {
                            processes.add(server.start());
                        } catch (IOException e) {
                            System.out.println("Processo falhou " + e.getMessage());
                        }
                    })
            );
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