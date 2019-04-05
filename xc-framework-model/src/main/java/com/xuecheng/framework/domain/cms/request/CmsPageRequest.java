package com.xuecheng.framework.domain.cms.request;

import com.xuecheng.framework.model.request.RequestData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by 周大侠
 * 2019-03-31 9:55
 */
@Data
public class CmsPageRequest extends RequestData {
    // 站点ID
    @ApiModelProperty("站点id")
    private String siteId;
    // 页面ID
    @ApiModelProperty("页面id")
    private String pageId;
    // 页面名称
    @ApiModelProperty("页面名称")
    private String pageName;
    // 别名
    @ApiModelProperty("别名")
    private String pageAliase;
    // 模版id
    @ApiModelProperty("模板id")
    private String templateId;
}
