package com.project.mapper;

import com.project.dto.DiscussionCommentDTO;
import com.project.dto.PageInfoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DiscussionCommentMapper {
    void incrementLike(@Param("commentId") Integer commentId);
    void incrementUnlike(@Param("commentId") Integer commentId);
    Integer getTotalVotesByCommentId(@Param("commentId") Integer commentId);
    Integer getCommentCountByDiscussion(@Param("discussionId") Integer discussionId);
    void addComment(@Param("discussionId") Integer discussionId, @Param("userId") String userId, @Param("content") String content);
    Integer hasUserVoted(String userId, Integer commentId);
    void addUserVote(@Param("commentId") Integer commentId, @Param("userId") String userId);
    String getDiscussionAuthorById(@Param("discussionId") Integer discussionId);
    List<DiscussionCommentDTO> getCommentsWithSortAndPagination(@Param("pageInfo") PageInfoDTO<DiscussionCommentDTO> pageInfo,
                                                                @Param("discussionId") Integer discussionId);
    Integer getTotalCommentsByDiscussionId(@Param("discussionId") Integer discussionId);
    DiscussionCommentDTO getFirstComment();
    DiscussionCommentDTO getSecondComment();
    Integer getDiscussionIdByCommentId(@Param("commentId") Integer commentId);
    Integer getLikeCount(@Param("commentId") Integer commentId);
    Integer getUnlikeCount(@Param("commentId") Integer commentId);
    String getDiscussionCommentAuthor(@Param("commentId") Integer commentId);
    @Select("SELECT COUNT(*) FROM discussion_comment WHERE discussion_id = #{discussionId} AND user_id = #{userId}")
    boolean hasUserCommented(@Param("discussionId") Integer discussionId, @Param("userId") String userId);
}
