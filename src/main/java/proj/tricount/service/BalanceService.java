package proj.tricount.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import proj.tricount.domain.balance.BalanceDTO;
import proj.tricount.domain.settlement.Expense;
import proj.tricount.domain.user.Member;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final JdbcTemplate jdbcTemplate;

    private final ExpenseService expenseService;
    private final MemberService memberService;

    // 정산 결과 계산
    public List<BalanceDTO> calculateBalances(Long settlementId) {

        // 지출 정보 가져오기
        List<Expense> expenses = expenseService.getExpensesBySettlementId(settlementId);
        if (expenses.isEmpty()) {
            return new ArrayList<>();
        }

        // 사용자별 지출 합계와 전체 지출 합계 계산
        Map<Long, BigDecimal> userExpenses = new HashMap<>();
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Expense expense : expenses) {
            userExpenses.merge(expense.getPayerId(), expense.getAmount(), BigDecimal::add);
            totalExpenses = totalExpenses.add(expense.getAmount());
        }

        // 평균 지출액 계산
        if (totalExpenses.compareTo(BigDecimal.ZERO) == 0) {
            return new ArrayList<>();
        }

        int numberOfUsers = userExpenses.size();
        BigDecimal averageExpense = totalExpenses.divide(BigDecimal.valueOf(numberOfUsers), 2, RoundingMode.HALF_UP);

        // 사용자 차이 계산 및 분류
        List<BalanceDTO> balances = new ArrayList<>();
        TreeMap<Long, BigDecimal> payers = new TreeMap<>();
        TreeMap<Long, BigDecimal> receivers = new TreeMap<>();

        userExpenses.forEach((userId, amountSpent) -> {
            BigDecimal difference = amountSpent.subtract(averageExpense);
            if (difference.compareTo(BigDecimal.ZERO) > 0) {
                receivers.put(userId, difference);
            } else if (difference.compareTo(BigDecimal.ZERO) < 0) {
                payers.put(userId, difference.negate());
            }
        });

        // 잔액 계산 및 매핑
        Iterator<Map.Entry<Long, BigDecimal>> payerIter = payers.entrySet().iterator();
        Iterator<Map.Entry<Long, BigDecimal>> receiverIter = receivers.entrySet().iterator();

        Map.Entry<Long, BigDecimal> payer = null;
        Map.Entry<Long, BigDecimal> receiver = null;

        while (payerIter.hasNext() && receiverIter.hasNext()) {
            if (payer == null) {
                payer = payerIter.next();
            }
            if (receiver == null) {
                receiver = receiverIter.next();
            }

            BigDecimal minTransfer = payer.getValue().min(receiver.getValue());
            Member payerMember = memberService.getMemberById(payer.getKey()).orElse(null);
            Member receiverMember = memberService.getMemberById(receiver.getKey()).orElse(null);

            if (payerMember != null && receiverMember != null) {
                balances.add(new BalanceDTO(payer.getKey(), payerMember.getUsername(), receiver.getKey(), receiverMember.getUsername(), minTransfer));
            }

            payer.setValue(payer.getValue().subtract(minTransfer));
            receiver.setValue(receiver.getValue().subtract(minTransfer));

            if (payer.getValue().compareTo(BigDecimal.ZERO) == 0) {
                payer = null;
            }
            if (receiver.getValue().compareTo(BigDecimal.ZERO) == 0) {
                receiver = null;
            }
        }

        return balances;
    }

    private List<Expense> getExpensesBySettlementId(Long settlementId) {
        String sql = "SELECT * FROM expense WHERE settlement_Id = ?";
        return jdbcTemplate.query(sql, new Object[]{settlementId}, (rs, rowNum) -> new Expense(
                rs.getLong("expense_Id"),
                rs.getLong("settlement_Id"),
                rs.getString("name"),
                rs.getLong("payer_Id"),
                rs.getBigDecimal("amount"),
                rs.getString("date")
        ));
    }
}
