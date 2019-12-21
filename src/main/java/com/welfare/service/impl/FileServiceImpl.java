package com.welfare.service.impl;

import com.welfare.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/9/23 14:20
 * @Description:
 */
@Service
public class FileServiceImpl implements FileService {
    @Value("${welfare.file.path}")
    private String filePath;

    @Override
    public String uploadFile(MultipartFile file) {
        // 获取文件名
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = System.currentTimeMillis() + suffixName;
        File fileNew = new File(filePath + File.separator + newFileName);
        try {
            file.transferTo(fileNew);
            return newFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public File downloadFIle(String path) {
        File fileNew = new File(filePath + File.separator + path);
        if (fileNew.length() > 0) {
            return fileNew;
        }
        return null;
    }
}
