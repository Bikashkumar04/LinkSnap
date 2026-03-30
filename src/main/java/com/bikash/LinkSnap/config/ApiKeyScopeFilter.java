package com.bikash.LinkSnap.config;

import com.bikash.LinkSnap.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyScopeFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";

    private final ApiKeyService apiKeyService;

    public ApiKeyScopeFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey == null || apiKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        String requiredScope = resolveRequiredScope(request);
        if (requiredScope == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Long workspaceId = resolveWorkspaceId(request);
        if (workspaceId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "workspaceId is required for API key authorization");
            return;
        }

        boolean validKey = apiKeyService.validateApiKey(workspaceId, apiKey);
        if (!validKey) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");
            return;
        }

        boolean hasScope = apiKeyService.hasRequiredScope(workspaceId, apiKey, requiredScope);
        if (!hasScope) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "API key scope is insufficient");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Long resolveWorkspaceId(HttpServletRequest request) {
        String[] parts = request.getRequestURI().split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if ("workspaces".equals(parts[i])) {
                try {
                    return Long.parseLong(parts[i + 1]);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        String queryWorkspaceId = request.getParameter("workspaceId");
        if (queryWorkspaceId == null || queryWorkspaceId.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(queryWorkspaceId);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String resolveRequiredScope(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (!uri.startsWith("/api/")) {
            return null;
        }
        if (uri.startsWith("/api/auth/")) {
            return null;
        }

        if (uri.startsWith("/api/links") || uri.contains("/links/")) {
            return HttpMethod.GET.matches(method) ? "links:read" : "links:write";
        }
        if (uri.contains("/tags")) {
            return HttpMethod.GET.matches(method) ? "tags:read" : "tags:write";
        }
        if (uri.contains("/analytics")) {
            return HttpMethod.GET.matches(method) ? "analytics:read" : "analytics:write";
        }
        if (uri.contains("/api-keys")) {
            return HttpMethod.GET.matches(method) ? "api_keys:read" : "api_keys:write";
        }
        if (uri.contains("/workspaces") || uri.contains("/domains")) {
            return HttpMethod.GET.matches(method) ? "workspace:read" : "workspace:write";
        }
        return null;
    }
}
