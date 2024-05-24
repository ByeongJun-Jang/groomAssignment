package proj.tricount.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import proj.tricount.domain.settlement.Expense;
import proj.tricount.domain.user.Member;
import proj.tricount.service.ExpenseService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    // 지출 추가
    @PostMapping("/expense")
    @ResponseBody
    public ResponseEntity<Object> addExpense(@ModelAttribute Expense expense, HttpSession httpSession) {
        Member member = (Member) httpSession.getAttribute("member");
        if (member != null){
            try {
                expenseService.addExpense(expense);
                log.info("지출 추가 완료");
                return ResponseEntity.ok(expense);
            } catch (DataAccessException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 정산에 관한 지출 조회
    @GetMapping("/expenses")
    @ResponseBody
    public List<Expense> getExpensesBySettlementId(@RequestParam Long settlementId) {
        return expenseService.getExpensesBySettlementId(settlementId);
    }

    // 지출 삭제
    @DeleteMapping("/delete_expense")
    @ResponseBody
    public void deleteExpense(@RequestParam Long expense_id, HttpSession httpSession) {
        Member member = (Member) httpSession.getAttribute("member");
        if (member != null){
            expenseService.deleteExpense(expense_id);
        }
        else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
