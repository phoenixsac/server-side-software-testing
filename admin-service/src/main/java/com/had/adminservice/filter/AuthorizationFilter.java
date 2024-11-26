//package com.had.adminservice.filter;
//
//import com.had.adminservice.entity.User;
//import com.had.adminservice.repository.UserRepository;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import java.io.IOException;
//
//@Component
//public class AuthorizationFilter implements Filter, WebFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);
//
//    @Autowired
//    UserRepository userRepo;
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // Initialization logic, if needed
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        // Get email and type headers from the request
//        String email = httpRequest.getHeader("X-User-Email");
//        String userType = httpRequest.getHeader("X-User-Type");
//
//        logger.info("Incoming request for authorization. Email: {}, User Type: {}", email, userType);
//
//        // Perform authorization logic based on email and type
//        boolean authorized = isAuthorized(email, userType);
//
//        if (authorized) {
//            // User is authorized, proceed with the request
//            logger.info("User is authorized to access the resource.");
//            chain.doFilter(request, response);
//        } else {
//            // User is not authorized, send a forbidden response
//            logger.warn("No admin privilege. Access denied.");
//            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        }
//    }
//
//    private boolean isAuthorized(String email, String userType) {
//
//        User user = userRepo.findByEmail(email);
//        return user != null && user.getType().equalsIgnoreCase("admin");
//    }
//
//    @Override
//    public void destroy() {
//        // Cleanup logic, if needed
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        return null;
//    }
//}
