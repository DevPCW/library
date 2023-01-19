package com.korit.library.web.api;

import com.korit.library.security.PrincipalDetails;
import com.korit.library.service.RentalService;
import com.korit.library.web.dto.CMRespDto;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/*
    /rental/{bookId}
    대여 요청 -> 대여 요청 날린 사용자의 대여가능 여부확인
    -> 가능함(대여 가능 횟수 3권 미만일 때) -> 대여 정보 추가 rental_mst(대여 코드), rental_dtl
    -> 불가능함(대여 가능 횟수 0이면) -> 예외처리
 */
@Api(tags = {"도서 대여 API"})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RentalApi {

    // @RequiredArgsConstructor 달고 'final' 로 잡으면 @Autowired 된거임
    private final RentalService rentalService;


    // public RentalApi(RentalService rentalService) {
    //        this.rentalService = rentalService;
    //    } // 얘를 만들어주는게 @RequiredArgsConstructor 임


    @PostMapping("/rental/{bookId}") // 'rental' 하는 것이기 때문에 POST
    public ResponseEntity<CMRespDto<?>> rental(@PathVariable int bookId,
                                               @AuthenticationPrincipal PrincipalDetails principalDetails) {
        rentalService.rentalOne(principalDetails.getUser().getUserId(), bookId);
        return ResponseEntity
                .ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Successfully", null));
    }

    @PutMapping("/rental/{bookId}")
    public ResponseEntity<CMRespDto<?>> rentalReturn(@PathVariable int bookId) {
    rentalService.returnBook(bookId);

        return ResponseEntity
                .ok()
                .body(new CMRespDto<>(HttpStatus.OK.value(), "Successfully", null));
    }


}
