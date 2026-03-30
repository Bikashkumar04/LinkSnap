package com.bikash.LinkSnap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {

    private Long id;
    private Long workspaceId;
    private String name;
    private String color;
    private LocalDateTime createdAt;
}
