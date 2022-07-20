package net.runelite.client.discord.webhook;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.runelite.client.discord.webhook.embeds.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DiscordMessage
{

    private final String username;
    private final String messageContent;
    private final String avatarUrl;
    private final boolean textToSpeech;
    private final List<EmbedObject> embeds;

    public DiscordMessage(DiscordMessageBuilder builder)
    {
        this.username = builder.getUsername();
        this.messageContent = builder.getMessageContent();
        this.avatarUrl = builder.getAvatarUrl();
        this.textToSpeech = builder.isTextToSpeech();
        this.embeds = builder.getEmbeds();
    }

    public String getUsername()
    {
        return username;
    }

    public String getMessageContent()
    {
        return messageContent;
    }

    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    public boolean isTextToSpeech()
    {
        return textToSpeech;
    }

    public List<EmbedObject> getEmbeds()
    {
        return embeds;
    }

    public JsonObject toJson()
    {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();

        json.addProperty("content", this.messageContent);
        json.addProperty("username", this.username);
        json.addProperty("avatar_url", this.avatarUrl);
        json.addProperty("tts", this.textToSpeech);

        if (!this.embeds.isEmpty())
        {
            List<JsonObject> embedObjects = new ArrayList<>();

            for (EmbedObject embed : this.embeds)
            {
                JsonObject jsonEmbed = new JsonObject();

                jsonEmbed.addProperty("title", embed.getTitle());
                jsonEmbed.addProperty("description", embed.getDescription());
                jsonEmbed.addProperty("url", embed.getUrl());

                if (embed.getColor() != null)
                {
                    Color color = embed.getColor();
                    int rgb = color.getRed();
                    rgb = (rgb << 8) + color.getGreen();
                    rgb = (rgb << 8) + color.getBlue();

                    jsonEmbed.addProperty("color", rgb);
                }

                EmbedFooter footer = embed.getFooter();
                EmbedImage image = embed.getImage();
                EmbedThumbnail thumbnail = embed.getThumbnail();
                EmbedAuthor author = embed.getAuthor();
                List<EmbedField> fields = embed.getFields();

                if (footer != null)
                {
                    JsonObject jsonFooter = new JsonObject();

                    jsonFooter.addProperty("text", footer.getText());
                    jsonFooter.addProperty("icon_url", footer.getIconUrl());
                    jsonEmbed.add("footer", jsonFooter);
                }

                if (image != null)
                {
                    JsonObject jsonImage = new JsonObject();

                    jsonImage.addProperty("url", image.getUrl());
                    jsonEmbed.add("image", jsonImage);
                }

                if (thumbnail != null)
                {
                    JsonObject jsonThumbnail = new JsonObject();

                    jsonThumbnail.addProperty("url", thumbnail.getUrl());
                    jsonEmbed.add("thumbnail", jsonThumbnail);
                }

                if (author != null)
                {
                    JsonObject jsonAuthor = new JsonObject();

                    jsonAuthor.addProperty("name", author.getName());
                    jsonAuthor.addProperty("url", author.getUrl());
                    jsonAuthor.addProperty("icon_url", author.getIconUrl());
                    jsonEmbed.add("author", jsonAuthor);
                }

                List<JsonObject> jsonFields = new ArrayList<>();
                for (EmbedField field : fields)
                {
                    JsonObject jsonField = new JsonObject();

                    jsonField.addProperty("name", field.getName());
                    jsonField.addProperty("value", field.getValue());
                    jsonField.addProperty("inline", field.isInline());

                    jsonFields.add(jsonField);
                }

                jsonEmbed.add("fields", gson.toJsonTree(jsonFields));
                embedObjects.add(jsonEmbed);
            }

            json.add("embeds", gson.toJsonTree(embedObjects));
        }

        return json;
    }
}
