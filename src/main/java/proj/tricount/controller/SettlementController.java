package proj.tricount.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import proj.tricount.domain.settlement.Settlement;
import proj.tricount.domain.user.Member;
import proj.tricount.service.SettlementService;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    // 정산 생성
    @PostMapping("/settlement")
    @ResponseBody
    public ResponseEntity<Object> createSettlement(@ModelAttribute Settlement settlement, HttpSession httpSession) {
        Member member = (Member) httpSession.getAttribute("member");
        if (member != null) {
            try {
                settlementService.createSettlement(settlement);
                log.info("정산 생성 완료");
                return ResponseEntity.ok(settlement);
            } catch (DataAccessException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 정산 참여자 추가
    @PostMapping("/settlements/{settlementId}/participants")
    @ResponseBody
    public ResponseEntity<Object> addParticipant(@PathVariable Long settlementId, @RequestParam Long memberId, HttpSession httpSession) {
        Member member = (Member) httpSession.getAttribute("member");
        if (member != null) {
            try {
                settlementService.addParticipantToSettlement(settlementId, memberId);
                log.info("정산 참여자 추가 완료");
                return ResponseEntity.ok(settlementId);
            }catch (DataAccessException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 정산 단건 조회
    @GetMapping("/settlements/{settlementId}")
    @ResponseBody
    public Settlement getSettlementById(@PathVariable Long settlementId) {
        return settlementService.getSettlementWithExpenses(settlementId);
    }

    // 정산들 조회
    @GetMapping("/settlements")
    @ResponseBody
    public List<Settlement> getAllSettlements() {
        return settlementService.getAllSettlements();
    }

    // 정산 삭제
    @DeleteMapping("/delete_settlements")
    @ResponseBody
    public void deleteSettlements(@RequestParam Long settlementId, HttpSession httpSession) {
        Member member = (Member) httpSession.getAttribute("member");
        if (member != null) {
            settlementService.deleteSettlement(settlementId);
        }
        else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
