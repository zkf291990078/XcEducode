package com.xuecheng.api.fileSystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileSystemControllerApi {

    public UploadFileResult upLoadFile(MultipartFile multipartFile, String filetag, String businesskey, String metadata);

}
