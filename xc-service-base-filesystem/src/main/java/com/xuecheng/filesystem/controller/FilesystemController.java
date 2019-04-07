package com.xuecheng.filesystem.controller;

import com.xuecheng.api.ﬁlesystem.FilesystemControllerApi;
import com.xuecheng.filesystem.service.FilesystemService;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.upload.FastDFSClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * Created by 周大侠
 * 2019-04-06 16:45
 */
@RestController
@RequestMapping("/filesystem")
public class FilesystemController implements FilesystemControllerApi {
    @Autowired
    private FilesystemService filesystemService;

    @Override
    @PostMapping("/upload")
    public UploadFileResult upload(MultipartFile file,
                                   String filetag,
                                   String businesskey,
                                   String metadata) {


        return filesystemService.saveFile(file,filetag,businesskey,metadata);
    }

}
