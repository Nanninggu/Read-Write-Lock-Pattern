package com.example.Read_Write.Lock.Pattern.mapper;

import com.example.Read_Write.Lock.Pattern.dto.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM public.user WHERE id = #{id}")
    User getUserById(int id);

    @Update("UPDATE public.user SET username = #{username}, email = #{email}, state = #{state} WHERE id = #{id}")
    int updateUser(User user);
}
