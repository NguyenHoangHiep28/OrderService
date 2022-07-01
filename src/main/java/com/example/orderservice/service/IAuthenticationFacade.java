package com.example.orderservice.service;

import com.example.orderservice.entity.dto.CurrentUser;
import org.springframework.security.core.Authentication;

import java.security.Principal;

public interface IAuthenticationFacade {
    String getAuthenticatedUserId();
}
