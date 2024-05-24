package proj.tricount.domain.balance;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Balance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceId;

    private Long settlementId;
    private Long payerId;
    private Long receiverId;
    private BigDecimal amount;

    public Balance(Long balanceId, Long settlementId, Long payerId, Long receiverId, BigDecimal amount) {
        this.balanceId = balanceId;
        this.settlementId = settlementId;
        this.payerId = payerId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    public Balance() {
    }
}
