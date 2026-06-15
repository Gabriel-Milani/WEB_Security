package com.exemplo.secrest.controller;

import com.exemplo.secrest.dto.RequestCodeDto;
import com.exemplo.secrest.dto.VerifyCodeDto;
import com.exemplo.secrest.dto.VerifyCodeResponseDto;
import com.exemplo.secrest.service.AuthCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthCodeService authCodeService;

    public AuthController(AuthCodeService authCodeService) {
        this.authCodeService = authCodeService;
    }

    @PostMapping("/request-code")
    public ResponseEntity<Void> requestCode(@RequestBody RequestCodeDto dto) {
        if (dto.email() == null || dto.email().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        authCodeService.requestCode(dto.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<VerifyCodeResponseDto> verifyCode(@RequestBody VerifyCodeDto dto) {
        if (dto.email() == null || dto.email().isBlank() || dto.code() == null || dto.code().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String token = authCodeService.verifyCodeAndGenerateToken(dto.email(), dto.code());
        return ResponseEntity.ok(new VerifyCodeResponseDto(token != null, token));
    }
}
