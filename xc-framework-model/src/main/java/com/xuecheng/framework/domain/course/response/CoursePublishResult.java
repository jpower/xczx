package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程预览响应对象
 * Created by 周大侠
 * 2019-04-07 15:53
 */
@Data
@NoArgsConstructor
public class CoursePublishResult extends ResponseResult {
    private String previewUrl;

    public CoursePublishResult(ResultCode resultCode, String previewUrl) {
        super(resultCode);
        this.previewUrl = previewUrl;
    }
}
