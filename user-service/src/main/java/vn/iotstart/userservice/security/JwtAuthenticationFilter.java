package vn.iotstart.userservice.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailService userDetailService;

    private Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CustomHttpServletRequestWrapper requestWrapper = new CustomHttpServletRequestWrapper(request);
        try {
            Optional<String> jwt = getJwtFromRequest(requestWrapper);

            if (jwt.isPresent() && jwtTokenProvider.validateToken(jwt.get())) {
                String id = jwtTokenProvider.getUserIdFromJwt(jwt.get());

                UserDetails userDetails = userDetailService.loadUserByUserId(id);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(requestWrapper));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    response.addHeader("Authorization", "Bearer " + jwt);
                }
            }
        } catch (MalformedJwtException | ExpiredJwtException | SignatureException e) {
            handleJwtException(response, e, "Invalid JWT token. Please login again!");
        } catch (Exception ex) {
            handleGenericException(response, ex, "Please login again!");
        } finally {
            filterChain.doFilter(requestWrapper, response);
        }
    }

    // Hàm xử lý cho các ngoại lệ liên quan đến JWT
    private void handleJwtException(HttpServletResponse response, JwtException ex, String errorMessage)
            throws IOException {
        response.setStatus(401);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonError = String.format(
                "{\"success\": false, \"message\": \"Access Denied\", \"result\": \"%s\", \"statusCode\": \"401\"}",
                errorMessage);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonError);
            out.flush();
        }
    }

    // Hàm xử lý cho các ngoại lệ thông thường
    private void handleGenericException(HttpServletResponse response, Exception ex, String errorMessage)
            throws IOException {
        ex.printStackTrace(); // Bạn có thể thay thế bằng logging framework như SLF4J
        response.setStatus(401);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonError = String.format(
                "{\"success\": false, \"message\": \"Access Denied\", \"result\": \"%s\", \"statusCode\": \"401\"}",
                errorMessage);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonError);
            out.flush();
        }
    }
}
