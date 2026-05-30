package com.deepsleep.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deepsleep.data.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Update("update user set avatar = #{avatarKey} where id = #{userId}")
    int updateAvatarById(
            @Param("userId") Long userId,
            @Param("avatarKey") String avatarKey
    );
}