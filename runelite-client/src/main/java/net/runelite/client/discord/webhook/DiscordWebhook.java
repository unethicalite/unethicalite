package net.runelite.client.discord.webhook;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import java.io.IOException;

public class DiscordWebhook
{

	private final String webhookUrl;

	public DiscordWebhook(String webhookUrl){
		this.webhookUrl = webhookUrl;
	}

	/**
	 * Send a message to a discord webhook
	 * @param message The message to send
	 */
	public void send(DiscordMessage message){
		new Thread(() ->
		{

			OkHttpClient httpClient = new OkHttpClient();
			RequestBody requestBody = RequestBody.create(message.toJson().toString().getBytes());

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
				e.printStackTrace();
			}


		}).start();
	}


}
