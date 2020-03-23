package com.xuecheng.manage_cms_client.mq;


import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class CustomPagePost {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomPagePost.class);

    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private PageService pageService;

    @RabbitListener(queues={"${xuecheng.mq.queue}"})
    public void post(String msg) {
        Map map = JSON.parseObject(msg, Map.class);
        String pageId = (String) map.get("pageId");
        LOGGER.info("receive cms post page:{}", msg);
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (!optional.isPresent()) {
            LOGGER.error("receive cms post page,cmsPage is null:{}", msg);
            ExceptionCast.cast(CommonCode.CMS_PAGE_NOTEXISTS);
        }
        pageService.savePageToServerPath(pageId);
    }
}
