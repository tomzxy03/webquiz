package com.tomzxy.web_quiz.services;


import com.tomzxy.web_quiz.dto.responses.PageResDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConvertToPageResDTO {
    private final ModelMapper modelMapper;

    public <T, R> PageResDTO<List<R>> convertToPageResponse (Page<T> page, int pageRequest, int size, Class<R> classes){
        List<R> list = page.stream()
                .map(entity -> modelMapper.map(entity, classes))
                .toList();
        return PageResDTO.<List<R>> builder()
                .page(pageRequest)
                .size(size)
                .total_page(page.getTotalPages())
                .items(list)
                .build();
    }

}
