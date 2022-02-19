package uz.coder24.apelsintt.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Result {

    private boolean success;
    private String message;
    private Object data;

}
