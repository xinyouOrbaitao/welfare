package com.welfare.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @Author ：chenxinyou.
 * @Title :
 * @Date ：Created in 2019/9/23 14:20
 * @Description:
 */
public interface FileService {
    public String uploadFile(MultipartFile file);

    public File downloadFIle(String path);
}
