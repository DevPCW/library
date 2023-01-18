package com.korit.library.service;

import com.korit.library.entity.BookImage;
import com.korit.library.entity.BookMst;
import com.korit.library.entity.CategoryView;
import com.korit.library.exception.CustomValidationException;
import com.korit.library.repository.BookRepository;
import com.korit.library.web.dto.*;
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
    private String filePath; // 'yml' 에 들어있는 경로 // 'BookService' 가 생성될 때 filePath 변수에  yml 경로가 할당 됨
    // C:/junil/web-3-202210/library/upload

    @Autowired
    private BookRepository bookRepository;

    public List<BookMst> searchBook(SearchReqDto searchReqDto) {
        searchReqDto.setIndex();
        return bookRepository.searchBook(searchReqDto);
    }

    public List<CategoryView> getCategories() {
        return bookRepository.findAllCategory();
    }

    public void registerBook(BookReqDto bookReqDto) {
        duplicateBookCode(bookReqDto.getBookCode());
        bookRepository.saveBook(bookReqDto);
    }


    private void duplicateBookCode(String bookCode) {
        BookMst bookMst = bookRepository.findBookByBookCode(bookCode);
        if(bookMst != null) { // null 이 아니면 중복
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

        List<BookImage> bookImages = new ArrayList<BookImage>();

        files.forEach(file -> {
            String originFileName = file.getOriginalFilename();
            String extension = originFileName.substring(originFileName.lastIndexOf("."));
            String tempFileName = UUID.randomUUID().toString().replaceAll("-", "") + extension;

            Path uploadPath = Paths.get(filePath + "book/" + tempFileName); // java.nio.File // Path 객체만 만들어진 상태

            File f = new File(filePath + "book"); // Java.io
            if(!f.exists()) { // 해당 경로가 없으면
                f.mkdirs(); // 경로 생성
            }

            try {
                Files.write(uploadPath, file.getBytes()); // java.nio.File // 클라이언트로부터 받은 이미지를 uploadPath 경로에다가 복사(써라)
            } catch (IOException e) { // 경로 없으면 오류가 나기 때문에 'IOException' 잡아줘야함.
                throw new RuntimeException(e);
            }

            BookImage bookImage = BookImage.builder()
                    .bookCode(bookCode)
                    .saveName(tempFileName)
                    .originName(originFileName)
                    .build();

            bookImages.add(bookImage);
        });

        bookRepository.registerBookImages(bookImages); // 반복이 끝난 후 'bookRepository' 에게 'bookImageDtos' 전달 -> 'xml' 에서 'forEach' 실행됨
    }

    public List<BookImage> getBooks(String bookCode) {
        return bookRepository.findBookImageAll(bookCode);
    }

    public void removeBookImage(int imageId) {
        BookImage bookImage = bookRepository.findBookImageByImageId(imageId);

        if(bookImage == null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "존재하지 않는 이미지 ID 입니다.");

            throw new CustomValidationException(errorMap);
        } // 여기서 'null' 이 아니면 지울 이미지가 존재

        if(bookRepository.deleteBookImage(imageId) > 0) { // 정상적으로 'db' 에서 지웠을 때
            File file = new File(filePath + "book/" + bookImage.getSaveName()); // 내가 방금 지운 파일 경로
            if(file.exists()) { // 존재한다면
                file.delete(); // 해당 파일을 지워라
            }
        }
    }
}
