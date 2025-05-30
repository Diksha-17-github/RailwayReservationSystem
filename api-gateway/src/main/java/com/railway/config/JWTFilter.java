package com.railway.config;

import com.railway.service.JWTService;
import com.railway.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip filter for login and register endpoints
        if (path.startsWith("/login") || path.startsWith("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        System.out.println("Request Path: " + path);
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Extracted Token: " + token);
            username = jwtService.extractUserName(token);
            System.out.println("Extracted Username: " + username);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
            System.out.println("Loaded UserDetails: " + userDetails.getUsername());

            if (jwtService.validateToken(token, userDetails)) {
                System.out.println("Token is valid");

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("Authentication set in SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());

                // Role-based access control: block non-admins from PUT and DELETE on /api/trains
                boolean isAdmin = userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

                if (!isAdmin) {
                    if (request.getMethod().equalsIgnoreCase("PUT") || request.getMethod().equalsIgnoreCase("DELETE")) {
                        String requestURI = request.getRequestURI();

                        if (requestURI.startsWith("/api/trains")) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Access denied: Admins only");
                            return;
                        }
                    }

                    if (request.getMethod().equalsIgnoreCase("GET")) {
                        String requestURI = request.getRequestURI();

                        if (requestURI.startsWith("/users")) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Access denied: Admins only");
                            return;
                        }
                    }
                }
            } else {
                System.out.println("Token is invalid or expired");
            }
        }

        filterChain.doFilter(request, response);
        System.out.println("Request Path After Filter: " + request.getRequestURI());
    }
}
