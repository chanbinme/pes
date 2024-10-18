package co.pes.domain.total.model;

import java.util.Comparator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TotalRanking implements Comparable<TotalRanking> {

    private Long evaluationTotalId;
    private String year;
    private Long teamId;        // 팀 ID
    private String teamTitle;
    private String divisionTitle;   // 부서명
    private String position;
    private String name;
    private double totalPoint;
    private String ranking;     // DB에 저장되어 있던 Rank
    private String newRanking;      // 새롭게 계산된 Rank
    private String note;

    public void updateNewRanking(String ranking) {
        this.newRanking = ranking;
    }

    @Override
    public int compareTo(TotalRanking totalRanking) {
        if (totalRanking.totalPoint < this.totalPoint) {
            return 1;
        } else if (totalRanking.totalPoint > this.totalPoint) {
            return -1;
        }
        return 0;
    }

    // preview용 정렬
    public static Comparator<TotalRanking> totalRankingComparator = Comparator
        .comparing((TotalRanking totalRanking) -> getIndexInArray(totalRanking.getRanking()))
        .thenComparing(TotalRanking::getTotalPoint, Comparator.reverseOrder());
    private static int getIndexInArray(String ranking) {
        String[] rankOrder = {"S", "A", "B", "C", "D", "-"};

        for (int i = 0; i < rankOrder.length; i++) {
            if (rankOrder[i].equals(ranking)) {
                return i;
            }
        }
        return -1;
    }
}
