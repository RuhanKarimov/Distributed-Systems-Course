package com.campusflow.monolith.enrollment;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LatencyInjectionFilter extends OncePerRequestFilter {

    private final LatencyInjector latencyInjector;

    public LatencyInjectionFilter(LatencyInjector latencyInjector) {
        this.latencyInjector = latencyInjector;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Actuator endpointlerini yavaşlatma (istersen kaldır)
        String path = request.getRequestURI();
        if (path.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🐢 gecikmeyi enjekte et
        latencyInjector.maybeSleep();

        filterChain.doFilter(request, response);
    }
}