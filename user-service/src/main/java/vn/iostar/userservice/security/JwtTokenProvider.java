package vn.iostar.userservice.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import vn.iostar.userservice.repository.AccountRepository;


@Component
@Log4j2
public class JwtTokenProvider {
	@Autowired
	AccountRepository userRepository;

	private final Long JWT_ACCESS_EXPIRATION =   36000000L;
	private final Long JWT_REFRESH_EXPIRATION = 604800000L;

	private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	private final String issuer = "Nhom 6";

	private Key getSigningKey() {
		return secretKey;
	}

	public String generateAccessToken(UserDetail userDetail) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + JWT_ACCESS_EXPIRATION);

		return Jwts.builder().setSubject((userDetail.getUser().getUser().getUserId()))
				.claim("userId", userDetail.getUser().getUser().getUserId()).setIssuer(issuer).setIssuedAt(now)
				.setExpiration(expiryDate).signWith(getSigningKey(), SignatureAlgorithm.HS512).compact();
	}

	public String generateRefreshToken(UserDetail userDetail) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + JWT_REFRESH_EXPIRATION);

		return Jwts.builder().setSubject((userDetail.getUser().getUser().getUserId()))
				.claim("userId", userDetail.getUser().getUser().getUserId()).setIssuer(issuer).setIssuedAt(now)
				.setExpiration(expiryDate).signWith(getSigningKey(), SignatureAlgorithm.HS512).compact();

	}

	public String getUserIdFromJwt(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
		return ((String) claims.get("userId"));
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey().getEncoded()).build().parseClaimsJws(authToken);
			return true;
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
		}
		return false;
	}

}
