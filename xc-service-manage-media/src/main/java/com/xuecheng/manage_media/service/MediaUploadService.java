package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class MediaUploadService {
    private final static Logger LOGGER = LoggerFactory.getLogger(MediaUploadService.class);
    @Autowired
    MediaFileRepository mediaFileRepository;
    //上传文件根目录
    @Value("${xc-service-manage-media.upload-location}")
    String uploadPath;
    @Value("${xc-service-manage-media.mq.queue-media-video-processor}")
    String queueMediaVideoProcessor;
    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    String routingkeyMediaVideo;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /*** 根据文件md5得到文件路径
     * * 规则：
     * * 一级目录：md5的第一个字符
     * * 二级目录：md5的第二个字符
     * * 三级目录：md5
     * * 文件名：md5+文件扩展名
     * * @param fileMd5 文件md5值
     * * @param fileExt 文件扩展名
     * * @return 文件路径 */
    private String getFilePath(String fileMd5, String fileExt) {
        String filePath = uploadPath + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
        return filePath;
    }

    //得到文件目录相对路径，路径中去掉根目录
    private String getFileFolderRelativePath(String fileMd5, String fileExt) {
        String filePath = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        return filePath;
    }

    //得到文件所在目录
    private String getFileFolderPath(String fileMd5) {
        String fileFolderPath = uploadPath + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        return fileFolderPath;
    }

    //创建文件目录
    private boolean createFileFold(String fileMd5) {
        //创建上传文件目录
        String fileFolderPath = getFileFolderPath(fileMd5);
        File fileFolder = new File(fileFolderPath);
        if (!fileFolder.exists()) {
            //创建文件夹
            boolean mkdirs = fileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }

    public ResponseResult register(String fileMd5, String fileName, String fileSize, String mimetype, String fileExt) {
        //检查文件是否上传
        // 1、得到文件的路径 String filePath = getFilePath(fileMd5, fileExt);
        // File file = new File(filePath);
        // 2、查询数据库文件是否存在 Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        // 文件存在直接返回
        // if(file.exists() && optional.isPresent()){ ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST); }
        // boolean fileFold = createFileFold(fileMd5); if(!fileFold){
        // 上传文件目录创建失败 ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_CREATEFOLDER_FAIL); }
        String filePath = getFilePath(fileMd5, fileExt);
        File file_upload = new File(filePath);
        Optional<MediaFile> optionalMediaFile = mediaFileRepository.findById(fileMd5);
        if (file_upload.exists() && optionalMediaFile.isPresent()) {
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        boolean iscreateFile = createFileFold(fileMd5);
        if (!iscreateFile) {
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //得到块文件所在目录
    private String getChunkFileFolderPath(String fileMd5) {
        String fileChunkFolderPath = getFileFolderPath(fileMd5) + "/" + "chunks" + "/";
        return fileChunkFolderPath;
    }

    //检查块文件
    public CheckChunkResult checkchunk(String fileMd5, String chunk, String chunkSize) {
        String chunkPath = getChunkFileFolderPath(fileMd5);
        File file_chunk = new File(chunkPath + chunk);
        if (file_chunk.exists()) {
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK, true);
        } else {
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK, false);
        }
    }

    //块文件上传
    public ResponseResult uploadchunk(MultipartFile file, String fileMd5, String chunk) {
        //创建块文件目录
        boolean fileFold = createChunkFileFolder(fileMd5);
        String chunkFilePath = getChunkFileFolderPath(fileMd5);
        File chunkFile = new File(chunkFilePath + chunk);
        if (!chunkFile.exists()) {
            try {
                chunkFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(chunkFile);
            inputStream = file.getInputStream();
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    private boolean createChunkFileFolder(String fileMd5) {
        String chunkpath = getChunkFileFolderPath(fileMd5);
        File file = new File(chunkpath);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        String chunkpath = getChunkFileFolderPath(fileMd5);
        File chunkfileFolder = new File(chunkpath);
        if (!chunkfileFolder.exists()) {
            chunkfileFolder.mkdirs();
        }
        File mergeFile = new File(getFilePath(fileMd5, fileExt));
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        try {
            boolean isNewFile = mergeFile.createNewFile();
            if (!isNewFile) {
                ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
            }
            List<File> chunkFiles = getChunkFileSortList(chunkfileFolder);
            mergeFile = mergeFiles(chunkFiles, mergeFile);
            if (mergeFile == null) {
                ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
            }
            //校验文件
            boolean checkResult = this.checkFileMd5(mergeFile, fileMd5);
            if (!checkResult) {
                ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
            }
            //将文件信息保存到数据库
            MediaFile mediaFile = new MediaFile();
            mediaFile.setFileId(fileMd5);
            mediaFile.setFileName(fileMd5 + "." + fileExt);
            mediaFile.setFileOriginalName(fileName);
            //文件路径保存相对路径
            mediaFile.setFilePath(getFileFolderRelativePath(fileMd5, fileExt));
            mediaFile.setFileSize(fileSize);
            mediaFile.setUploadTime(new Date());
            mediaFile.setMimeType(mimetype);
            mediaFile.setFileType(fileExt);
//状态为上传成功
            mediaFile.setFileStatus("301002");
            MediaFile save = mediaFileRepository.save(mediaFile);
            sendRabbitmqMessage(fileMd5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    private void sendRabbitmqMessage(String fileid) {
        Optional<MediaFile> optionalMediaFile = mediaFileRepository.findById(fileid);
        if (!optionalMediaFile.isPresent()) {
            ExceptionCast.cast(CommonCode.FAIL);
        }
        HashMap<String, String> map = new HashMap<>();
        MediaFile mediaFile = optionalMediaFile.get();
        map.put("mediaId", mediaFile.getFileId());
        String json = JSON.toJSONString(map);
        LOGGER.info("send media process task msg:{}", json);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK, routingkeyMediaVideo, json);
    }

    //校验文件的md5值
    private boolean checkFileMd5(File mergeFile, String md5) {
        if (mergeFile == null || StringUtils.isEmpty(md5)) {
            return false;
        }
        //进行md5校验
        FileInputStream mergeFileInputstream = null;
        try {
            mergeFileInputstream = new FileInputStream(mergeFile);
            //得到文件的md5
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileInputstream);
            //比较md5
            if (md5.equalsIgnoreCase(mergeFileMd5)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("checkFileMd5 error,file is:{},md5 is: {}", mergeFile.getAbsoluteFile(), md5);
        } finally {
            try {
                mergeFileInputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private File mergeFiles(List<File> chunkFiles, File mergeFile) {
        //用于写文件
        RandomAccessFile write_file = null;
        try {
            write_file = new RandomAccessFile(mergeFile, "rw");
            write_file.seek(0);
            byte[] b = new byte[1024];
            for (File file : chunkFiles) {
                RandomAccessFile raf_read = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_read.read(b)) != -1) {
                    write_file.write(b, 0, len);
                }
                raf_read.close();
            }
            write_file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mergeFile;
    }

    private List<File> getChunkFileSortList(File chunkfileFolder) {
        File[] files = chunkfileFolder.listFiles();
        List<File> fileList = new ArrayList<>(Arrays.asList(files));
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                    return -1;
                }
                return 1;
            }
        });
        return fileList;
    }
}