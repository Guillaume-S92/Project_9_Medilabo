package com.medilabo.auth.document;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public class UserDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String passwordHash;
    private Set<String> roles;
    private boolean enabled;

    public UserDocument() {
    }

    public UserDocument(String id, String username, String passwordHash, Set<String> roles, boolean enabled) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = roles;
        this.enabled = enabled;
    }

    public String getId() { return id; }
    public UserDocument setId(String id) { this.id = id; return this; }
    public String getUsername() { return username; }
    public UserDocument setUsername(String username) { this.username = username; return this; }
    public String getPasswordHash() { return passwordHash; }
    public UserDocument setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
    public Set<String> getRoles() { return roles; }
    public UserDocument setRoles(Set<String> roles) { this.roles = roles; return this; }
    public boolean isEnabled() { return enabled; }
    public UserDocument setEnabled(boolean enabled) { this.enabled = enabled; return this; }
}
