package group.project.myserver.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.hibernate.sql.ast.tree.expression.Collation;

import java.util.*;

public class AddCustomHeaderRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String> customHeaders;

    public AddCustomHeaderRequestWrapper(HttpServletRequest request) {
        super(request);
        customHeaders = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = customHeaders.get(name);
        if (headerValue != null) {
            return headerValue;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        HashSet<String> headerNames = new HashSet<>(customHeaders.keySet());
        Enumeration<String> parentHeaderNames = super.getHeaderNames();
        while (parentHeaderNames.hasMoreElements()) {
            headerNames.add(parentHeaderNames.nextElement());
        }
        return Collections.enumeration(headerNames);
    }
}
