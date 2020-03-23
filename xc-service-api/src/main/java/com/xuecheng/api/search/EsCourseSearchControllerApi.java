package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;

import java.util.Map;

public interface EsCourseSearchControllerApi {
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam);

    public Map<String, CoursePub> getall(String id);
}
