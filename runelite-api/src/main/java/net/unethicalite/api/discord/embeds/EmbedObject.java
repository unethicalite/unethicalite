package net.unethicalite.api.discord.embeds;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.awt.*;

@Builder
@Getter
public class EmbedObject
{
    private String title;
    private String description;
    private String url;
    private Color color;

    private EmbedFooter footer;
    private String thumbnail;
    private String image;
    private EmbedAuthor author;

    @Singular
    private java.util.List<EmbedField> fields;

}