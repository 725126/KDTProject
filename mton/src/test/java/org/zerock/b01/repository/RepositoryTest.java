package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b01.domain.Comment;
import org.zerock.b01.domain.Post;
import org.zerock.b01.dto.PostListCommentCountDTO;

@SpringBootTest
@Log4j2
public class RepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void testInsertPost() {
        Post post = Post.builder()
                .pTitle("title test")
                .pCode(1)
                .mid("tester_ID")
                .pContent("내용입니다.")
                .build();

        Post result = postRepository.save(post);

        log.info("pno: " + result.getPno());
    }

    @Test
    public void testSearchCommentCountPost() {
        String[] types = {"t", "c", "w"};
        String keyword = "title";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("pno").descending());

        Page<PostListCommentCountDTO> result = postRepository.searchWithCommentCount(types, keyword, pageable);

        // total pages
        log.info(result.getTotalPages());
        // page size
        log.info(result.getSize());
        // page number
        log.info(result.getNumber());
        log.info(result.hasPrevious() + " : " +result.hasNext());

        result.getContent().forEach(log::info);
    }

    @Test
    public void testInsertComment() {
        Long pno = 12L;

        Post post = Post.builder().pno(pno).build();

        Comment comment = Comment.builder()
//                .pno(12L)
                .post(post)
                .mid("tester_ID")
                .cContent("좋은 정보 감사합니다.")
                .build();

        commentRepository.save(comment);
    }

    @Test
    public void testInsertComments() {
        Long pno = 12L;

        Post post = Post.builder().pno(pno).build();

        for (int i = 0; i < 100; i++) {
            Comment comment = Comment.builder()
//                    .pno(12L)
                    .post(post)
                    .mid("testerID" + i)
                    .cContent("test content...." + i)
                    .build();

            commentRepository.save(comment);
        }
    }

    @Test
    public void testPostComments() {
        Long pno = 12L;

        Pageable pageable = PageRequest.of(0, 10, Sort.by("cno").descending());

        Page<Comment> result = commentRepository.listOfPost(pno, pageable);

        result.getContent().forEach(log::info);
    }

}
