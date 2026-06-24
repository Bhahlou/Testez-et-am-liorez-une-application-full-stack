package com.openclassrooms.test.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openclassrooms.starterjwt.security.jwt.AuthTokenFilter;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    // doFilterInternal() is protected (inherited contract from OncePerRequestFilter),
    // so we drive it through the public doFilter() entry point instead.
    @InjectMocks
    AuthTokenFilter authTokenFilter;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    UserDetailsServiceImpl userDetailsService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterShouldSetAuthenticationWhenTokenIsValid() throws Exception {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().id(1L).username("yoga@studio.com").build();

        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtils.validateJwtToken("valid-token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid-token")).thenReturn("yoga@studio.com");
        when(userDetailsService.loadUserByUsername("yoga@studio.com")).thenReturn(userDetails);

        authTokenFilter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(userDetails, authentication.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterShouldNotSetAuthenticationWhenAuthorizationHeaderIsMissing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        authTokenFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).validateJwtToken(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterShouldNotSetAuthenticationWhenHeaderDoesNotStartWithBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        authTokenFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils, never()).validateJwtToken(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterShouldNotSetAuthenticationWhenTokenIsInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtUtils.validateJwtToken("invalid-token")).thenReturn(false);

        authTokenFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterShouldContinueChainWhenAnExceptionIsThrown() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtils.validateJwtToken("valid-token")).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken("valid-token")).thenReturn("yoga@studio.com");
        when(userDetailsService.loadUserByUsername("yoga@studio.com"))
                .thenThrow(new UsernameNotFoundException("not found"));

        authTokenFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
