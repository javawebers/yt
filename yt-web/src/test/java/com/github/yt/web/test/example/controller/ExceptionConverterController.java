package com.github.yt.web.test.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异常转换
 *
 * @author sheng
 * @version 1.0
 * @date 2020-11-27 10:02
 */
@RestController
@RequestMapping("exceptionConverter")
public class ExceptionConverterController {


    @GetMapping("unsupportedOperationExceptionNoMessage")
    public void unsupportedOperationExceptionNoMessage() {
        throw new UnsupportedOperationException();
    }

}
