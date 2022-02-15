package co.featureflags.server;

import co.featureflags.server.exterior.FFCClient;
import co.featureflags.server.exterior.model.FFCUser;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

abstract class Demos {

    static void monitoringPerf(String name, Instant start, Instant end) {
        Duration duration = Duration.between(start, end);
        System.out.println(String.format("execution time for %s is %d milliseconds", name, duration.toMillis()));
    }

    static final class FFCClientStartAndWait {
        public static void main(String[] args) throws IOException, InterruptedException {
            String envSecret = "YjA1LTNiZDUtNCUyMDIxMDkwNDIyMTMxNV9fMzhfXzQ4X18xMDNfX2RlZmF1bHRfNzc1Yjg=";

            StreamingBuilder streamingBuilder = Factory.streamingBuilder()
                    .newStreamingURI("wss://ffc-api-ce2-dev.chinacloudsites.cn");

            FFCConfig config = new FFCConfig.Builder()
                    .updateProcessorFactory(streamingBuilder)
                    .build();

            FFCClient client = new FFCClientImp(envSecret, config);

            Scanner scanner = new Scanner(System.in);
            FFCUser user;
            String line;
            while (client.isInitialized()) {
                System.out.println("------------------------------");
                System.out.println("input user key and flag key seperated by /");
                line = scanner.nextLine();
                if ("exit".equalsIgnoreCase(line)) {
                    break;
                }
                try {
                    String[] words = line.split("/");
                    user = new FFCUser.Builder(words[0]).build();
                    Instant start = Instant.now();
                    String res = client.variation(words[1], user, "Not Found");
                    Instant end = Instant.now();
                    System.out.println("result is " + res);
                    monitoringPerf("evaluate", start, end);
                } catch (Exception e) {
                    break;
                }
            }
            scanner.close();
            client.close();
            System.out.println("APP FINISHED");

        }
    }

    static final class FFCClientStartNotWait {
        public static void main(String[] args) throws InterruptedException, IOException {
            String envSecret = "YjA1LTNiZDUtNCUyMDIxMDkwNDIyMTMxNV9fMzhfXzQ4X18xMDNfX2RlZmF1bHRfNzc1Yjg=";

            StreamingBuilder streamingBuilder = Factory.streamingBuilder()
                    .newStreamingURI("wss://ffc-api-ce2-dev.chinacloudsites.cn");

            FFCConfig config = new FFCConfig.Builder()
                    .startWaitTime(Duration.ZERO)
                    .updateProcessorFactory(streamingBuilder)
                    .build();

            FFCClient client = new FFCClientImp(envSecret, config);

            Scanner scanner = new Scanner(System.in);
            FFCUser user;
            String line;
            while (client.getDataUpdateStatusProvider().waitForOKState(Duration.ofSeconds(15))) {
                System.out.println("------------------------------");
                System.out.println("input user key and flag key seperated by /");
                line = scanner.nextLine();
                if ("exit".equalsIgnoreCase(line)) {
                    break;
                }
                try {
                    String[] words = line.split("/");
                    user = new FFCUser.Builder(words[0]).build();
                    Instant start = Instant.now();
                    String res = client.variation(words[1], user, "Not Found");
                    Instant end = Instant.now();
                    System.out.println("result is " + res);
                    monitoringPerf("evaluate", start, end);
                } catch (Exception e) {
                    break;
                }
            }
            scanner.close();
            client.close();
            System.out.println("APP FINISHED");
        }
    }

}