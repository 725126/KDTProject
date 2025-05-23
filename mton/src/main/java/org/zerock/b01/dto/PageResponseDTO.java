package org.zerock.b01.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PageResponseDTO<E> {

    private int page;
    private int size;
    private int total;

    // 시작 페이지 번호
    private int start;
    // 마지막 페이지 번호
    private int end;

    private boolean prev;
    private boolean next;

    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {
        if (total <= 0) {
            return;
        }

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList;

        // 마지막 페이지 번호 계산
        int last = (int) Math.ceil((total / (double) size));

        // ✅ 모든 페이지를 한 번에 다 보여주기 위한 설정
        this.start = 1;
        this.end = last;

        this.prev = this.page > 0;
        this.next = this.page + 1 < last;
    }
}
