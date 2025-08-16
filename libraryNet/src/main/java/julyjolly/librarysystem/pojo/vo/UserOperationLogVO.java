package julyjolly.librarysystem.pojo.vo;

import julyjolly.librarysystem.pojo.entity.UserOperationLog;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserOperationLogVO extends UserOperationLog {
    private String userName;
}
