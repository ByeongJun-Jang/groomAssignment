package proj.tricount.domain.settlement;

import lombok.Data;

@Data
public class SettlementParticipant {
    private Long participantId;

    private Long settlementId;
    private Long memberId;

    public SettlementParticipant() {
    }

    public SettlementParticipant(Long participantId, Long settlementId, Long memberId) {
        this.participantId = participantId;
        this.settlementId = settlementId;
        this.memberId = memberId;
    }
}
