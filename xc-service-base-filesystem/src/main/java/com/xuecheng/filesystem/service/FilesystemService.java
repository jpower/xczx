package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FilesystemRespository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.upload.FastDFSClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
/**
 * Created by 周大侠
 * 2019-04-06 16:45
 */
@Service
@Slf4j
public class FilesystemService {
    @Autowired
    private FilesystemRespository filesystemRespository;

    /**
     * 保存文件信息到Mongdb
     * @param multipartFile
     * @param filetag
     * @param businesskey
     * @param metadata
     * @return
     */
    public UploadFileResult saveFile(MultipartFile multipartFile, String filetag, String businesskey, String metadata) {
        if(multipartFile == null) {
            ExceptionCast.exception(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        String fileId = null;
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            // 取得文件扩展名
            String originalFilename = multipartFile.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf("." ) + 1);
            // 执行上传
            fileId = fastDFSClient.uploadFile(multipartFile.getBytes(), extName);
        } catch (Exception e) {
            e.printStackTrace();
            // 上传文件失败
            log.error("上传文件失败！！！{}",e.getMessage());
            ExceptionCast.exception(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setFiletag(filetag);
        if(StringUtils.isNotEmpty(metadata)) {
            Map map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);
        }
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileSize(multipartFile.getSize());
        fileSystem.setFileType(multipartFile.getContentType());
        filesystemRespository.save(fileSystem);

        return new UploadFileResult(CommonCode.SUCCESS, fileSystem);
    }


}
