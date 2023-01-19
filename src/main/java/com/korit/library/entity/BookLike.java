package com.korit.library.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookLike {
    @ApiModelProperty(value = "좋아요 번호", example = "1")
    private int likeId;

    @ApiModelProperty(value = "도서 ID", example = "1")
    private int bookId;

    @ApiModelProperty(value = "사용자 ID", example = "1")
    private int userId;
}
