package com.eduvy.user.service.impl;


import com.eduvy.user.model.ProfilePicture;
import com.eduvy.user.model.UserDetails;
import com.eduvy.user.repository.ProfilePictureRepository;
import com.eduvy.user.service.ProfilePictureService;
import com.eduvy.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Service
@AllArgsConstructor
public class ProfilePictureServiceImpl implements ProfilePictureService {

    UserService userService;

    ProfilePictureRepository profilePictureRepository;


    @Override
    @Transactional
    public ResponseEntity<Void> uploadProfilePicture(MultipartFile file) {
        UserDetails userDetails = userService.getUserFromContext();
        if(userDetails == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageData;
        try {
            imageData = file.getBytes();

        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return ResponseEntity.badRequest().build();
        }

        ProfilePicture profilePicture = profilePictureRepository.findByUserDetails(userDetails);
        if (profilePicture == null) {
            profilePicture = new ProfilePicture();
            profilePicture.setUserDetails(userDetails);
        }

        profilePicture.setImageData(imageData);
        profilePicture.setUserDetails(userDetails);

        profilePictureRepository.save(profilePicture);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<byte[]> getProfilePicture() {
        UserDetails userDetails = userService.getUserFromContext();
        if(userDetails == null) {
            return ResponseEntity.notFound().build();
        }

        ProfilePicture profilePicture = profilePictureRepository.findByUserDetails(userDetails);
        if (profilePicture == null) {
            return ResponseEntity.notFound().build();
        }

        String fileType = "image/jpeg";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"profile-picture.jpg\"")
                .contentType(MediaType.parseMediaType(fileType))
                .body(profilePicture.getImageData());
    }
}
