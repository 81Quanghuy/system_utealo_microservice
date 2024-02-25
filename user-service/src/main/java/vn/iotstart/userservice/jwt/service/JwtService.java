package vn.iotstart.userservice.jwt.service;

import io.jsonwebtoken.Claims;
import vn.iotstart.userservice.entity.Account;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
	
	String extractUserId(final String token);
	Date extractExpiration(final String token);
	<T> T extractClaims(final String token, final Function<Claims, T> claimsResolver);
	String generateAccessToken(final Account account);
	String generateRefreshToken(final Account account);
	Boolean validateToken(final String token);
	
}










