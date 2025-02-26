package com.project.mapper;

import com.project.dto.DiscussionDTO;
import com.project.dto.PageInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussionMapper {
    void createDiscussion(DiscussionDTO discussion);
    String getRecentCommentByDiscussionId(@Param("discussionId") Integer discussionId);
    Integer getCommentCountByDiscussion(@Param("discussionId") Integer discussionId);
    List<DiscussionDTO> getCurrentDiscussion();
    Integer selectPaginatedDiscussionsTotalCount(@Param("pageInfo") PageInfoDTO<DiscussionDTO> pageInfo);
    List<DiscussionDTO> getDiscussions(@Param("pageInfo") PageInfoDTO<DiscussionDTO> pageInfo);
    List<DiscussionDTO> getDiscussionByBookTitle(@Param("pageInfo") PageInfoDTO<DiscussionDTO> pageInfo, @Param("title") String title);
    byte[] getBookImageByTitle(@Param("title") String title);
    List<DiscussionDTO> getMyDiscussion(@Param("pageInfo") PageInfoDTO<DiscussionDTO> pageInfo, @Param("userId") String userId);
    DiscussionDTO selectDiscussionByDiscussionId(@Param("discussionId") Integer discussionId);
    Integer getTotalCountByTitle(@Param("title") String title);
    Integer getTotalCountByUser(@Param("userId") String userId);
}
