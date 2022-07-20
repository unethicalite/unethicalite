package net.runelite.client.discord.webhook.embeds;

public class EmbedField
{
    private final String name;
    private final String value;
    private final boolean inline;

    public EmbedField(String name, String value, boolean inline)
    {
        this.name = name;
        this.value = value;
        this.inline = inline;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public boolean isInline()
    {
        return inline;
    }
}