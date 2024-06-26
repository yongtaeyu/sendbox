package com.example.sandbox.excel;

import com.example.sandbox.util.ConverterUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;



@Component
public class ExcelUtil {

    @Autowired
    private ConverterUtil converterUtil;

    int headerStartRowToParse = 0;
    int headerStartRowToRender = 0;

    int startColToRender = 0;
    int bodyStartRowToRender = 1;

    // 엑셀 업로드를 위한 퍼블릭 메서드
    public <T> List<T> parseExcelToObject(int headerStartRowToParse, MultipartFile file, Class<T> clazz) {

        this.headerStartRowToParse = headerStartRowToParse;
        Sheet sheet = null;
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            sheet = workbook.getSheetAt(0);
            parseHeader(sheet, clazz);
        }catch (Exception e){
            e.printStackTrace();
        }
        return parseBody(sheet, clazz);
    }

    // 2. 인덱스와 자료형 추론을 통한 자동매핑
    public <T> List<T> parseExcelToAutoMappingObject(int headerStartRowToParse, MultipartFile file, Class<T> clazz){
        Sheet sheet = null;
        Map<String, Integer> headers = null;
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            sheet = workbook.getSheetAt(0);
            headers = parseHeader2(sheet, clazz); // 헤더 파싱
        }catch (Exception e){
            e.printStackTrace();
        }
        return parseBody2(headers, sheet, clazz);
    }
    private <T> Map<String, Integer> parseHeader2(Sheet sheet, Class<T> clazz){
        Map<String, Integer> excelHeaders = new HashMap<>();
        sheet.getRow(headerStartRowToParse)
        .cellIterator()
        .forEachRemaining(
            e-> excelHeaders.put(e.getStringCellValue(), e.getColumnIndex())
        );
        return excelHeaders;
    }

    private <T> List<T> parseBody2(Map<String, Integer> headers, Sheet sheet, Class<T> clazz){
        List<T> objects = new ArrayList<>();

        int bodyStartRowToParse = 1;

        try {
            Field[] fields = clazz.getDeclaredFields();
            clazz.getDeclaredConstructor().setAccessible(true);

            /* 엑셀의 줄마다 매핑시킬 객체를 만들고 */
            for (int i = bodyStartRowToParse; i <= sheet.getLastRowNum(); i++) {
                T object = clazz.getDeclaredConstructor().newInstance();

                /* 객체의 필드를 순회하며 값을 넣음 */
                for (Field field : fields) {
                    field.setAccessible(true);

                    /* @ExcelColumn에 기입된 헤더명을 Key로 사용하면 */
                    String key = field.getAnnotation(ExcelColumn.class).headerName().equals("") ?
                            field.getName() : field.getAnnotation(ExcelColumn.class).headerName();

                    /* Value에 해당하는 엑셀 인덱스의 값을 가져올 수 있음 */
                    Cell cell = sheet.getRow(i).getCell(headers.get(key));

                    /* Converter를 활용한 자동 매핑 로직 (아래 자료형 추론 단락 참고!) */
                    field.set(object, converterUtil.convert(cell, field.getType()));
                }
                /* 완성된 객체를 리스트에 쌓기 */
                objects.add(converterUtil.convert(sheet.getRow(i), clazz));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return objects;
    }



    /*
    엑셀 다운로드를 위한 퍼블릭 메서드이다. 임의의 객체 리스트를 엑셀 파일로 변환해 OutputStream에 쓴다.
    업로드 API와 마찬가지로 헤더와 바디 렌더링은 하위 프라이빗 메서드에 맡겼다.
    Controller에서 HttpServletResponse의 OutputStream을 이 API의 파라미터에 넘기면 프론트에서 파일을 받아볼 수 있다.
     */
    public <T> void  renderObjectToExcel(
        int startColToRender
        ,int bodyStartRowToRender
        ,OutputStream stream, List<T> data, Class<T> clazz) {

        Workbook workbook = null;
        this.startColToRender = startColToRender;
        this.bodyStartRowToRender = bodyStartRowToRender;

        try {
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            renderHeader(sheet, clazz);
            renderBody(sheet, data, clazz);
            //write this workbook to an Outputstream.
            workbook.write(stream);
            stream.flush();
            stream.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private <T> void parseHeader(Sheet sheet, Class<T> clazz) {
        Set<String> excelHeaders = new HashSet<>();
        Set<String> classHeaders = new HashSet<>();

        sheet.getRow(headerStartRowToParse).cellIterator()
                .forEachRemaining(e->excelHeaders.add(e.getStringCellValue()));

        Arrays.stream(clazz.getDeclaredFields())
                .filter(e->e.isAnnotationPresent(ExcelColumn.class))
                .forEach(e->{
                    if (e.getAnnotation(ExcelColumn.class).headerName().equals(""))
                        classHeaders.add(e.getName());
                    else
                        classHeaders.add(e.getAnnotation(ExcelColumn.class).headerName());
                });

        // 헤더와 맞지 않으면 에러..
        if (!excelHeaders.containsAll(classHeaders)) {
            throw new BusinessException(
                String.format("Excel file headers are not compatible with given class %s", clazz.getName())
            );
        }
    }
    private <T> List<T> parseBody(Sheet sheet, Class<T> clazz) {
        List<T> objects = null;
        try {
            objects = new ArrayList<>();
            clazz.getDeclaredConstructor().setAccessible(true);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                T object = clazz.getDeclaredConstructor().newInstance();
                clazz.getMethod("fillUpFromRow", Row.class).invoke(object, sheet.getRow(i));
                objects.add(object);
            }
        }catch (Exception ex){
            System.out.println("Exception ::" + ex.getMessage());
        }
        return objects;
    }

    // 다운로드
    private <T> void renderHeader(Sheet sheet, Class<T> clazz) {
        Row row = sheet.createRow(headerStartRowToRender);
        int colIdx = startColToRender;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelColumn.class)) {
                String headerName = field.getAnnotation(ExcelColumn.class).headerName();
                row.createCell(colIdx, Cell.CELL_TYPE_STRING).setCellValue(
                        headerName.isEmpty() ? field.getName() : headerName
                );
                colIdx++;
            }
        }
        if (colIdx == startColToRender) {
            throw new BusinessException(
                    String.format("Class %s has no @ExcelColumn", clazz.getName())
            );
        }
    }
    // 다운로드
    private <T> void renderBody(Sheet sheet, List<T> data, Class<T> clazz) throws IllegalAccessException {
        int rowIdx = bodyStartRowToRender;

        for (T datum : data) {
            Row row = sheet.createRow(rowIdx);
            int colIdx = startColToRender;
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                row.createCell(colIdx, Cell.CELL_TYPE_STRING).setCellValue(String.valueOf(field.get(datum)));
                colIdx++;
            }
            rowIdx++;
        }
    }
}
