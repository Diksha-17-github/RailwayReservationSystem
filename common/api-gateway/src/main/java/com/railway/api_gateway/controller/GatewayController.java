package com.railway.api_gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {

    @GetMapping("/csrf")
    public CsrfToken getcasrf(HttpServletRequest request)
    {
        return (CsrfToken) request.getAttribute("_csrf");
    }
}
