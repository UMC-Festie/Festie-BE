package com.umc.FestieBE.domain.user.domain;


import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Enumerated;


@NoArgsConstructor
public enum Role {
    USER, MANAGER, ADMIN;
}