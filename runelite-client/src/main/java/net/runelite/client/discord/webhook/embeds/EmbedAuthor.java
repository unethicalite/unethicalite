package net.runelite.client.discord.webhook.embeds;

public class EmbedAuthor
{
    private final String name;
    private final String url;
    private final String iconUrl;

    public EmbedAuthor(String name, String url, String iconUrl)
    {
        this.name = name;
        this.url = url;
        this.iconUrl = iconUrl;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }
}