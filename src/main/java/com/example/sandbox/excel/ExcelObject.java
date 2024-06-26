package com.example.sandbox.excel;

import org.apache.poi.ss.usermodel.Row;

public interface ExcelObject {
    void fillUpFromRow(Row row);
}
