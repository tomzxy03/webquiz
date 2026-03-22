package com.tomzxy.web_quiz.services.common;


import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ConvertToPageResDTO {
    private final ModelMapper modelMapper;


    //use modelMapper
    public <T, R> PageResDTO<R> convertToPageResponse (Page<T> page, int pageRequest, int size, Class<R> classes){
        List<R> list = page.stream()
                .map(entity -> modelMapper.map(entity, classes))
                .toList();
        return PageResDTO.<R> builder()
                .page(pageRequest)
                .size(size)
                .total_page(page.getTotalPages())
                .items(list)
                .build();
    }

    // user mapstruct
    public <T, R> PageResDTO<R> convertPageResponse(Page<T> entities, Pageable pageable, Function<T, R> mapper) {
        List<R> dtoList = entities.stream()
                .map(mapper)
                .toList();
            
        return PageResDTO.<R>builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .total_page(entities.getTotalPages())
                .items(dtoList)
                .build();
    }


}
