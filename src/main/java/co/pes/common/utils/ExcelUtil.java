package co.pes.common.utils;

import co.pes.domain.evaluation.model.JobEvaluation;
import co.pes.domain.excel.dto.ExcelDto;
import co.pes.domain.excel.model.CellDataType;
import co.pes.domain.total.model.TotalRanking;
import co.pes.common.exception.BusinessLogicException;
import co.pes.common.exception.ExceptionCode;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.NONE)
public class ExcelUtil {

    public static <T> byte[] excelDownload(HttpServletResponse response, List<T> dataList) {
        ExcelDto<T> excelDto;

        if (dataList.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.NOT_EXISTS_DATA);
        }

        if (dataList.get(0) instanceof JobEvaluation) {
            String[] rowTitle = new String[]{"업무명", "담당팀장", "담당임원", "가중치(%)", "점수",
                "점수", "업무구분", "난이도", "난이도", "기여도", "기여도", "최종점수", "피드백"};
            String[] dataType = new String[]{CellDataType.STRING, CellDataType.STRING, CellDataType.STRING, CellDataType.NUMBER, CellDataType.STRING, CellDataType.STRING,
                CellDataType.STRING, CellDataType.STRING, CellDataType.STRING, CellDataType.STRING, CellDataType.STRING, CellDataType.NUMBER, CellDataType.STRING};
            int[] cellSize = new int[]{200, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 100};
            excelDto = ExcelDto.<T>builder()
                .fileName("evaluation_excel_" + LocalDate.now())
                .rowTitle(rowTitle)
                .dataType(dataType)
                .cellSize(cellSize)
                .dataList(dataList)
                .build();
        } else if (dataList.get(0) instanceof TotalRanking) {
            String[] rowTitle = new String[]{"팀명/부문명", "성명", "직책", "최종점수", "등급", "메모"};
            String[] dataType = new String[]{CellDataType.STRING, CellDataType.STRING, CellDataType.STRING, CellDataType.NUMBER, CellDataType.STRING, CellDataType.STRING};
            int[] cellSize = new int[]{100, 30, 30, 30, 30, 100};
            excelDto =  ExcelDto.<T>builder()
                .fileName("ranking_excel_" + LocalDate.now())
                .rowTitle(rowTitle)
                .dataType(dataType)
                .cellSize(cellSize)
                .dataList(dataList)
                .build();
        } else {
            throw new BusinessLogicException(ExceptionCode.INVALID_DATA_LIST);
        }

