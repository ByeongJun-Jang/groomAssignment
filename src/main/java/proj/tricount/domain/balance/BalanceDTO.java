package proj.tricount.domain.balance;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class BalanceDTO {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long balanceDTOid;

    private Long payerId;
    private String payerName;
    private Long receiverId;
    private String receiverName;
    private BigDecimal amount;

    public BalanceDTO(Long payerId, String payerName, Long receiverId, String receiverName, BigDecimal amount) {
        this.payerId = payerId;
        this.payerName = payerName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.amount = amount;
    }

    public BalanceDTO() {
    }
}
