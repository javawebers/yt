package com.github.yt.web.test.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("fileUpload")
public class FileUploadController {

    Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @PostMapping(value = "uploadFile")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        logger.warn(new String(file.getBytes()));
    }

}
