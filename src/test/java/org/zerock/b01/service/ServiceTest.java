package org.zerock.b01.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.PostDTO;

@SpringBootTest
@Log4j2
public class ServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Test
    public void testRegister() {
        log.info(postService.getClass().getName());

        PostDTO postDTO = PostDTO.builder()
                .pTitle("title Service test")
                .pCode(1)
                .pContent("서비스 내용입니다.")
                .build();

        Long pno = postService.register(postDTO);

        log.info("pno: {}", pno);
    }

    @Test
    public void testRegisters() {
        for (int i = 0; i < 50; i++) {
            log.info(postService.getClass().getName());

            PostDTO postDTO = PostDTO.builder()
                    .pTitle("게시글 테스트 제목..." + i)
                    .pCode(1)
                    .pContent("게시글 등록 테스트 내용..." + i)
                    .build();

            Long pno = postService.register(postDTO);

            log.info("pno: {}", pno);
        }
    }


    @Test
    public void testModifyPost() {
        // 변경에 필요한 데이터만
        PostDTO postDTO = PostDTO.builder()
                .pno(1L)
                .pTitle("수정된 제목입니다.")
                .pContent("수정된 새로운 내용입니다")
                .build();

        postService.modify(postDTO);
    }

    @Test
    public void testListPost() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(9)
                .build();

        PageResponseDTO<PostDTO> responseDTO = postService.list(pageRequestDTO);
        log.info("responseDTO: {}", responseDTO);
    }

    @Test
    public void testModifyComment() {
        
    }

}
