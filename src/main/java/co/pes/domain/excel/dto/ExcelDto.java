package co.pes.domain.excel.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.poi.ss.formula.functions.T;

@Getter
@Builder
public class ExcelDto<T> {

    private String[] rowTitle;
    private int[] cellSize;
    private List<T> dataList;
    private String[] dataType;
    private String fileName;

}
