package com.example.sandbox.excel;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Row;

//V1
// 초기 버전 코드 살펴보기
@Getter
@Setter
public class SampleExcelDto implements ExcelObject{
    @ExcelColumn(headerName = "순번") private Integer order;
    @ExcelColumn(headerName = "이름") private String name;
    @ExcelColumn(headerName = "나이") private Integer age;
    @ExcelColumn(headerName = "성별") private Gender gender;

    @Override
    public void fillUpFromRow(Row row) {
        this.order  = (int)row.getCell(0).getNumericCellValue();
        this.name   = row.getCell(1).getStringCellValue();
        this.age    = (int)row.getCell(2).getNumericCellValue();
        if(row.getCell(3).getStringCellValue().equals(Gender.M.name()))
            this.gender = Gender.M;
        else
            this.gender = Gender.F;
    }
}

