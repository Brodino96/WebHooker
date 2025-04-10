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
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void sendToAll(String sender, String message) {
        if (sender == null || message == null) {
            Webhooker.LOGGER.error("Sender or message cannot be null");
            return;
        }
        
        Map<String, String> channelList = Webhooker.CONFIG.getChannelList();
        if (channelList.isEmpty()) {
            Webhooker.LOGGER.warn("No webhook channels configured");
            return;
        }
        
        String payload = getPayload(sender, message);
        invokeWebhook(payload, channelList);
    }

    public static void sendToChannels(String sender, String message, String[] channels) {
        if (sender == null || message == null || channels == null || channels.length == 0) {
            Webhooker.LOGGER.error("Invalid parameters for sendToChannels");
            return;
        }
        
        String payload = getPayload(sender, message);
        Map<String, String> targetChannels = new HashMap<>();

        for (String channel : channels) {
            if (Webhooker.CONFIG.hasChannel(channel)) {
                targetChannels.put(channel, Webhooker.CONFIG.getWebhookUrl(channel));
            } else {
                Webhooker.LOGGER.warn("Channel '{}' not found in configuration", channel);
            }
        }
        
        if (targetChannels.isEmpty()) {
            Webhooker.LOGGER.warn("No valid channels found for webhooks");
            return;
        }

        invokeWebhook(payload, targetChannels);
    }

    private static void invokeWebhook(String payload, Map<String, String> channelList) {
        for (Map.Entry<String, String> channel : channelList.entrySet()) {
            try {
                String webhookUrl = channel.getValue();
                if (webhookUrl == null || webhookUrl.isEmpty() || !webhookUrl.startsWith("http")) {
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

                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();
                
                if (statusCode < 200 || statusCode >= 300) {
                    Webhooker.LOGGER.error("Webhook request failed for channel {} with status {}: {}", 
                            channel.getKey(), statusCode, response.body());
                } else {
                    Webhooker.LOGGER.debug("Webhook sent successfully to channel {}", channel.getKey());
                }
            } catch (Exception e) {
                Webhooker.LOGGER.error("Failed to send webhook to channel: {}", channel.getKey(), e);
            }
        }
    }

    private static String getPayload(String sender, String message) {
        // Escape JSON special characters to prevent injection
        String escapedSender = escapeJson(sender);
        String escapedMessage = escapeJson(message);
        String username = escapeJson(Webhooker.CONFIG.getUsername());
        String imageUrl = escapeJson(Webhooker.CONFIG.getImage());
        int embedColor = Webhooker.CONFIG.getEmbedColor();
        
        StringBuilder json = new StringBuilder("{");
        
        // Add mention @everyone only if enabled in config
        if (Webhooker.CONFIG.shouldMentionEveryone()) {
            json.append("\"content\": \"@everyone\",");
        } else {
            json.append("\"content\": \"\",");
        }
        
        json.append("\"embeds\": [{")
            .append("\"title\": \"").append(escapedSender).append("\",")
            .append("\"description\": \"").append(escapedMessage).append("\",")
            .append("\"color\": ").append(embedColor)
            .append("}],")
            .append("\"username\": \"").append(username).append("\",")
            .append("\"avatar_url\": \"").append(imageUrl).append("\"");
        
        json.append("}");
        return json.toString();
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
        Webhooker.LOGGER.info("Discord webhook integration initialized");
    }
}