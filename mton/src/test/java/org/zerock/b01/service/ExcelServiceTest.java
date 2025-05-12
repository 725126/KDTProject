package org.zerock.b01.service;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SpringBootTest
@Log4j2
public class ExcelServiceTest {
    // 이쪽 테스트 코드를 활용해서 DTO 또는 ENTITY 클래스를 생성한 다음 넘겨주는 것으로 간단하게 구현 가능
    // 현재로서는 Cell 을 읽어 그 타입에 따라 터미널에 출력하는 역할만을 하고 있다.
    // 우선은 범용적으로 '읽어 주는' 코드가 있었으면 함. 범용 포맷으로 읽은 다음 파싱해서 각각에 넘겨주면 되니까.
    @Test
    public void readExcel() {
        try {
            FileInputStream file = new FileInputStream("./test.xlsx");
            IOUtils.setByteArrayMaxOverride(Integer.MAX_VALUE);

            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                int cellCount = row.getLastCellNum();

                for (int i = 0; i < cellCount; i++) {
                    Cell cell = row.getCell(i);

                    if (cell != null) {
                        // 이 부분은 현재 출력만을 담당하고 있지만, 값을 대입하도록 바꿀 수도 있다.
                        // 범용적으로 사용하도록 하기 위해서는 문자열 등으로 변환시켜야 한다.
                        switch (cell.getCellType()) {
                            case NUMERIC:
                                log.info("numeric: " + cell.getNumericCellValue());
                                break;
                            case STRING:
                                log.info("string: " + cell.getStringCellValue());
                                break;
                            case BLANK:
                                log.info("\"\"");
                                break;
                            default:
                                break;
                        }
                    } else {
                        log.info("null");
                    }
                }
                System.out.println();
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void uploadExcel() {
        try {
            Map<String, String> testMap = new HashMap<String, String>();
            FileInputStream file = new FileInputStream("./test.xlsx");
            IOUtils.setByteArrayMaxOverride(Integer.MAX_VALUE);

            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
