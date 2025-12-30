package io.quarkus.omnifaces.runtime;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.quarkus.security.identity.SecurityIdentity;

/**
 * Basic Jakarta SecurityContext implementation
 * based on Quarkus' SecurityIdentity
 *
 * @author Leonardo Bernardes
 */
@Named
@RequestScoped
public class SecurityContextImpl implements SecurityContext {

    @Inject
    SecurityIdentity identity;

    @Override
    public Principal getCallerPrincipal() {
        return identity.isAnonymous() ? null : identity.getPrincipal();
    }

    @Override
    public <T extends Principal> Set<T> getPrincipalsByType(Class<T> pType) {
        Set<T> result = new HashSet<>();

        if (!identity.isAnonymous())
            result.add(identity.getPrincipal(pType));

        return result;
    }

    @Override
    public boolean isCallerInRole(String role) {
        if (identity.isAnonymous())
            return false;

        return identity.hasRole(role);
    }

    @Override
    public Set<String> getAllDeclaredCallerRoles() {
        Set<String> result = new HashSet<>();

        if (!identity.isAnonymous())
            result = identity.getRoles();

        return result;
    }

    @Override
    public boolean hasAccessToWebResource(String resource, String... methods) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public AuthenticationStatus authenticate(HttpServletRequest request, HttpServletResponse response,
            AuthenticationParameters parameters) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
