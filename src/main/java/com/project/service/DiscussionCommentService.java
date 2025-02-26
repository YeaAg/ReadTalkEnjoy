package com.project.service;

import com.project.dto.DiscussionCommentDTO;
import com.project.dto.PageInfoDTO;
import com.project.dto.ReviewDTO;
import com.project.mapper.DiscussionCommentMapper;
import com.project.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class DiscussionCommentService {

    @Autowired
    private DiscussionCommentMapper discussionCommentMapper;

    @Autowired
    private UserMapper userMapper;

    private static final int POINTS_THRESHOLD = 1000; // 포인트 지급 값
    private static final int COMMENT_COUNT_THRESHOLD = 10; // 댓글 10개 조건
    private static final int COMMENT_LIKE_AND_UNLIKE_SUM = 50;

    public void addComment(Integer discussionId, String userId, String content) {
        boolean hasCommented = discussionCommentMapper.hasUserCommented(discussionId, userId);
        if (hasCommented) {
            throw new IllegalStateException("이미 해당 토론에 댓글을 작성했습니다.");
        }
        discussionCommentMapper.addComment(discussionId, userId, content);
        Integer commentCount = discussionCommentMapper.getCommentCountByDiscussion(discussionId);
        if (commentCount == COMMENT_COUNT_THRESHOLD) {
            String authorId = discussionCommentMapper.getDiscussionAuthorById(discussionId); // 토론 작성자 ID 가져오기
            userMapper.addPointToUser(authorId, POINTS_THRESHOLD);
        }
    }


    public void addLike(Integer commentId, String userId) {
        if (discussionCommentMapper.hasUserVoted(userId, commentId) > 0) {
            throw new IllegalStateException("이미 투표한 댓글입니다.");
        }

        // 좋아요 증가
        discussionCommentMapper.addUserVote(commentId, userId);
        discussionCommentMapper.incrementLike(commentId);

        // 현재 찬성/반대 총합 조회
        Integer totalVotes = discussionCommentMapper.getTotalVotesByCommentId(commentId);

        // 포인트 지급 조건 확인 (찬성 + 반대 합이 50일 경우)
        if (totalVotes == COMMENT_LIKE_AND_UNLIKE_SUM) {
            String authorId = discussionCommentMapper.getDiscussionCommentAuthor(commentId);
            userMapper.addPointToUser(authorId, POINTS_THRESHOLD);
            log.info("포인트 지급 완료: " + POINTS_THRESHOLD + "점, 사용자: " + authorId);
        }
    }

    public void addUnlike(Integer commentId, String userId) {
        if (discussionCommentMapper.hasUserVoted(userId, commentId) > 0) {
            throw new IllegalStateException("이미 투표한 댓글입니다.");
        }

        // 반대 증가
        discussionCommentMapper.addUserVote(commentId, userId);
        discussionCommentMapper.incrementUnlike(commentId);

        // 현재 찬성/반대 총합 조회
        Integer totalVotes = discussionCommentMapper.getTotalVotesByCommentId(commentId);

        // 포인트 지급 조건 확인 (찬성 + 반대 합이 50일 경우)
        if (totalVotes == COMMENT_LIKE_AND_UNLIKE_SUM) {
            String authorId = discussionCommentMapper.getDiscussionCommentAuthor(commentId);
            userMapper.addPointToUser(authorId, POINTS_THRESHOLD);
            log.info("포인트 지급 완료: " + POINTS_THRESHOLD + "점, 사용자: " + authorId);
        }
    }

    public Integer getLikeCount(Integer commentId) {
        return discussionCommentMapper.getLikeCount(commentId);
    }

    public Integer getUnlikeCount(Integer commentId) {
        return discussionCommentMapper.getUnlikeCount(commentId);
    }

    public Integer getDiscussionIdByCommentId(Integer commentId) {
        return discussionCommentMapper.getDiscussionIdByCommentId(commentId);
    }

    public PageInfoDTO<DiscussionCommentDTO> getCommentsWithSortAndPagination(PageInfoDTO<DiscussionCommentDTO> pageInfo, Integer discussionId) {
        pageInfo.setSize(3);
        if(pageInfo.getPage() < 1) {
            return null;
        }
        List<DiscussionCommentDTO> comments = discussionCommentMapper.getCommentsWithSortAndPagination(pageInfo, discussionId);
        if(!comments.isEmpty()) {
            Integer totalElementCount = discussionCommentMapper.getTotalCommentsByDiscussionId(discussionId);
            pageInfo.setTotalElementCount(totalElementCount);
            pageInfo.setElements(comments);
        }
        return pageInfo;
    }
    public DiscussionCommentDTO getFirstComment() {
        return discussionCommentMapper.getFirstComment();
    }
    public DiscussionCommentDTO getSecondComment() {
        return discussionCommentMapper.getSecondComment();
    }

}
