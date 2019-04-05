package com.xuecheng.manager.cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import javafx.application.Application;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by 周大侠
 * 2019-04-02 14:57
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestCmsPage {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private RestTemplate restTemplate;
    @Test
    public void fun1() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream("C:/src/index_banner.ftl");
        ObjectId objectId = gridFsTemplate.store(inputStream, "index_banner.ftl", "");
        System.out.println(objectId);
    }

    @Test
    public void fun2() throws IOException {
        String fileId = "5ca30bdd45ff2c07e0eb3785";

        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);

        String s = IOUtils.toString(gridFsResource.getInputStream());
        System.out.println(s);


    }
    @Test
    public void fun3() {

        gridFsTemplate.delete(Query.query(Criteria.where("_id").is("5ca30a4345ff2c50a4a15482")));

    }

    @Test
    public void fun4() {
        ResponseEntity<CmsConfig> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/5a791725dd573c3574ee333f", CmsConfig.class);
        System.out.println(forEntity.toString());
    }

    @Test
    public void fun5() {
    }

}
