package com.example.sandbox.util;

import com.example.sandbox.excel.SampleExcelDto;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.convert.converter.Converter;

public class SampleExcelDtoConverter implements Converter<Row, SampleExcelDto> {
    /*
        뭔가 변환을 해줘야 하는게 ...
     */
    @Override
    public SampleExcelDto convert(Row source) {
        source.iterator().forEachRemaining(
            System.out::println
        );
        SampleExcelDto sampleExcelDto = new SampleExcelDto();
        sampleExcelDto.fillUpFromRow(source);
        return sampleExcelDto;
    }
}
