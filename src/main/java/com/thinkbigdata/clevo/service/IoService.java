package com.thinkbigdata.clevo.service;

import com.thinkbigdata.clevo.entity.UserImage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IoService {
    @Value("${img.location}") private String imgLocation;
    public UserImage saveImage(MultipartFile imageFile) throws IOException {
        String originName = imageFile.getOriginalFilename();
        String extension = originName.substring(originName.lastIndexOf("."));
        String savedFileName = UUID.randomUUID() + extension;
        String path = "/images/user/profile/"+savedFileName;

        FileOutputStream fos = new FileOutputStream(imgLocation+"/"+savedFileName);
        fos.write(imageFile.getBytes());
        fos.close();

        UserImage userImage = new UserImage();
        userImage.setName(savedFileName);
        userImage.setOriginName(originName);
        userImage.setPath(path);
        return userImage;
    }
}
