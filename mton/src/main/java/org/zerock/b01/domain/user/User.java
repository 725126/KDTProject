package org.zerock.b01.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"userRole", "uPassword"})
public class User {
    @Id
    private String userId;

    private String uEmail;

    private String uPassword;

    private String uName;

    private String uPhone;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;
}