        return startDownload(response, excelDto);
    }

    private static <T> byte[] startDownload(HttpServletResponse response, ExcelDto<T> excelDto) {
        // Excel Write
        SXSSFWorkbook workbook = new SXSSFWorkbook(10000);
        byte[] excelContent = new byte[0];
        try {
            // sheet 생성
            SXSSFSheet sheet = workbook.createSheet();

            // title 생성
            Object[] objs = {workbook, sheet};
            objs = createTitle(objs, excelDto, 0);
            workbook = (SXSSFWorkbook) objs[0];
            sheet = (SXSSFSheet) objs[1];

            // contents 생성
            objs = new Object[]{workbook, sheet};
            if (excelDto.getDataList().get(0) instanceof JobEvaluation) {
                objs = createContent(objs, excelDto, 2);
            } else {
                objs = createContent(objs, excelDto, 1);
            }
            workbook = (SXSSFWorkbook) objs[0];

            // ContentType, Header 정보 설정
            String fileName = excelDto.getFileName().replaceAll("[.@$^\\s]", "_");
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Set-Cookie", "fileDownload=true;path=/");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            // 바이트 배열로 변환
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            excelContent = byteArrayOutputStream.toByteArray();

            // Excel File 다운로드
            if (excelDto.getDataList().get(0) instanceof JobEvaluation) {
                ServletOutputStream os = response.getOutputStream();
                os.write(excelContent);
                os.flush();
                os.close();
            }
            workbook.close();
            byteArrayOutputStream.close();
        } catch (Exception e) {
            log.info("ExcelDownload execute exception: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            workbook.dispose();
        }

        return excelContent;
    }

    public static <T> Object[] createTitle(Object[] objs, ExcelDto<T> excelDto, int rowIndex) {
        SXSSFWorkbook workbook = (SXSSFWorkbook) objs[0];
        SXSSFSheet sheet = (SXSSFSheet) objs[1];

        try {
            // Cell Size 설정
            for (int i = 0; i < excelDto.getDataType().length; i++) {
                sheet.setColumnWidth(i, (excelDto.getCellSize()[i] * 100));
            }

            // Font 설정
            Font titleFont = workbook.createFont();
            titleFont.setFontName("Arial");
            titleFont.setFontHeightInPoints((short) 12);
            titleFont.setBold(true);
            titleFont.setColor(IndexedColors.WHITE.getIndex());

            // Style 설정
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setBorderBottom(BorderStyle.THIN);
            titleStyle.setBorderTop(BorderStyle.THIN);
            titleStyle.setBorderLeft(BorderStyle.THIN);
            titleStyle.setBorderRight(BorderStyle.THIN);
            titleStyle.setFont(titleFont);

            // Title Row 생성
            SXSSFRow titleRow = sheet.createRow(rowIndex);
            titleRow.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 2);
            String[] rowTitle = excelDto.getRowTitle();
            for (int i = 0; i < rowTitle.length; i++) {
                // Cell에 Title 매핑
                SXSSFCell cell = titleRow.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellType(CellType.STRING);
                cell.setCellValue(rowTitle[i]);
            }

            // 평가 엑셀은 별도 추가 작업 필요
            if (excelDto.getDataList().get(0) instanceof JobEvaluation) {
                SXSSFRow subTitleRow = sheet.createRow(1);
                subTitleRow.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 2);

                for (int i = 0; i <= 11; i++) {
                    SXSSFCell cell = subTitleRow.createCell(i);
                    cell.setCellStyle(titleStyle);
                }

                // 담당임원
                SXSSFCell pointOfficerCell = subTitleRow.createCell(4);
                SXSSFCell levelOfficerCell = subTitleRow.createCell(7);
                SXSSFCell condOfficerCell = subTitleRow.createCell(9);

                pointOfficerCell.setCellStyle(titleStyle);
                levelOfficerCell.setCellStyle(titleStyle);
                condOfficerCell.setCellStyle(titleStyle);
                pointOfficerCell.setCellType(CellType.STRING);
                levelOfficerCell.setCellType(CellType.STRING);
                condOfficerCell.setCellType(CellType.STRING);
                pointOfficerCell.setCellValue("담당임원");
                levelOfficerCell.setCellValue("담당임원");
                condOfficerCell.setCellValue("담당임원");

                // 대표조정
                SXSSFCell pointCeoCell = subTitleRow.createCell(5);
                SXSSFCell levelCeoCell = subTitleRow.createCell(8);
                SXSSFCell condCeoCell = subTitleRow.createCell(10);

                pointCeoCell.setCellStyle(titleStyle);
                levelCeoCell.setCellStyle(titleStyle);
                condCeoCell.setCellStyle(titleStyle);
                pointCeoCell.setCellType(CellType.STRING);
                levelCeoCell.setCellType(CellType.STRING);
                condCeoCell.setCellType(CellType.STRING);
                pointCeoCell.setCellValue("대표조정");
                levelCeoCell.setCellValue("대표조정");
                condCeoCell.setCellValue("대표조정");

                // Cell merge
                sheet.addMergedRegion(CellRangeAddress.valueOf("A1:A2"));
                sheet.addMergedRegion(CellRangeAddress.valueOf("B1:B2"));
                sheet.addMergedRegion(CellRangeAddress.valueOf("C1:C2"));
                sheet.addMergedRegion(CellRangeAddress.valueOf("D1:D2"));
                sheet.addMergedRegion(CellRangeAddress.valueOf("E1:F1"));
                sheet.addMergedRegion(CellRangeAddress.valueOf("G1:G2"));
                sheet.addMergedRegion(CellRangeAddress.valueOf("H1:I1"));
                sheet.addMergedRegion(CellRangeAddress.valueOf("J1:K1"));
                sheet.addMergedRegion(CellRangeAddress.valueOf("L1:L2"));
                sheet.addMergedRegion(CellRangeAddress.valueOf("M1:M2"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Object[]{workbook, sheet};
    }

    public static <T> Object[] createContent(Object[] objs, ExcelDto<T> excelDto, int rowIndex) {
        SXSSFWorkbook workbook = (SXSSFWorkbook) objs[0];
        SXSSFSheet sheet = (SXSSFSheet) objs[1];

        try {
            workbook.setCompressTempFiles(true);

            // Font 설정
            Font contentFont = workbook.createFont();
            contentFont.setFontName("Arial");
            contentFont.setFontHeightInPoints((short) 10);
            contentFont.setBold(false);

            // 기본 스타일 설정
            CellStyle contentStyle = workbook.createCellStyle();
            contentStyle.setAlignment(HorizontalAlignment.CENTER);
            contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            contentStyle.setFont(contentFont);
            contentStyle.setBorderBottom(BorderStyle.THIN);
            contentStyle.setBorderTop(BorderStyle.THIN);
            contentStyle.setBorderRight(BorderStyle.THIN);
            contentStyle.setBorderLeft(BorderStyle.THIN);
            contentStyle.setWrapText(true);

            // Cell에 내용 매핑
            List<T> dataList = excelDto.getDataList();

            Double sumWeight = (double) 0;
            Double sumTotalPoint = (double) 0;
            boolean withTotalInfo = true;
            String chargeTeam = "";
            if (dataList.get(0) instanceof JobEvaluation) {
                chargeTeam = ((JobEvaluation) dataList.get(0)).getChargeTeam();
            }

            for (int i = 0; i < dataList.size(); i++) {

                // Content Row 생성
                SXSSFRow contentRow = sheet.createRow(i + rowIndex);
                contentRow.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 2);

                // Cell에 Content 매핑
                // 평가 엑셀 다운로드
                if (dataList.get(0) instanceof JobEvaluation) {
                    StringBuilder sb = new StringBuilder();
                    JobEvaluation data = (JobEvaluation) dataList.get(i);

                    sumWeight += data.getWeight();
                    sumTotalPoint += data.getTotalPoint();

                    // total 정보 포함해야하는지 체크
                    if (!chargeTeam.equals(data.getChargeTeam())) {
                        withTotalInfo = false;
                    }

                    // 업무명
                    SXSSFCell cell1 = contentRow.createCell(0);
                    cell1.setCellStyle(contentStyle);
                    cell1.setCellType(CellType.STRING);
                    cell1.setCellValue(sb.append(data.getTaskTitle()).append("\n").append(data.getProjectTitle()).append(" | ").append(data.getTaskState()).toString());

                    // 담당 Manager
                    SXSSFCell cell3 = contentRow.createCell(1);
                    cell3.setCellStyle(contentStyle);
                    cell3.setCellType(CellType.STRING);
                    cell3.setCellValue(data.getChargeTeam());

                    // 담당 Ceo
                    SXSSFCell cell4 = contentRow.createCell(2);
                    cell4.setCellStyle(contentStyle);
                    cell4.setCellType(CellType.STRING);
                    cell4.setCellValue(data.getChargeOfficer());

                    // 가중치
                    SXSSFCell cell5 = contentRow.createCell(3);
                    cell5.setCellStyle(contentStyle);
                    cell5.setCellType(CellType.STRING);
                    cell5.setCellValue(data.getWeight());

                    // 담당임원 - 점수
                    SXSSFCell cell6 = contentRow.createCell(4);
                    cell6.setCellStyle(contentStyle);
                    cell6.setCellType(CellType.NUMERIC);
                    cell6.setCellValue(data.getOfficerPoint());

                    // 대표조정 - 점수
                    SXSSFCell cell7 = contentRow.createCell(5);
                    cell7.setCellStyle(contentStyle);
                    cell7.setCellType(CellType.NUMERIC);
                    cell7.setCellValue(data.getCeoPoint());

                    // 업무 구분
                    SXSSFCell cell8 = contentRow.createCell(6);
                    cell8.setCellStyle(contentStyle);
                    cell8.setCellType(CellType.STRING);
                    cell8.setCellValue(data.getJobGb());
                    
                    // 담당임원 - 난이도
                    SXSSFCell cell9 = contentRow.createCell(7);
                    cell9.setCellStyle(contentStyle);
                    cell9.setCellType(CellType.STRING);
                    cell9.setCellValue(data.getLevelOfficer());

                    // 대표조정 - 난이도
                    SXSSFCell cell10 = contentRow.createCell(8);
                    cell10.setCellStyle(contentStyle);
                    cell10.setCellType(CellType.STRING);
                    cell10.setCellValue(data.getLevelCeo());

                    // 담당임원 - 기여도
                    SXSSFCell cell11 = contentRow.createCell(9);
                    cell11.setCellStyle(contentStyle);
                    cell11.setCellType(CellType.STRING);
                    cell11.setCellValue(data.getCondOfficer());

                    // 대표조정 - 기여도
                    SXSSFCell cell12 = contentRow.createCell(10);
                    cell12.setCellStyle(contentStyle);
                    cell12.setCellType(CellType.STRING);
                    cell12.setCellValue(data.getCondCeo());

                    // 최종 점수
                    SXSSFCell cell13 = contentRow.createCell(11);
                    cell13.setCellStyle(contentStyle);
                    cell13.setCellType(CellType.NUMERIC);
                    cell13.setCellValue(data.getTotalPoint());

                    // 피드백
                    SXSSFCell cell14 = contentRow.createCell(12);
                    cell14.setCellStyle(contentStyle);
                    cell14.setCellType(CellType.STRING);
                    cell14.setCellValue(data.getNote());
                } else if (dataList.get(0) instanceof TotalRanking) {
                    TotalRanking data = (TotalRanking) dataList.get(i);

                    SXSSFCell cell1 = contentRow.createCell(0);
                    cell1.setCellStyle(contentStyle);
                    cell1.setCellType(CellType.STRING);
                    cell1.setCellValue(data.getTeamTitle());

                    SXSSFCell cell2 = contentRow.createCell(1);
                    cell2.setCellStyle(contentStyle);
                    cell2.setCellType(CellType.STRING);
                    cell2.setCellValue(data.getName());

                    SXSSFCell cell3 = contentRow.createCell(2);
                    cell3.setCellStyle(contentStyle);
                    cell3.setCellType(CellType.STRING);
                    cell3.setCellValue(data.getPosition());

                    SXSSFCell cell4 = contentRow.createCell(3);
                    cell4.setCellStyle(contentStyle);
                    cell4.setCellType(CellType.NUMERIC);
                    cell4.setCellValue(data.getTotalPoint());

                    SXSSFCell cell5 = contentRow.createCell(4);
                    cell5.setCellStyle(contentStyle);
                    cell5.setCellType(CellType.STRING);
                    cell5.setCellValue(data.getRanking());

                    SXSSFCell cell6 = contentRow.createCell(5);
                    cell6.setCellStyle(contentStyle);
                    cell6.setCellType(CellType.STRING);
                    cell6.setCellValue(data.getNote());
                }

                if (dataList.get(0) instanceof JobEvaluation) {
                    SXSSFRow totalRow = sheet.createRow(dataList.size() + 2);
                    totalRow.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * 2);

                    CellStyle totalStyle = workbook.createCellStyle();
                    totalStyle.setAlignment(HorizontalAlignment.CENTER);
                    totalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    totalStyle.setFont(contentFont);
                    totalStyle.setBorderBottom(BorderStyle.THIN);
                    totalStyle.setBorderTop(BorderStyle.THIN);
                    totalStyle.setBorderRight(BorderStyle.THIN);
                    totalStyle.setBorderLeft(BorderStyle.THIN);
                    totalStyle.setWrapText(true);
                    totalStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
                    totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                    if (withTotalInfo) {
                        for (int j = 0; j <= 12; j++) {
                            SXSSFCell cell = totalRow.createCell(j);
                            cell.setCellStyle(totalStyle);
                            cell.setCellType(CellType.STRING);
                            cell.setCellValue("");

                            if (j == 0) {
                                cell.setCellValue("합계");
                            } else if (j == 3) {
                                cell.setCellValue(sumWeight + "%");
                            } else if (j == 11) {
                                cell.setCellType(CellType.NUMERIC);
                                cell.setCellValue(sumTotalPoint);
                            }
                        }
                    }
                }

                if (i % 10000 == 0) {
                    sheet.flushRows(10000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Object[]{workbook, sheet};
    }
}
