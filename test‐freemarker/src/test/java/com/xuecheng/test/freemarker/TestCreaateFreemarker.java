package com.xuecheng.test.freemarker;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestCreaateFreemarker {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Test
    public void getTemplete() {

    }

    @Test
    public void saveFile() throws IOException {
        File file = new File("D:/course.ftl");
        if(file.exists()){
            //定义输入流
            FileInputStream inputStram = new FileInputStream(file);
            //向GridFS存储文件
            ObjectId objectId = gridFsTemplate.store(inputStram, "课程详情页静态化测试", "");
            //得到文件ID
            String fileId = objectId.toString();
            System.out.println(fileId);
        }

    }

}
