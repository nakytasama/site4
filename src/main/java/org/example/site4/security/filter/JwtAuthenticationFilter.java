package org.example.site4.security.filter;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.site4.security.domain.User;
import org.example.site4.security.service.JwtService;
import org.example.site4.security.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    public static final String COOKIE_NAME = "jwtToken";
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;

        String authHeader = request.getHeader(HEADER_NAME);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            jwt = authHeader.substring(BEARER_PREFIX.length());
        }
        else if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt != null) {
            try {
                String username = jwtService.extractUserName(jwt);

                if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Optional<User> userOptional = userService.findByUsernameSafe(username);

                    if (userOptional.isPresent()) {
                        UserDetails userDetails = userOptional.get();

                        if (jwtService.isTokenValid(jwt, userDetails)) {
                            SecurityContext context = SecurityContextHolder.createEmptyContext();
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            context.setAuthentication(authToken);
                            SecurityContextHolder.setContext(context);
                        } else {
                            System.out.println("JWT токен невалиден, удаляем: " + username);
                            removeJwtCookie(response);
                            SecurityContextHolder.clearContext();
                        }
                    } else {
                        System.out.println("Пользователь не найден в базе, удаляем токен: " + username);
                        removeJwtCookie(response);
                        SecurityContextHolder.clearContext();

                        if (request.getRequestURI().startsWith("/api/")) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Пользователь не найден\"}");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Ошибка при проверке JWT: " + e.getMessage());
                removeJwtCookie(response);
                SecurityContextHolder.clearContext();

                if (request.getRequestURI().startsWith("/api/")) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Ошибка аутентификации\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void removeJwtCookie(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie(COOKIE_NAME, "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
    }
}