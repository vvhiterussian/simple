package com.vvhiterussian.websockets.simple.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Subscription {
    private String sessionId;
    private String subsriptionId;
    private String destination;
}
