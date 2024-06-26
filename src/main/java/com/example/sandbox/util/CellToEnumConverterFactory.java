package com.example.sandbox.util;

import com.example.sandbox.excel.Gender;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CellToEnumConverterFactory implements ConverterFactory<Cell, Enum> {


    @Override
    public <T extends Enum> Converter<Cell, T> getConverter(Class<T> targetType) {
        return new CellToEnum(targetType);
    }

    class CellToEnum<T extends Enum> implements Converter<Cell, T> {
        private final Class<T> enumType;

        CellToEnum(Class<T> enumType) {
            this.enumType = enumType;
        }
        @Override
        public T convert(Cell source) {
            if(source.getStringCellValue().equals("M"))
                return (T) Gender.M;
            else
                return (T) Gender.F;
        }
    }
}