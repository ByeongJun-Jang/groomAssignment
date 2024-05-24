package proj.tricount.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String loginId;

    public Member() {
    }

    public Member(Long id, String username, String password, String loginId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.loginId = loginId;
    }
}
