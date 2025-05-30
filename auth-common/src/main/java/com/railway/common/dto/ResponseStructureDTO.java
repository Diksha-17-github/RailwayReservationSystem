package com.railway.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ResponseStructureDTO<T> {
    private LocalDateTime timestamp; //when response is created
    private String message; //status message
    private T data; //can work with any data type

    public ResponseStructureDTO(LocalDateTime timestamp, String message, T data) {
        this.timestamp = timestamp;
        this.message = message;
        this.data = data;
    }

}