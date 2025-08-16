package julyjolly.librarysystem.pojo.dto.query.extend;

import julyjolly.librarysystem.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoticeQueryDto extends QueryDto {
    private String name;
    private String content;
}
