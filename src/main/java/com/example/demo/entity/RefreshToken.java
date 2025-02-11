package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshToken_id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

}
