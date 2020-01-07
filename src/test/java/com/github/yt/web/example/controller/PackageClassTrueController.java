package com.github.yt.web.example.controller;

import com.github.yt.web.result.PackageResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PackageResponseBody
@RequestMapping("packageClassTrue")
public class PackageClassTrueController {

    @GetMapping("methodDefault")
    public void methodDefault() {

    }

    @GetMapping("methodTrue")
    @PackageResponseBody
    public void methodTrue() {

    }

    @GetMapping("methodFalse")
    @PackageResponseBody(false)
    public void methodFalse() {

    }
}
