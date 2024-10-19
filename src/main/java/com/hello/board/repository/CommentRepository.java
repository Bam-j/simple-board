package com.hello.board.repository;

import com.hello.board.entity.BoardEntity;
import com.hello.board.entity.CommentEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    //SELECT * FROM comment_table WHERE board_id=? ORDER BY id DESC;
    List<CommentEntity> findAllByBoardEntityOrderByIdDesc(BoardEntity boardEntity);
}
