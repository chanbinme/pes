package co.pes.common.pagination;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Paging {

    // 현재 페이지 번호
    private int pageNum;

    // 한 페이지에 노출할 레코드 개수
    private int pageSize;

    // 총 레코드 개수
    private int totalRecordCount;

    // 총 페이지 개수
    private int totalPageCount;

    // SQL 조건에 이용되는 시작 ROW
    private int startNum;

    // SQL 조건에 이용되는 마지막 ROW
    private int endNum;

    @Builder
    public Paging(int pageNum, int pageSize, int totalRecordSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalRecordCount = totalRecordSize;
        this.totalPageCount = totalRecordSize % pageSize == 0 ? totalRecordSize / pageSize : totalRecordSize / pageSize + 1;
        this.startNum = ((pageNum - 1) * pageSize) + 1;
        this.endNum = startNum + pageSize - 1;
    }
}
