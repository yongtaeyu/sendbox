package com.example.sandbox.controller;

import com.example.sandbox.excel.SampleExcelDto;
import com.example.sandbox.jwt.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody SignInDto signInDto) {
        String username = signInDto.getUsername();
        String password = signInDto.getPassword();
        JwtToken jwtToken = memberService.signIn(username, password);
        log.info("request username = {}, password = {}", username, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }
    @PostMapping("/test")
    public String test() {
        return "success";
    }

    @PostMapping("/hello")
    public String test2() {
        return "success";
    }

    @PostMapping(value="/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void testExcel(MultipartFile file){

        List<SampleExcelDto> list = memberService.excelUpload(file);
        for(SampleExcelDto s:list){
            System.out.println(s.getName());
        }
    }
    @PostMapping(value="/excelDownload")
    public void testExcelDownload() throws IOException {
        memberService.excelDownload();
    }
}
