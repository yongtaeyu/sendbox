package com.example.sandbox.util;

import com.example.sandbox.excel.BusinessException;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.core.convert.converter.Converter;

public class CellToIntegerConverter implements Converter<Cell, Integer> {
    @Override
    public Integer convert(Cell source) {
        if(source.getCellType() == Cell.CELL_TYPE_NUMERIC){
            return (int)source.getNumericCellValue();
        }else{
            throw new BusinessException("ERROR IN CONVERTER");
        }
    }
}
