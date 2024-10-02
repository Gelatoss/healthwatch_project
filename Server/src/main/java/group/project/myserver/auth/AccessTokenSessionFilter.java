package group.project.myserver.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AccessTokenSessionFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal (
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
            ) throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                String accessToken = (String) session.getAttribute("accessToken");
                if (accessToken != null) {
                    authHeader = "Bearer " + accessToken;
                }
            }
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            AddCustomHeaderRequestWrapper wrappedRequest = new AddCustomHeaderRequestWrapper(request);
            wrappedRequest.addHeader("Authorization", authHeader);
            filterChain.doFilter(wrappedRequest, response);
        }
        else {
            filterChain.doFilter(request, response);
        }
    }

}
