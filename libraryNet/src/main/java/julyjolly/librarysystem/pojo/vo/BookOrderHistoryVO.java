package julyjolly.librarysystem.pojo.vo;

import julyjolly.librarysystem.pojo.entity.BookOrderHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookOrderHistoryVO extends BookOrderHistory {
    private String userName;
    private String bookName;
    private String bookCover;
}
