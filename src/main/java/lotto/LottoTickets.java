package lotto;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LottoTickets {

    private static int size;
    private static List<Lotto> lottoTickets = new ArrayList<>();
    private static List<LottoResult> lottoLottoResults = new ArrayList<>();
    private static List<Integer> lottoMatches = new ArrayList<>();

    public LottoTickets(List<Lotto> lottoTickets) {
        this.size = lottoTickets.size();
        this.lottoTickets = lottoTickets;
    }

    public void setLottoResults(List<LottoResult> lottoLottoResults) {
        this.lottoLottoResults = lottoLottoResults;
    }

    public Lotto getLotto(int index) {
        return lottoTickets.get(index);
    }

    public int getLottoTicketsSize() {
        return lottoTickets.size();
    }

    public static List<Lotto> getLottoTickets() {
        return lottoTickets;
    }

    public LottoResult getLottoResult(int resultIndex) {
        LottoResult lottoResult = lottoLottoResults.get(resultIndex);
        return lottoResult;
    }

    public double getTotalReturn() {

        double purchaseAmount = (double) getPurchaseAmount();
        double winningAmounts = (double) getWinningAmounts();

        double totalReturn = (winningAmounts / purchaseAmount) * ReferenceValue.MULTIPLE_HUNDRED;
        double totalReturnPercent = Math.round(totalReturn * ReferenceValue.MULTIPLE_FIRST_DECIMAL) / ReferenceValue.DIVIDE_FIRST_DECIMAL;

        return totalReturnPercent;
    }

    private static double getPurchaseAmount() {
        return size * ReferenceValue.Lotto.PRICE.getValue();
    }

    public static int getWinningAmounts() {
        int sum = 0;
        int size = ReferenceValue.Lotto.SIZE.getValue();

        for (int index = 0; index < size; index++) {
            int count = lottoMatches.get(index);
            sum += getMatchesMoney(index, count);
        }

        return sum;
    }

    private static int getMatchesMoney(int index, int count) {

        int money = 0;

        for (ReferenceValue.WinningStats winningStats : ReferenceValue.WinningStats.values()) {
            if (winningStats.equalsIndex(index)) {
                money = (winningStats.getMoney() * count);
                break;
            }
        }

        return money;
    }

    public static List<Integer> getMatches() {
        return lottoMatches;
    }

    public void setMatches() {

        Map<Integer, Integer> matchesDictionary = inItMatchesDictionary();
        List<Integer> matches;

        for (int resultIndex = 0; resultIndex < size; resultIndex++) {
            LottoResult lottoResult = lottoLottoResults.get(resultIndex);
            int matchesIndex = getMatchesIndex(lottoResult);
            int matchesCount = matchesDictionary.get(matchesIndex);
            matchesDictionary.replace(matchesIndex, matchesCount + 1);
        }

        matches = new ArrayList<>(matchesDictionary.values());
        lottoMatches = matches;
    }

    private static HashMap<Integer, Integer> inItMatchesDictionary() {

        HashMap<Integer, Integer> matchesDictionary = new HashMap<>();

        for (ReferenceValue.WinningStats winningStats : ReferenceValue.WinningStats.values()) {
            matchesDictionary.put(winningStats.getIndex(), ReferenceValue.NOTHING);
        }

        return matchesDictionary;
    }

    private static int getMatchesIndex(LottoResult lottoResult) {
        int size = lottoResult.size();

        if (size < ReferenceValue.WinningStats.THREE_MATCHES.getCount()) {
            size = ReferenceValue.NOTHING;
        }

        int matchesIndex = getSameCountIndex(size);
        boolean checkFive = checkCountFive(matchesIndex);

        if (checkFive) {
            matchesIndex = checkBonusIndex(lottoResult);
        }

        return matchesIndex;
    }

    private static int getSameCountIndex(int size) {

        int index = 0;
        for (ReferenceValue.WinningStats winningStats : ReferenceValue.WinningStats.values()) {
            if (winningStats.equalsCount(size)) {
                index = winningStats.getIndex();
                break;
            }
        }
        return index;
    }
    public static boolean checkCountFive(int index) {
        return ReferenceValue.WinningStats
                .FIVE_MATCHES
                .equalsIndex(index);
    }

    public static int checkBonusIndex(LottoResult lottoResult) {
        int matchesIndex = ReferenceValue
                .WinningStats
                .FIVE_MATCHES
                .getIndex();

        boolean isBonus = lottoResult.getIsBonus();

        if (isBonus) {
            matchesIndex = ReferenceValue
                    .WinningStats
                    .FIVE_BONUS_MATCHES
                    .getIndex();
        }

        return matchesIndex;
    }
}
