package com.hello.board.repository;

import com.hello.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    //UPDATE board_table SET board_hits=board_hits+1 WHERE id=?
    @Modifying  //DELETE, UPDATE 등의 쿼리를 실행할 때 붙이는 어노테이션
    @Query(value = "UPDATE BoardEntity b SET b.boardHits=b.boardHits+1 WHERE b.id=:id")
    void updateHits(@Param("id") Long id);
}
