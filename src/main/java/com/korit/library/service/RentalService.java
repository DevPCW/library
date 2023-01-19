package com.korit.library.service;

import com.korit.library.entity.RentalDtl;
import com.korit.library.entity.RentalMst;
import com.korit.library.exception.CustomRentalException;
import com.korit.library.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    public void rentalOne(int userId, int bookId) {
        availability(userId);
        availabilityLoan(bookId);

        RentalMst rentalMst = RentalMst.builder()
                .userId(userId)
                .build();

        rentalRepository.saveRentalMst(rentalMst);
        // 'save' 가 되고 나서 'autoincrement' 된 거[rentalMst.getRentalId()]를 밑에 'rentalId' 에 넣어줘야함.
        // 그래서 이 코드가 밑에 빌드패턴보다 위에 있어야 함.

        List<RentalDtl> rentalDtlList = new ArrayList<>(); // RentalDtl 객체를 담는 리스트

        rentalDtlList.add(RentalDtl.builder()
                .rentalId(rentalMst.getRentalId())
                .bookId(bookId)
                .build());

//        이렇게 해도됨
//        RentalDtl rentalDtl = RentalDtl.builder()
//                .rentalId(rentalMst.getRentalId())
//                .bookId(bookId)
//                .build();
//        rentalDtlList.add(rentalDtl);

        rentalRepository.saveRentalDtl(rentalDtlList);
    }

    public void returnBook(int bookId) {
        notAvailabilityLoan(bookId);
        rentalRepository.updateReturnDate(bookId);
    }


    private void availability(int userId) {
        int rentalCount = rentalRepository.rentalAvailability(userId); // rental 횟수
        if(rentalCount > 2) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("rentalCountError", "대여 가능 횟수를 초과하였습니다.");

            throw new CustomRentalException(errorMap);
        }
    }

    private void availabilityLoan(int bookId) {
        int loanCount = rentalRepository.loanRental(bookId);
        if(loanCount > 0) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("loanError", "현재 대여 중인 도서입니다.");

            throw new CustomRentalException(errorMap);
        }
    }

    private void notAvailabilityLoan(int bookId) {
        int loanCount = rentalRepository.loanRental(bookId);
        if(loanCount < 1) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("loanError", "대여 중인 도서가 아닙니다.");

            throw new CustomRentalException(errorMap);
        }
    }
}
