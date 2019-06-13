package com.vvhiterussian.websockets.simple.principal;

import lombok.Getter;

import java.security.Principal;

public class StompPrincipal implements Principal {
    @Getter
    private String name;
}
