package julyjolly.librarysystem.pojo.dto.query.extend;

import julyjolly.librarysystem.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReaderProposalQueryDto extends QueryDto {
    private String content;
    private Integer userId;
    private Boolean isPublish;
    private Boolean isReply;
}
