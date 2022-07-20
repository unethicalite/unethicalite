package net.runelite.client.discord.webhook.embeds;

public class EmbedFooter
{
    private String text;
    private String iconUrl;

    public EmbedFooter(String text, String iconUrl)
    {
        this.text = text;
        this.iconUrl = iconUrl;
    }

    public String getText()
    {
        return text;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }
}
