package net.unethicalite.api.discord.embeds;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmbedAuthor
{
    private String name;
    private String url;
    private String iconUrl;

}