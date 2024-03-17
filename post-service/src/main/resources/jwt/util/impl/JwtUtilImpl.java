package jwt.util.impl;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.iostar.postservice.constant.AppConstant;
import vn.iostar.postservice.jwt.util.JwtUtil;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtilImpl implements JwtUtil {
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










