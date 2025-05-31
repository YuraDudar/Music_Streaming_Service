package ru.SberPo666.security_service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder encoder;

    private static final String LOAD_USER_BY_USERNAME_SQL =
            "SELECT id, username, email, password, enabled FROM users WHERE username = ?";
    private static final String LOAD_AUTHORITIES_BY_USER_ID_SQL =
            "SELECT authority FROM authorities WHERE user_id = ?";

    @PostConstruct
    private void init(){
        if (!userExists("admin")) {
            jdbcTemplate.update(
                    """
                            INSERT INTO users (username, email, password, enabled)
                            VALUES (?, ?, ?, ?)
                            """,
                    "admin",
                    "email",
                    encoder.encode("admin"),
                    true
            );

            UUID adminId = jdbcTemplate.queryForObject(
                    "SELECT id FROM users WHERE username = ?",
                    UUID.class,
                    "admin"
            );

            if (adminId != null) {
                jdbcTemplate.update(
                        "INSERT INTO authorities (user_id, authority) VALUES (?, ?)",
                        adminId,
                        "ROLE_ADMIN"
                );
                jdbcTemplate.update(
                        "INSERT INTO authorities (user_id, authority) VALUES (?, ?)",
                        adminId,
                        "ROLE_USER"
                );
            }
        }

    }

    private boolean userExists(String username) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class, username
        );
        return count != null && count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails appUser = jdbcTemplate.query(LOAD_USER_BY_USERNAME_SQL, ps -> ps.setString(1, username), rs -> {
            if (rs.next()) {
                return CustomUserDetails.builder()
                        .id(rs.getObject("id", UUID.class))
                        .username(rs.getString("username"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .enabled(rs.getBoolean("enabled"))
                        .authorities(loadUserAuthorities(rs.getObject("id", UUID.class)))
                        .build();
            }
            return null;
        });

        if (appUser == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        return appUser;
    }

    private List<GrantedAuthority> loadUserAuthorities(UUID userId) {
        return jdbcTemplate.query(LOAD_AUTHORITIES_BY_USER_ID_SQL, ps -> ps.setObject(1, userId),
                (rs, rowNum) -> new SimpleGrantedAuthority(rs.getString("authority")));
    }

}
