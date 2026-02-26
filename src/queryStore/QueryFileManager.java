package querystore;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryFileManager {

    private final String serverUrl = "http://localhost:8080/api/queries";
    private final String username;
    private final HttpClient client;

    public QueryFileManager(String baseStorageDirectory, String username) {
        this.username = username;
        this.client = HttpClient.newHttpClient();
    }

    public void saveQuery(String queryName, String queryContent) throws java.io.IOException {
        // More robust escaping for JSON
        String escapedContent = queryContent.replace("\\", "\\\\")
                                           .replace("\"", "\\\"")
                                           .replace("\n", "\\n")
                                           .replace("\r", "\\r")
                                           .replace("\t", "\\t");
        
        String escapedTitle = queryName.replace("\\", "\\\\")
                                       .replace("\"", "\\\"");
                                           
        String json = String.format("{\"title\":\"%s\", \"content\":\"%s\", \"username\":\"%s\"}", 
                                     escapedTitle, escapedContent, username);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new java.io.IOException("Server returned error: " + response.statusCode() + " - " + response.body());
            }
        } catch (InterruptedException e) {
            throw new java.io.IOException("Save interrupted", e);
        }
    }

    public List<UserQuery> listQueries() throws java.io.IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/" + username))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return new ArrayList<>();
            }
            return parseQueriesFromJson(response.body());
        } catch (InterruptedException e) {
            throw new java.io.IOException("Fetch interrupted", e);
        }
    }

    private List<UserQuery> parseQueriesFromJson(String json) {
        List<UserQuery> queries = new ArrayList<>();
        // Robust manual parsing since we avoid external libraries
        // Looking for patterns like "id":"...", "title":"...", "content":"..."
        
        String[] objects = json.split("\\},\\{");
        for (String obj : objects) {
            String id = extractValue(obj, "id");
            String title = extractValue(obj, "title");
            String content = extractValue(obj, "content");
            
            if (id != null && title != null) {
                queries.add(new UserQuery(id, title, decodeJsonString(content)));
            }
        }
        return queries;
    }

    private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\":\"(.*?)\"";
        Matcher matcher = Pattern.compile(pattern).matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String decodeJsonString(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    public boolean queryExists(String queryName) {
        try {
            List<UserQuery> queries = listQueries();
            for (UserQuery q : queries) {
                if (q.getTitle().equalsIgnoreCase(queryName)) {
                    return true;
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return false; 
    }

    public void deleteQuery(String queryId) throws java.io.IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/" + queryId))
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new java.io.IOException("Server returned error: " + response.statusCode() + " - " + response.body());
            }
        } catch (InterruptedException e) {
            throw new java.io.IOException("Delete interrupted", e);
        }
    }

    public String loadQuery(String queryName) throws java.io.IOException {
        return "";
    }
}
