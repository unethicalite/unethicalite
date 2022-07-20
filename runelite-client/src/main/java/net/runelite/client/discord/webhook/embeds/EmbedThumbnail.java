package net.runelite.client.discord.webhook.embeds;

public class EmbedThumbnail
{
    private final String url;

    public EmbedThumbnail(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }
}
