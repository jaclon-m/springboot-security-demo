package com.jaclon.bootsecurity.dao;

import com.jaclon.bootsecurity.model.FileInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author jaclon
 * @date 2019/8/27
 */

@Mapper
public interface FileInfoDao {

    @Select("select * from file_info t where t.id = #{id}")
    FileInfo getById(String md5);

    /**
     * 更新文件上传时间
     * @param file
     * @return
     */
    @Update("update file_info t set t.updateTime = now() where t.id = #{id}")
    int update(MultipartFile file);

    @Insert("insert into file_info(id, contentType, size, path, url, type, createTime, updateTime) values(#{id}, #{contentType}, #{size}, #{path}, #{url}, #{type}, now(), now())")
    int save(FileInfo fileInfo);

    int count(@Param("params") Map<String, Object> params);

    List<FileInfo> list(@Param("params") Map<String, Object> params, @Param("offset") Integer offset,
                        @Param("limit") Integer limit);

    @Delete("delete from file_info where id = #{id}")
    int delete(String id);
}
