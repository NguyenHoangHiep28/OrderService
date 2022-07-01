package com.example.orderservice.service;

import com.example.orderservice.entity.dto.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {
    @Override
    public String getAuthenticatedUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
