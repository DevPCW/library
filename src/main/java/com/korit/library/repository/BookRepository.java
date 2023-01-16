package com.korit.library.repository;

import com.korit.library.web.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookRepository {

    /*
        C: 도서 등록
        R: [1]. 도서 전체 조회
                {1}. 검색
                    1. 도서코드
                    2. 도서명
                    3. 저자
                    4. 출판사
                {2}. 카테고리
                    1. 전체 조회
                    2. 20개씩 가져오기
           [2]. 도서코드로 조회
        U: 도서 수정
        D: 도서 삭제
     */
    public List<BookMstDto> searchBook(SearchReqDto searchReqDto);

    public BookMstDto findBookByBookCode(String bookCode);

    public List<CategoryDto> findAllCategory();

    public int saveBook(BookReqDto bookReqDto);

    public int updateBookByBookCode(BookReqDto bookReqDto);
    public int maintainUpdateBookByBookCode(BookReqDto bookReqDto);


    public int deleteBookByBookCode(String bookCode);

    public int registerBookImages(List<BookImageDto> bookImageDtos);

}
