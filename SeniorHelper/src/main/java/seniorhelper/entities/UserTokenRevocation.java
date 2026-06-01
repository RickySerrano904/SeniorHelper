package seniorhelper.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "user_token_revocations")
public class UserTokenRevocation {

    @Id
    @Column(length = 255)
    private String username;

    @Column(nullable = false)
    private Instant revokedAfter;

    public UserTokenRevocation() {}

    public UserTokenRevocation(String username, Instant revokedAfter) {
        this.username = username;
        this.revokedAfter = revokedAfter;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getRevokedAfter() {
        return revokedAfter;
    }

    public void setRevokedAfter(Instant revokedAfter) {
        this.revokedAfter = revokedAfter;
    }
}
