package dev.brodino.webhooker;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class Discord {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void initialize() {
        Webhooker.LOGGER.info("Initializing Discord module");
    }

    public static void sendToAll(String sender, String message, String[] tags) {
        String[] channels = Webhooker.CONFIG.getChannelList().keySet().toArray(new String[0]);
        Discord.sendMessage(sender, message, channels, tags);
    }

    public static void sendToChannels(String sender, String message, String[] channels, String[] tags) {
        Discord.sendMessage(sender, message, channels, tags);
    }

    public static void sendMessage(String sender, String message, String[] channels, String[] tags) {
        if (sender == null) {
            Webhooker.LOGGER.error("Sender cannot be null!");
            return;
        }

        if (message == null) {
            Webhooker.LOGGER.error("Message cannot be null!");
            return;
        }

        if (channels == null || channels.length == 0) {
            Webhooker.LOGGER.error("Channel list is empty");
            return;
        }

        if (tags == null) {
            tags = Webhooker.CONFIG.getDefaultTags();
        }

        String payload = getPayload(sender, message, tags);

        invokeWebhook(payload, channels);
    }

    private static String getPayload(String sender, String message, String[] tags) {
        String escapedSender = Webhooker.escapeJson(sender);
        String escapedMessage = Webhooker.escapeJson(message);
        String escapedUsername = Webhooker.escapeJson(Webhooker.CONFIG.getUsername());
        String escapedImage = Webhooker.escapeJson(Webhooker.CONFIG.getImage());

        StringBuilder json = new StringBuilder("{");
        StringBuilder mention = new StringBuilder();

        for (String tag : tags) {
            if (Webhooker.CONFIG.getTaggableList().containsKey(tag)) {
                mention.append(getDiscordTag(Webhooker.CONFIG.getTaggableList().get(tag)));
            }
        }

        json
            .append("\"content\": \"").append(mention).append("\",");

        json
            .append("\"embeds\": [{")
                .append("\"title\": \"").append(escapedSender).append("\",")
                .append("\"description\": \"").append(escapedMessage).append("\",")
                .append("\"color\": ").append(Webhooker.CONFIG.getEmbedColor())
                .append("}],")
                .append("\"username\": \"").append(escapedUsername).append("\",")
                .append("\"avatar_url\": \"").append(escapedImage).append("\"");

        json.append("}");
        return json.toString();
    }

    private static String getDiscordTag(String id) {
        StringBuilder builder = new StringBuilder();
        if (id.equals("@everyone")) {
            builder
                .append(id)
                .append(" ");
        } else {
            builder
                .append("<@&")
                .append(id)
                .append("> ");
        }
        return builder.toString();
    }

    public static void invokeWebhook(String payload, String[] channelList) {

        for (String channelKey : channelList) {
            try {
                String webhookUrl = Webhooker.CONFIG.getChannelList().get(channelKey);
                if (webhookUrl == null || !webhookUrl.startsWith("http")) {
                    Webhooker.LOGGER.error("Invalid webhook URL for channel: {}", channelKey);
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
                    Webhooker.LOGGER.error("Webhook request failed for channel {} with status {}: {}", channelKey, status, response.body());
                } else {
                    Webhooker.LOGGER.info("Webhook request sent successfully to channel {}", channelKey);
                }
            } catch (IOException | InterruptedException e) {
                Webhooker.LOGGER.error("Failed to send webhook to channel: {} caused by error: {}", channelKey, e);
            }
        }
    }
}
