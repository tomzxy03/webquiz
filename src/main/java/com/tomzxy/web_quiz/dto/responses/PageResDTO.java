package com.tomzxy.web_quiz.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResDTO <T>{
    int page;
    int size;
    int total_page;
    List<T> items;
}
