package querystore;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class UserManager {

    private final String serverUrl = "http://localhost:8080/api/auth"; // Change to your Render URL later
    private final HttpClient client;

    public UserManager(String storageDirectory) {
        // storageDirectory is no longer needed for cloud, but kept for compatibility
        this.client = HttpClient.newHttpClient();
    }

    public User authenticateUser(String username, String password) {
        String json = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return new User(username, ""); // We don't store password on client anymore
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String username, String password) {
        String json = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean userExists(String username) {
        // For simplicity, we can let the server handle this during registration
        return false; 
    }
}
