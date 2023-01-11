package com.korit.library.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookRepository {

    /*
        C: 도서 등록
        R: 도서 전체 조회
           도서 검색
                1. 도서코드
                2. 도서명
                3. 저자
                4. 출판사
                    1) 전체 조회
                    2) 20개씩 가져오기
        U: 도서 수정
        D: 도서 삭제
     */

}
