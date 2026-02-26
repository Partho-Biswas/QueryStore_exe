package querystore;

import java.util.UUID;

public class UserQuery {
    private String id;
    private String title;
    private String content;

    public UserQuery(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return title;
    }
}
