package com.example.sandbox.util;

import com.example.sandbox.excel.BusinessException;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.core.convert.converter.Converter;

public class CellToStringConverter implements Converter<Cell, String>{
    @Override
    public String convert(Cell source) {
        if(source.getCellType() == Cell.CELL_TYPE_STRING){
            return source.getStringCellValue();
        }else{
            throw new BusinessException("ERROR IN CONVERTER");
        }
    }
}
