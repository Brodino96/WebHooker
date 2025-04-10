package dev.brodino.webhooker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Discord {

    public Discord() {}
    private static final HttpClient CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    public static void sendToAll(String sender, String message, String[] tags) {

        if (sender == null) {
            Webhooker.LOGGER.error("Sender cannot be null");
            return;
        }

        if (message == null) {
            Webhooker.LOGGER.error("Message cannot be null");
            return;
        }

        HashMap<String, String> channelList = Webhooker.CONFIG.channelList();
        if (channelList.isEmpty()) {
            Webhooker.LOGGER.warn("No webhook channels configured");
            return;
        }

        String payload = getPayload(sender, message, tags);

        Discord.invokeWebhook(payload, channelList);
    }

    public static void sendToChannels(String sender, String message, String[] channels, String[] tags) {

        if (sender == null) {
            Webhooker.LOGGER.error("Sender cannot be null");
            return;
        }

        if (message == null) {
            Webhooker.LOGGER.error("Message cannot be null");
            return;
        }

        if (channels == null) {
            Webhooker.LOGGER.error("Channels list cannot be null");
        }

        if (channels.length == 0) {
            Webhooker.LOGGER.error("Channels list cannot be empty");
        }

        String payload = Discord.getPayload(sender, message, tags);

        HashMap<String, String> channelList = new HashMap<>();

        for (String key : channels) {
            if (Webhooker.CONFIG.channelList().containsKey(key)) {
                channelList.put(key, Webhooker.CONFIG.channelList().get(key));
            }
        }

        if (channelList.isEmpty()) {
            Webhooker.LOGGER.warn("No valid channels found for webhooks");
        }

        Discord.invokeWebhook(payload, channelList);
    }

    private static void invokeWebhook(String payload, HashMap<String, String> channelList) {

        for (Map.Entry<String, String> channel : channelList.entrySet()) {
            try {

                String webhookUrl = channel.getValue();
                if (webhookUrl == null || !webhookUrl.startsWith("http")) {
                    Webhooker.LOGGER.warn("Invalid webhook URL for channel: {}", channel.getKey());
                    continue;
                }

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();

                HttpResponse<String> response = Discord.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                int status = response.statusCode();

                if (status < 200 || status >= 300) {
                    Webhooker.LOGGER.error("Webhook request failed for channel {} with status {}: {}",
                        channel.getKey(), status, response.body());
                } else {
                    Webhooker.LOGGER.debug("Webhook sent successfully to channel {}", channel.getKey());
                }

            } catch (Exception e) {
                Webhooker.LOGGER.error("Failed to send webhook to channel: {}", channel.getKey(), e);
            }
        }
    }

    private static String getDiscordTag(String tag) {
        StringBuilder builder = new StringBuilder();
        if (tag.equals("@everyone")) {
            builder.append(tag).append(" ");
        } else {
            builder.append("<@&").append(tag).append("> ");
        }
        return builder.toString();
    }

    private static String getPayload(String sender, String message, String[] tags) {

        // Escape JSON special characters to prevent injection
        String escapedSender = escapeJson(sender);
        String escapedMessage = escapeJson(message);
        String username = escapeJson(Webhooker.CONFIG.username());
        String imageUrl = escapeJson(Webhooker.CONFIG.image());
        int embedColor = Webhooker.CONFIG.embedColor();

        StringBuilder json = new StringBuilder("{");
        StringBuilder mention = new StringBuilder();

        if (tags == null || tags.length == 0 && Webhooker.CONFIG.defaultTags().length != 0) {
            for (String tag : Webhooker.CONFIG.defaultTags()) {
                if (Webhooker.CONFIG.taggableList().containsKey(tag)) {
                    mention.append(getDiscordTag(Webhooker.CONFIG.taggableList().get(tag)));
                }
            }
        } else {
            for (String tag : tags) {
                if (Webhooker.CONFIG.taggableList().containsKey(tag)) {
                    mention.append(getDiscordTag(Webhooker.CONFIG.taggableList().get(tag)));
                }
            }
        }

        json.append("\"content\": \"").append(mention).append("\",");

        json.append("\"embeds\": [{")
            .append("\"title\": \"").append(escapedSender).append("\",")
            .append("\"description\": \"").append(escapedMessage).append("\",")
            .append("\"color\": ").append(embedColor)
            .append("}],")
            .append("\"username\": \"").append(username).append("\",")
            .append("\"avatar_url\": \"").append(imageUrl).append("\"");

        json.append("}");
        return json.toString();

        /*
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
        */
    }

    private static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static void initialize() {
        System.out.println("Webhook initialized");
    }
}