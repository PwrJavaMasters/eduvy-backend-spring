package com.eduvy.user.repository;

import com.eduvy.user.model.ProfilePicture;
import com.eduvy.user.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {

    public ProfilePicture findByUserDetails(UserDetails userDetails);
}
