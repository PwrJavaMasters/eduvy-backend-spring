package com.eduvy.user.service.impl;


import com.eduvy.user.model.ProfilePicture;
import com.eduvy.user.model.UserDetails;
import com.eduvy.user.repository.ProfilePictureRepository;
import com.eduvy.user.repository.UserDetailsRepository;
import com.eduvy.user.service.ProfilePictureService;
import com.eduvy.user.service.UserService;
import com.eduvy.user.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Service
@AllArgsConstructor
public class ProfilePictureServiceImpl implements ProfilePictureService {

    UserService userService;

    ProfilePictureRepository profilePictureRepository;
    UserDetailsRepository userDetailsRepository;


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
    @Transactional
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
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400, must-revalidate")
                .contentType(MediaType.parseMediaType(fileType))
                .body(profilePicture.getImageData());
    }

    @Override
    @Transactional
    public ResponseEntity<byte[]> getProfilePicture(String hash) {
        String mail = Utils.decodeUserMail(hash);
        UserDetails userDetails = userDetailsRepository.findByEmail(mail);
        if(userDetails == null) {
            return ResponseEntity.notFound().build();
        }

        ProfilePicture profilePicture = profilePictureRepository.findByUserDetails(userDetails);
        if (profilePicture == null) {
            try {
                byte[] defaultAvatar = loadDefaultAvatar();

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"default-avatar.jpg\"")
                        .header(HttpHeaders.CACHE_CONTROL, "max-age=86400, must-revalidate")
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(defaultAvatar);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        String fileType = "image/jpeg";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"profile-picture.jpg\"")
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400, must-revalidate")
                .contentType(MediaType.parseMediaType(fileType))
                .body(profilePicture.getImageData());
    }

    private byte[] loadDefaultAvatar() throws IOException {
        ClassPathResource resource = new ClassPathResource("default_avatar.jpg");
        try (InputStream in = resource.getInputStream()) {
            return in.readAllBytes();
        }
    }
}
