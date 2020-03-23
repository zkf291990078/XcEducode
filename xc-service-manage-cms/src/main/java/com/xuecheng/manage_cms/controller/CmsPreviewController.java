package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Controller
public class CmsPreviewController extends BaseController {

    @Autowired
    CmsPageService pageService;

    //接收到页面id
    @RequestMapping(value = "/cms/preview/{pageId}", method = RequestMethod.GET)
    public void preview(@PathVariable("pageId") String pageId) {
        String html = pageService.getTempleteHtml(pageId);
        try {
            response.setHeader("content-type","text/html;charset=utf-8");
            ServletOutputStream servletResponse = response.getOutputStream();
            servletResponse.write(html.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
