package julyjolly.librarysystem.service;

import julyjolly.librarysystem.pojo.api.Result;
import julyjolly.librarysystem.pojo.dto.query.extend.ReaderProposalQueryDto;
import julyjolly.librarysystem.pojo.entity.ReaderProposal;
import julyjolly.librarysystem.pojo.vo.ReaderProposalVO;

import java.util.List;

/**
 * 读者建议的业务逻辑接口
 */
public interface ReaderProposalService {

    Result<Void> save(ReaderProposal readerProposal);

    Result<Void> batchDelete(List<Integer> ids);

    Result<Void> update(ReaderProposal readerProposal);

    Result<List<ReaderProposalVO>> query(ReaderProposalQueryDto readerProposalQueryDto);

}
