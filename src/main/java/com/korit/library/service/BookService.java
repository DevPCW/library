package com.korit.library.service;

import com.korit.library.exception.CustomValidationException;
import com.korit.library.repository.BookRepository;
import com.korit.library.web.dto.*;
import jdk.jfr.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class BookService {

    @Value("${file.path}") // 스프링 어노테이션 // 지역변수에 적어도 됨
    private String filePath; // 'yml' 에 들어있는 경로

    @Autowired
    private BookRepository bookRepository;

    public List<BookMstDto> searchBook(SearchReqDto searchReqDto) {
        searchReqDto.setIndex();
        return bookRepository.searchBook(searchReqDto);
    }

    public List<CategoryDto> getCategories() {
        return bookRepository.findAllCategory();
    }

    public void registerBook(BookReqDto bookReqDto) {
        duplicateBookCode(bookReqDto.getBookCode());
        bookRepository.saveBook(bookReqDto);
    }


    private void duplicateBookCode(String bookCode) {
        BookMstDto bookMstDto = bookRepository.findBookByBookCode(bookCode);
        if(bookMstDto != null) { // null 이 아니면 중복
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("bookCode", "이미 존재하는 도서코드입니다.");

            throw new CustomValidationException(errorMap);
        }
    }

    public void modifyBook(BookReqDto bookReqDto) {
        bookRepository.updateBookByBookCode(bookReqDto);
    }

    public void maintainModifyBook(BookReqDto bookReqDto) {
        bookRepository.maintainUpdateBookByBookCode(bookReqDto);
    }

    public void deleteBook(String bookCode) {
        bookRepository.deleteBookByBookCode(bookCode);
    }

    public void registerBookImages(String bookCode, List<MultipartFile> files) {
        if(files.size() < 1) { // 1보다 적으면 로직 실행
            Map<String, String> errorMap = new HashMap<String, String>();
            errorMap.put("file", "이미지를 선택하세요.");

            throw new CustomValidationException(errorMap);
        }

        List<BookImageDto> bookImageDtos = new ArrayList<BookImageDto>();

        files.forEach(file -> {
            String originFileName = file.getOriginalFilename();
            String extension = originFileName.substring(originFileName.lastIndexOf("."));
            String tempFileName = UUID.randomUUID().toString().replaceAll("-", "") + extension;

            Path uploadPath = Paths.get(filePath + "/book/" + tempFileName); // java.nio.File

            File f = new File(filePath + "/book"); // Java.io
            if(!f.exists()) { // 해당 경로가 없으면
                f.mkdirs(); // 경로 생성
            }

            try {
                Files.write(uploadPath, file.getBytes()); // java.nio.File // 여기 경로에다가 복사
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            BookImageDto bookImageDto = BookImageDto.builder()
                    .bookCode(bookCode)
                    .saveName(tempFileName)
                    .originName(originFileName)
                    .build();

            bookImageDtos.add(bookImageDto);
        });

        bookRepository.registerBookImages(bookImageDtos);
    }
}
