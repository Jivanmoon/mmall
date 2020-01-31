package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Jivan on 2020/1/27.
 */
public interface IFileService {
    public String upload(MultipartFile file, String path);
}
