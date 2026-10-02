package gr.aueb.cf.schoolapp.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public CustomAuthenticationFailureHandler() {
        super("/login?error");      // redirect
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.warn("Authentication failed: username={} reason={}, ip={}, userAgent={}",
                request.getParameter("username"),
                request.getClass().getSimpleName(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        super.onAuthenticationFailure(request, response, exception);
    }
}
