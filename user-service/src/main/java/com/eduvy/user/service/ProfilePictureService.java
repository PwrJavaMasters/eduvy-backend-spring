package com.eduvy.user.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ProfilePictureService {

    public ResponseEntity<Void> uploadProfilePicture(MultipartFile file);

    public ResponseEntity<byte[]> getProfilePicture();

    public ResponseEntity<byte[]> getProfilePicture(String hash);
}
