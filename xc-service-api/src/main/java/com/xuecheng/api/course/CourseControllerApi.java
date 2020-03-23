package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.response.CourseView;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);

    @ApiOperation("添加课程计划")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan);

    @ApiOperation("查询课程列表")
    public QueryResponseResult getCourseList(int page, int size, CourseListRequest courseListRequest);

    @ApiOperation("添加课程")
    public AddCourseResult addCourseBase(CourseBase courseBase);

    @ApiOperation("查询课程")
    public CourseBase getCourseBaseById(String courseId);

    @ApiOperation("修改课程")
    public ResponseResult updateCourseBase(String courseId, CourseBase courseBase);

    @ApiOperation("获取课程营销信息")
    public CourseMarket getCourseMarketById(String courseId);

    //更新课程营销信息
    @ApiOperation("更新课程营销信息")
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket);

    @ApiOperation("添加图片")
    public ResponseResult addCoursePic(String id, String pic);

    @ApiOperation("查询图片")
    public CoursePic getCoursePic(String id);

    @ApiOperation("删除图片")
    public ResponseResult deletePicById(String id);

    @ApiOperation("获取课程详情模板数据")
    public CourseView getCoureseViewInfo(String id);

    @ApiOperation("获取课程详情页面url")
    public CoursePublishResult preview(String id);

    @ApiOperation("发布课程")
    public CoursePublishResult publish(@PathVariable String id);

    @ApiOperation("保存媒资信息")
    public ResponseResult savemedia(TeachplanMedia teachplanMedia);

}
