package dev.brodino.webhooker;

import io.wispforest.owo.config.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;

@Config(name = "webhook", wrapperName = "WebhookConfig")
public class ConfigHelper {
    public String image = "https://img.freepik.com/psd-gratis/single-yellow-potato-closeup-studio-shot_191095-85935.jpg";
    public String username = "Webhooker";
    public HashMap<String,String> channelList = new HashMap<>();
}