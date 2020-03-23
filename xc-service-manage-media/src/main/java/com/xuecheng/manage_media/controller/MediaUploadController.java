package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.MediaUploadControllerApi;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.service.MediaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media/upload")
public class MediaUploadController implements MediaUploadControllerApi {
    @Autowired
    private MediaUploadService mediaUploadService;

    @Override
    @PostMapping("/register")
    public ResponseResult register(@RequestParam("fileMd5") String fileMd5,
                                   @RequestParam("fileName") String fileName,
                                   @RequestParam Long fileSize,
                                   @RequestParam String mimetype,
                                   @RequestParam String fileExt) {
        return mediaUploadService.register(fileMd5, fileName, String.valueOf(fileSize), mimetype, fileExt);
    }

    @Override
    @PostMapping("/checkchunk")
    public CheckChunkResult checkchunk(@RequestParam String fileMd5,
                                       @RequestParam Integer chunk,
                                       @RequestParam Integer chunkSize) {
        return mediaUploadService.checkchunk(fileMd5, String.valueOf(chunk), String.valueOf(chunkSize));
    }

    @Override
    @PostMapping("/uploadchunk")
    public ResponseResult uploadchunk(@RequestParam("file") MultipartFile file, @RequestParam Integer chunk,
                                      @RequestParam String fileMd5) {
        return mediaUploadService.uploadchunk(file, fileMd5, String.valueOf(chunk));
    }

    @Override
    @PostMapping("/mergechunks")
    public ResponseResult mergechunks(@RequestParam String fileMd5,
                                      @RequestParam String fileName,
                                      @RequestParam Long fileSize,
                                      @RequestParam String mimetype,
                                      @RequestParam String fileExt) {
        return mediaUploadService.mergechunks(fileMd5,fileName,(fileSize),mimetype,fileExt);
    }
}
