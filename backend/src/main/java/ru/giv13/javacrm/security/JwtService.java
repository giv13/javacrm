package ru.giv13.javacrm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.giv13.javacrm.user.User;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final HttpServletResponse httpServletResponse;
    @Value("${security.jwt.secret}")
    private String jwtSecret;
    @Value("${security.jwt.expiration}")
    private Duration jwtExpiration;
    @Value("${security.jwt.token-name}")
    private String tokenName;

    public void generateCookie(User user) {
        String token = generateToken(user);
        Cookie cookie = new Cookie(tokenName, token);
        cookie.setMaxAge((int) jwtExpiration.toSeconds());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }

    public void eraseCookie() {
        Cookie cookie = new Cookie(tokenName, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }

    private String generateToken(User user) {
        return buildToken(user, jwtExpiration);
    }

    private String buildToken(User user, Duration expiration) {
        return Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, User user) {
        return extractUsername(token).equals(user.getUsername()) && !user.isTokenExpired() && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<?> extractAuthorities(String token) {
        return extractClaim(token, claims -> claims.get("auth", List.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
