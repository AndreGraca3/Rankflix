package pt.graca.service.external.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pt.graca.infra.GsonInstantTypeAdapter;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.Instant;

public class DiscordWebhookService {

    public DiscordWebhookService(String webhookUrl) {
        this.WEBHOOK_URL = webhookUrl;
    }

    private final String WEBHOOK_URL;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new GsonInstantTypeAdapter())
            .create();

    public void sendMessage(String message) {
        try {
            var json = gson.toJson(new WebhookPayload(message));

            URL url = new URI(WEBHOOK_URL).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            OutputStream stream = con.getOutputStream();
            stream.write(json.getBytes());
            stream.flush();
            stream.close();

            con.getInputStream().close();
            con.disconnect();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send ranking to Discord", e);
        }

    }
}

class WebhookPayload {
    public final String content;

    public WebhookPayload(String content) {
        this.content = content;
    }
}