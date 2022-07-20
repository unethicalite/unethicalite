package net.runelite.client.discord.webhook;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.unethicalite.api.discord.DiscordMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.unethicalite.api.discord.embeds.*;


@Slf4j
public class DiscordWebhook
{

    private final String webhookUrl;

    public DiscordWebhook(String webhookUrl)
    {
        this.webhookUrl = webhookUrl;
    }

    /**
     * Send a message to a discord webhook
     *
     * @param message The message to send
     */
    public void send(DiscordMessage message)
    {

        new Thread(() ->
        {

            OkHttpClient httpClient = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(toJson(message).toString().getBytes());

            Request request = new Request.Builder()
                    .url(webhookUrl)
                    .method("POST", requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try
            {
                httpClient.newCall(request).execute();
            }
            catch (IOException e)
            {
                log.warn("Failed to submit message to discord webhook", e);
            }


        }).start();
    }


    private JsonObject toJson(DiscordMessage discordMessage)
    {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();

        json.addProperty("content", discordMessage.getMessageContent());
        json.addProperty("username", discordMessage.getUsername());
        json.addProperty("avatar_url", discordMessage.getAvatarUrl());
        json.addProperty("tts", discordMessage.isTextToSpeech());

        if (!discordMessage.getEmbeds().isEmpty())
        {
            List<JsonObject> embedObjects = new ArrayList<>();

            for (var embed : discordMessage.getEmbeds())
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
                String image = embed.getImage();
                String thumbnail = embed.getThumbnail();
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

                    jsonImage.addProperty("url", image);
                    jsonEmbed.add("image", jsonImage);
                }

                if (thumbnail != null)
                {
                    JsonObject jsonThumbnail = new JsonObject();

                    jsonThumbnail.addProperty("url", thumbnail);
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
