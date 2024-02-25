package vn.iotstart.apigateway.jwt.service;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
	
	String extractUserId(final String token);
	Date extractExpiration(final String token);
	<T> T extractClaims(final String token, final Function<Claims, T> claimsResolver);
	Boolean validateToken(final String token);
	String extractUserRole(final String token);
//
//	boolean isValidUser(String accessToken);
//	boolean isValidAdmin(String accessToken);
	
}










