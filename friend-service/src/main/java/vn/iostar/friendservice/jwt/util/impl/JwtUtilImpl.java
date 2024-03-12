package vn.iostar.friendservice.jwt.util.impl;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.iostar.friendservice.constant.AppConstant;
import vn.iostar.friendservice.jwt.util.JwtUtil;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtilImpl implements JwtUtil {

	private static final Long JWT_ACCESS_EXPIRATION = 3600000L;
	private static final Long JWT_REFRESH_EXPIRATION = 604800000L;

	private Key getSigningKey() {
		return AppConstant.getSecretKey();
	}

	@Override
	public String extractUserId(final String token) {
		return this.extractClaims(token, Claims::getSubject);
	}

	@Override
	public Date extractExpiration(final String token) {
		return this.extractClaims(token, Claims::getExpiration);
	}

	@Override
	public <T> T extractClaims(final String token, Function<Claims, T> claimsResolver) {
		final Claims claims = this.extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(final String token) {
		return Jwts.parserBuilder()
				.setSigningKey(this.getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private Boolean isTokenExpired(final String token) {
		return this.extractExpiration(token).before(new Date());
	}

	@Override
	public Boolean validateToken(final String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey().getEncoded()).build().parseClaimsJws(token);
			return !this.isTokenExpired(token);
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
		}
		return false;
	}

}