package com.adam.post.mapper;

import com.adam.post.model.entity.Tag;
import com.adam.post.model.vo.PostTagVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
* @author chenjiahan
* @description 针对表【tag(标签表)】的数据库操作Mapper
* @createDate 2025-01-19 19:31:39
* @Entity com.adam.post.model.entity.Tag
*/
public interface TagMapper extends BaseMapper<Tag> {

    List<PostTagVO> selectTagVOListByPostId(@Param("postId") Long postId);

    List<PostTagVO> selectTagVOListByPostIdList(@Param("postIds") Set<Long> postIds);
}




