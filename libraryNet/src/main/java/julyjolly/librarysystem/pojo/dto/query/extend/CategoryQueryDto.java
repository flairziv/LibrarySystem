package julyjolly.librarysystem.pojo.dto.query.extend;

import julyjolly.librarysystem.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类别的查询条件类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryQueryDto extends QueryDto {

    /**
     * 类别名
     */
    private String name;

}
