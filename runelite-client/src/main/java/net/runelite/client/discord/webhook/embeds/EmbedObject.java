package net.runelite.client.discord.webhook.embeds;

import java.awt.*;
import java.util.List;

public class EmbedObject
{
    private String title;
    private String description;
    private String url;
    private Color color;

    private EmbedFooter footer;
    private EmbedThumbnail thumbnail;
    private EmbedImage image;
    private EmbedAuthor author;
    private java.util.List<EmbedField> fields;

    public EmbedObject(EmbedBuilder builder)
    {
        this.title = builder.getTitle();
        this.description = builder.getDescription();
        this.url = builder.getUrl();
        this.color = builder.getColor();
        this.footer = builder.getFooter();
        this.thumbnail = builder.getThumbnail();
        this.image = builder.getImage();
        this.author = builder.getAuthor();
        this.fields = builder.getFields();
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getUrl()
    {
        return url;
    }

    public Color getColor()
    {
        return color;
    }

    public EmbedFooter getFooter()
    {
        return footer;
    }

    public EmbedThumbnail getThumbnail()
    {
        return thumbnail;
    }

    public EmbedImage getImage()
    {
        return image;
    }

    public EmbedAuthor getAuthor()
    {
        return author;
    }

    public List<EmbedField> getFields()
    {
        return fields;
    }

}