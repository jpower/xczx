package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by 周大侠
 * 2019-04-06 16:50
 */
public interface FilesystemRespository extends MongoRepository<FileSystem, String> {
}
