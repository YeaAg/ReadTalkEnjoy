package com.project.service;

import com.project.dto.DiscussionDTO;
import com.project.dto.PageInfoDTO;
import com.project.mapper.DiscussionMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Log4j2
@Service
public class DiscussionService {

    @Autowired
    private DiscussionMapper discussionMapper;

    /**
     * 새로운 토론 주제를 생성
     */
    public void createDiscussion(String bookTitle, String topic, String contents, String userId, String bookIsbn) {
        byte[] bookImage = discussionMapper.getBookImageByTitle(bookTitle);
        if (bookImage == null) {
            throw new IllegalArgumentException("책 제목에 해당하는 이미지를 찾을 수 없습니다.");
        }

        DiscussionDTO discussion = new DiscussionDTO();
        discussion.setBookTitle(bookTitle);
        discussion.setTopic(topic);
        discussion.setContents(contents);
        discussion.setUserId(userId);
        discussion.setBookIsbn(bookIsbn);
        discussion.setBookImage(bookImage);

        log.info("service - discussion" + discussion);
        discussionMapper.createDiscussion(discussion);
    }

    /**
     * 특정 토론에 대한 댓글 수 반환
     */
    public Integer getCommentCountByDiscussion(Integer discussionId) {
        return discussionMapper.getCommentCountByDiscussion(discussionId);
    }

    /**
     * 가장 최근의 토론 주제 반환
     */
    public List<DiscussionDTO> getCurrentDiscussions() {
        return discussionMapper.getCurrentDiscussion();
    }

    /**
     * 페이징된 토론 목록 반환 (책 정보 포함)
     */
    public PageInfoDTO<DiscussionDTO> getDiscussionsWithBookInfo(PageInfoDTO<DiscussionDTO> pageInfo) {
        if (pageInfo.getPage() < 1) {
            pageInfo.setPage(1);
        }
        if (pageInfo.getSize() == null || pageInfo.getSize() <= 0) {
            pageInfo.setSize(5); // 기본 5개씩 노출
        }

        Integer totalDiscussionCount = discussionMapper.selectPaginatedDiscussionsTotalCount(pageInfo);
        pageInfo.setTotalElementCount(totalDiscussionCount != null ? totalDiscussionCount : 0);

        if (totalDiscussionCount != null && totalDiscussionCount > 0) {
            List<DiscussionDTO> discussions = discussionMapper.getDiscussions(pageInfo);
            for (DiscussionDTO discussion : discussions) {
                String recentComment = discussionMapper.getRecentCommentByDiscussionId(discussion.getId());
                discussion.setRecentComment(recentComment);
            }
            pageInfo.setElements(discussions);
        } else {
            pageInfo.setElements(Collections.emptyList());
        }

        return pageInfo;
    }

    /**
     * 책 제목으로 토론 검색
     */
    public PageInfoDTO<DiscussionDTO> getDiscussionByBookTitle(PageInfoDTO<DiscussionDTO> pageInfo, String title) {
        List<DiscussionDTO> result = discussionMapper.getDiscussionByBookTitle(pageInfo, title);
        Integer totalElementCount = discussionMapper.getTotalCountByTitle(title);
        pageInfo.setTotalElementCount(totalElementCount);
        pageInfo.setElements(result);
        return pageInfo;
    }

    /**
     * 마이 페이지, 내가 작성한 토론 글 조회
     */
    public List<DiscussionDTO> getMyDiscussion(PageInfoDTO<DiscussionDTO> pageInfo, String userId) {
        if(pageInfo.getPage() < 1) {
            pageInfo.setPage(1);
        }
        if(pageInfo.getSize() == null || pageInfo.getSize() <= 0) {
            pageInfo.setSize(5);
        }
        Integer totalDiscussionCount = discussionMapper.getTotalCountByUser(userId);

        if(totalDiscussionCount != null && totalDiscussionCount > 0) {
            List<DiscussionDTO> discussions = discussionMapper.getMyDiscussion(pageInfo, userId);
            pageInfo.setTotalElementCount(totalDiscussionCount);
            pageInfo.setElements(discussions);
        }
        return discussionMapper.getMyDiscussion(pageInfo, userId);
    }

    public Integer getMyDiscussionCount(String userId) {
        return discussionMapper.getTotalCountByUser(userId);
    }

    /**
     * 각 토론 페이지 조회
     */
    public DiscussionDTO selectDiscussionByDiscussionId(Integer discussionId) {
        return discussionMapper.selectDiscussionByDiscussionId(discussionId);
    }
}
