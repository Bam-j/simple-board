package com.hello.board.service;

import com.hello.board.dto.CommentDTO;
import com.hello.board.entity.BoardEntity;
import com.hello.board.entity.CommentEntity;
import com.hello.board.repository.BoardRepository;
import com.hello.board.repository.CommentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public Long save(CommentDTO commentDTO) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(commentDTO.getBoardId());

        if (optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            CommentEntity commentEntity = CommentEntity.toSaveEntity(commentDTO, boardEntity);

            return commentRepository.save(commentEntity).getId();

        } else {
            return null;
        }
    }

    public List<CommentDTO> findAll(Long boardId) {
        //SELECT * FROM comment_table WHERE board_id=? ORDER BY id DESC;
        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        List<CommentEntity> commentEntityList = commentRepository.findAllByBoardEntityOrderByIdDesc(boardEntity);

        List<CommentDTO> commentDTOList = new ArrayList<>();

        for (CommentEntity commentEntity : commentEntityList) {
            CommentDTO commentDTO = CommentDTO.toCommentDTO(commentEntity, boardId);
            commentDTOList.add(commentDTO);
        }

        return commentDTOList;
    }
}
