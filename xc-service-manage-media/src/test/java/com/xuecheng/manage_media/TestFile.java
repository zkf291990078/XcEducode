package com.xuecheng.manage_media;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFile {

    @Test
    public void testChunk() {
        File sourceFile = new File("D:/ffmpeg/lucene.mp4");
        String chunkPath = "D:/ffmpeg/chunk/";
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        try {
            RandomAccessFile read_file = new RandomAccessFile(sourceFile, "r");
            long chunkSize = 1024 * 1024 * 1;
            long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
            if (chunkNum < 1) {
                chunkNum = 1;
            }
            byte[] b = new byte[1024];
            for (int i = 0; i < chunkNum; i++) {
                File chunkFile = new File(chunkPath + i);
                boolean newFile = chunkFile.createNewFile();
                if (newFile) {
                    RandomAccessFile write_file = new RandomAccessFile(chunkFile, "rw");
                    int len = -1;
                    while ((len = read_file.read(b)) != -1) {
                        write_file.write(b, 0, len);
                        if (chunkFile.length() > chunkSize) {
                            break;
                        }
                    }
                    write_file.close();
                }
            }
            read_file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //测试文件合并方法
    @Test
    public void testMerge() throws IOException {
        //块文件目录
        File chunkFolder = new File("D:/ffmpeg/chunk/");
        //合并文件
        File mergeFile = new File("D:/ffmpeg/lucene1.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile write_file = new RandomAccessFile(mergeFile, "rw");
        write_file.seek(0);
        byte[] b = new byte[1024];
        File[] files = chunkFolder.listFiles();
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
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                write_file.write(b, 0, len);
            }
            raf_read.close();
        }
        write_file.close();


    }
}