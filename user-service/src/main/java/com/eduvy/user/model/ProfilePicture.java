package com.eduvy.user.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ProfilePicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private byte[] imageData;

    @OneToOne
    private UserDetails userDetails;


    public ProfilePicture(byte[] imageData, UserDetails userDetails) {
        this.imageData = imageData;
        this.userDetails = userDetails;
    }
}
