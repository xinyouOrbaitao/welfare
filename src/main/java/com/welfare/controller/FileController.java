package com.welfare.controller;

import com.alibaba.fastjson.JSONObject;
import com.welfare.service.FileService;
import com.welfare.util.ImgTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author ：zhangyue.
 * @Title :
 * @Date ：Created in 2019/9/23 14:23
 * @Description:
 */
@Controller
@RequestMapping("/file")
public class FileController {
    private Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (file.isEmpty()) {
                jsonObject.put("code", "error");
                logger.warn("file is empty");
                return jsonObject.toJSONString();
            }
            jsonObject.put("code", "SUCCESS");
            String fileName = fileService.uploadFile(file);
            jsonObject.put("fileName", fileName);
        } catch (Exception e) {
            logger.error("fileUpload exception", e);
        }
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "/down")
    public void download(@RequestParam String fileId, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(fileId)) {
            return;
        }
        String fileName = fileId.substring(fileId.lastIndexOf("/") + 1);
        File file = fileService.downloadFIle(fileId);
        if (file == null) {
            return;
        }
        String contentType = "application/octet-stream";
        String fileType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        ImgTypeEnum imgTypeEnum = ImgTypeEnum.valueOfCode(fileType);
        if (null != imgTypeEnum) {
            contentType = imgTypeEnum.getContentType();
        }
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "inline; filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1"));
        OutputStream out = response.getOutputStream();
        FileInputStream fis = new FileInputStream(file);
        try {
            byte[] buffer = new byte[1024];
            int len;
            //读入需要下载的文件的内容，打包到zip文件
            while ((len = fis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            response.flushBuffer();

        } catch (Exception e) {
            logger.error("download exception", e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.warn("out close exception");
                }
            }
            fis.close();
        }
    }

}
