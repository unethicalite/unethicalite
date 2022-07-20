package net.runelite.client.discord.webhook;

import net.runelite.client.discord.webhook.embeds.EmbedObject;

import java.util.ArrayList;
import java.util.List;

public class DiscordMessageBuilder
{

    private String username;
    private String messageContent;
    private String avatarUrl;
    private boolean textToSpeech;
    private List<EmbedObject> embeds;

    public DiscordMessageBuilder()
    {
        embeds = new ArrayList<>();
    }

    public String getUsername()
    {
        return username;
    }

    public DiscordMessageBuilder setUsername(String username)
    {
        this.username = username;
        return this;
    }

    public String getMessageContent()
    {
        return messageContent;
    }

    public DiscordMessageBuilder setMessageContent(String messageContent)
    {
        this.messageContent = messageContent;
        return this;
    }

    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    public DiscordMessageBuilder setAvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public boolean isTextToSpeech()
    {
        return textToSpeech;
    }

    public DiscordMessageBuilder setTextToSpeech(boolean textToSpeech)
    {
        this.textToSpeech = textToSpeech;
        return this;
    }

    public List<EmbedObject> getEmbeds()
    {
        return embeds;
    }

    public DiscordMessageBuilder addEmbed(EmbedObject embed)
    {
        this.embeds.add(embed);
        return this;
    }

    public DiscordMessageBuilder setEmbeds(List<EmbedObject> embeds)
    {
        this.embeds = embeds;
        return this;
    }

    public DiscordMessage build()
    {
        return new DiscordMessage(this);
    }
}
