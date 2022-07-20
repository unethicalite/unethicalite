package net.unethicalite.api.discord;

import net.unethicalite.api.discord.embeds.EmbedObject;
import lombok.Builder;
import lombok.Singular;

import java.util.List;


@Builder
public class DiscordMessage
{

    private final String username;
    private final String messageContent;
    private final String avatarUrl;
    private final boolean textToSpeech;

    @Singular
    private final List<EmbedObject> embeds;

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


}
