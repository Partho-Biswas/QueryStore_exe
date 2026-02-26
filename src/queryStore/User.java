package querystore;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password; // Insecure for demonstration. In production, this should be a hashed password.

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    // In a real application, this would compare a hashed password.
    public boolean checkPassword(String passwordAttempt) {
        return this.password.equals(passwordAttempt);
    }

    // For demonstration purposes only. Never expose in production.
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
               "username='" + username + "'" +
               '}';
    }
}
