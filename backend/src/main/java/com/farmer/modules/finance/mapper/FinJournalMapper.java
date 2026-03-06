package com.farmer.modules.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmer.modules.finance.entity.FinJournal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FinJournalMapper extends BaseMapper<FinJournal> {

    @Select("<script>" +
            "SELECT * FROM fin_journal WHERE deleted = 0" +
            "<if test='accountSetId != null'> AND account_set_id = #{accountSetId}</if>" +
            "<if test='accountBookId != null'> AND account_book_id = #{accountBookId}</if>" +
            "<if test='journalType != null'> AND journal_type = #{journalType}</if>" +
            "<if test='startDate != null'> AND journal_date &gt;= #{startDate}</if>" +
            "<if test='endDate != null'> AND journal_date &lt;= #{endDate}</if>" +
            " ORDER BY journal_date ASC, id ASC" +
            "</script>")
    IPage<FinJournal> selectPage(Page<FinJournal> page,
                                  @Param("accountSetId") Long accountSetId,
                                  @Param("accountBookId") Long accountBookId,
                                  @Param("journalType") Integer journalType,
                                  @Param("startDate") String startDate,
                                  @Param("endDate") String endDate);
}
