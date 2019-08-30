package com.jaclon.bootsecurity.dao;

import com.jaclon.bootsecurity.model.TokenModel;
import org.apache.ibatis.annotations.*;

/**
 * @author jaclon
 * @date 2019/8/14
 * @time 16:23
 */
@Mapper
public interface TokenDao {

    @Insert("insert into t_token(id, val, expireTime, createTime, updateTime) values (#{id}, #{val}, #{expireTime}, #{createTime}, #{updateTime})")
    int save(TokenModel model);

    @Select("select * from t_token t where t.id = #{id}")
    TokenModel getById(String id);

    @Update("update t_token t set t.val = #{val}, t.expireTime = #{expireTime}, t.updateTime = #{updateTime} where t.id = #{id}")
    int update(TokenModel model);

    @Delete("delete from t_token where id = #{id}")
    int delete(String id);
}
