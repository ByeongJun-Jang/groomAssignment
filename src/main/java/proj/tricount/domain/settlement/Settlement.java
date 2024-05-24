package proj.tricount.domain.settlement;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
public class Settlement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settlementId;

    private String name;
    private Timestamp createdDate;
    private List<Expense> expenses;

    public Settlement(Long id, String name, Timestamp createDate) {
        this.settlementId = id;
        this.name = name;
        this.createdDate = createDate;
    }

    public Settlement() {

    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

}
