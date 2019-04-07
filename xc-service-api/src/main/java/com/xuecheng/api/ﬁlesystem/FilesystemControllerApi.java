package com.xuecheng.api.ﬁlesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 周大侠
 * 2019-04-06 16:41
 */
@Api(value="文件上传接口",description = "文件上传接口，提供文件相关操作")
public interface FilesystemControllerApi {

    /**
     * 上传文件
     * @param multipartFile
     * @param filetag
     * @param businesskey
     * @param metadata
     * @return
     */
    @ApiOperation("文件上传")
    UploadFileResult upload(MultipartFile multipartFile,
                                   String filetag,
                                   String businesskey,
                                   String metadata
                                   );


}
