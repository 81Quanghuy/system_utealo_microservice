package vn.iostar.userservice.jwt.service;

import io.jsonwebtoken.Claims;
import vn.iostar.userservice.entity.Account;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {

	String extractUserId(final String token);
	Date extractExpiration(final String token);
	<T> T extractClaims(final String token, final Function<Claims, T> claimsResolver);
	Boolean validateToken(final String token);
	String generateAccessToken(final Account credential);
	String generateRefreshToken(final Account credential);

}