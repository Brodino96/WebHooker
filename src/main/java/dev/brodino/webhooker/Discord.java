package dev.brodino.webhooker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Discord {

    public Discord() {}

    public static void sendToAll(String sender, String message) {

        String payload = getPayload(sender, message);
        HashMap<String, String> channelList = Webhooker.CONFIG.channelList();

        Discord.invokeWebhook(payload, channelList);
    }

    public static void sendToChannels(String sender, String message, String[] channels) {

        String payload = Discord.getPayload(sender, message);

        HashMap<String, String> channelList = new HashMap<>();

        for (String key : channels) {
            if (Webhooker.CONFIG.channelList().containsKey(key)) {
                channelList.put(key, Webhooker.CONFIG.channelList().get(key));
            }
        }

        Discord.invokeWebhook(payload, channelList);
    }

    private static void invokeWebhook(String payload, HashMap<String, String> channelList) {

        HttpClient client = HttpClient.newHttpClient();

        for (Map.Entry<String, String> channel : channelList.entrySet()) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(channel.getValue()))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getPayload(String sender, String message) {
        // language=json
        return String.format("""
            {
                "content": "@everyone",
                "embeds": [
                    {
                        "title": "%s",
                        "description": "%s",
                        "color": 5814783
                    }
                ],
                "username": "%s",
                "avatar_url": "%s"
            }
            """, sender, message, Webhooker.CONFIG.username(), Webhooker.CONFIG.image());
    }

    public static void initialize() {
        System.out.println("Webhook initialized");
    }
}