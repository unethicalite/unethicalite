package net.runelite.client.discord.webhook.embeds;


import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class EmbedBuilder
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

    public EmbedBuilder()
    {
        fields = new ArrayList<>();
    }

    public String getTitle()
    {
        return title;
    }

    public EmbedBuilder setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public EmbedBuilder setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public String getUrl()
    {
        return url;
    }

    public EmbedBuilder setUrl(String url)
    {
        this.url = url;
        return this;
    }

    public Color getColor()
    {
        return color;
    }

    public EmbedBuilder setColor(Color color)
    {
        this.color = color;
        return this;
    }

    public EmbedFooter getFooter()
    {
        return footer;
    }

    public EmbedBuilder setFooter(String text, String icon)
    {
        this.footer = new EmbedFooter(text, icon);
        return this;
    }

    public EmbedThumbnail getThumbnail()
    {
        return thumbnail;
    }

    public EmbedBuilder setThumbnail(String url)
    {
        this.thumbnail = new EmbedThumbnail(url);
        return this;
    }

    public EmbedImage getImage()
    {
        return image;
    }

    public EmbedBuilder setImage(String url)
    {
        this.image = new EmbedImage(url);
        return this;
    }

    public EmbedAuthor getAuthor()
    {
        return author;
    }

    public EmbedBuilder setAuthor(String name, String url, String icon)
    {
        this.author = new EmbedAuthor(name, url, icon);
        return this;
    }

    public EmbedBuilder addField(String name, String value, boolean inline)
    {
        this.fields.add(new EmbedField(name, value, inline));
        return this;
    }

    public List<EmbedField> getFields()
    {
        return fields;
    }

    public EmbedObject build()
    {
        return new EmbedObject(this);
    }

}
