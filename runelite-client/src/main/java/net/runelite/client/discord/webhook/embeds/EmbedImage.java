package net.runelite.client.discord.webhook.embeds;

public class EmbedImage
{
    private final String url;

    public EmbedImage(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }
}