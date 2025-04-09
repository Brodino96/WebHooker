package dev.brodino.webhooker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Discord {

    public Discord() {}

    public void sendMessage(String sender, String message, boolean all, String[] channels) {

        String payload = String.format("""
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

        HttpClient client = HttpClient.newHttpClient();

        HashMap<String, String> channelList = new HashMap<>();

        if (all) {
            channelList = Webhooker.CONFIG.channelList();
        } else {
            for (String key : channels) {
                if (Webhooker.CONFIG.channelList().containsKey(key)) {
                    channelList.put(key, Webhooker.CONFIG.channelList().get(key));
                }
            }
        }

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

    public static void initialize() {
        System.out.println("Webhook initialized");
    }
}
