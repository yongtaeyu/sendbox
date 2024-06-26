package com.example.sandbox.controller;

import com.example.sandbox.excel.ExcelUtil;
import com.example.sandbox.excel.Gender;
import com.example.sandbox.excel.SampleExcelDto;
import com.example.sandbox.jwt.JwtToken;
import com.example.sandbox.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {



    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final ExcelUtil excelUtil;

    private final JwtTokenProvider jwtTokenProvider;

    public JwtToken signIn(String username, String password) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        return jwtTokenProvider.generateToken(authentication);
    }
    // 파일 파싱
    public List<SampleExcelDto> excelUpload(MultipartFile file) {

        // 1 단계
        //return excelUtil.parseExcelToObject(0, file, SampleExcelDto.class);

        // 2 단계
        return excelUtil.parseExcelToAutoMappingObject(0, file, SampleExcelDto.class);
    }

    // 파일 다운로드
    public void excelDownload() throws FileNotFoundException {
        List<SampleExcelDto> list = new ArrayList<>();
        SampleExcelDto sampleExcelDto = new SampleExcelDto();
        sampleExcelDto.setOrder(0);
        sampleExcelDto.setName("맹구");
        sampleExcelDto.setAge(11);
        sampleExcelDto.setGender(Gender.F);
        list.add(sampleExcelDto);
        excelUtil.renderObjectToExcel( 0,1, new FileOutputStream("download.xlsx"), list, SampleExcelDto.class);
    }

}
