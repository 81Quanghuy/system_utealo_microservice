package vn.iostar.friendservice.jwt.service.impl;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.iostar.friendservice.jwt.service.JwtService;
import vn.iostar.friendservice.jwt.util.JwtUtil;

import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

	private final JwtUtil jwtUtil;

	@Override
	public String extractUserId(final String token) {
		log.info("JwtServiceImpl, String, extractCredentialId");
		return this.jwtUtil.extractUserId(token);
	}

	@Override
	public Date extractExpiration(final String token) {
		log.info("JwtServiceImpl, Date, extractExpiration");
		return this.jwtUtil.extractExpiration(token);
	}

	@Override
	public <T> T extractClaims(final String token, final Function<Claims, T> claimsResolver) {
		log.info("JwtServiceImpl, <T> T, extractClaims");
		return this.jwtUtil.extractClaims(token, claimsResolver);
	}

	@Override
	public Boolean validateToken(final String token) {
		log.info("JwtServiceImpl, Boolean, validateToken");
		return this.jwtUtil.validateToken(token);
	}
}