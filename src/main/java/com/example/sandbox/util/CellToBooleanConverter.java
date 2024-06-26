package com.example.sandbox.util;


import org.apache.poi.ss.usermodel.Cell;
import org.springframework.core.convert.converter.Converter;

public class CellToBooleanConverter implements Converter<Cell, Boolean> {
    @Override
    public Boolean convert(Cell source) {

        int cellType = source.getCellType() == Cell.CELL_TYPE_FORMULA ?
                source.getCachedFormulaResultType(): source.getCellType();

        if (cellType == Cell.CELL_TYPE_BOOLEAN) {
            return source.getBooleanCellValue();
        }
        else if (cellType == Cell.CELL_TYPE_STRING) {
            return Boolean.valueOf(source.getStringCellValue());
        }
        else {
            throw new IllegalArgumentException(
                    String.format("cell-to-boolean converter does not support cell type : %s", cellType)
            );
        }
    }
}
