package julyjolly.librarysystem.mapper;

import julyjolly.librarysystem.pojo.dto.query.extend.ReaderProposalQueryDto;
import julyjolly.librarysystem.pojo.entity.ReaderProposal;
import julyjolly.librarysystem.pojo.vo.ReaderProposalVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 读者建议持久化接口
 */
@Mapper
public interface ReaderProposalMapper {

    void save(ReaderProposal readerProposal);

    void update(ReaderProposal readerProposal);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<ReaderProposalVO> query(ReaderProposalQueryDto readerProposalQueryDto);

    Integer queryCount(ReaderProposalQueryDto readerProposalQueryDto);

}
