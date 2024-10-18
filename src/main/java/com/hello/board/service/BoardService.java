package com.hello.board.service;

import com.hello.board.dto.BoardDTO;
import com.hello.board.entity.BoardEntity;
import com.hello.board.entity.BoardFileEntity;
import com.hello.board.repository.BoardFileRepository;
import com.hello.board.repository.BoardRepository;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    public void save(BoardDTO boardDTO) throws IOException {
        if (boardDTO.getBoardFile().isEmpty()) {
            //파일이 첨부되지 않은 경우
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        } else {
            //파일이 첨부된 경우 (1 ~ 5: 파일 저장)
            //1. DTO에 담긴 파일을 꺼낸다.
            MultipartFile boardFile = boardDTO.getBoardFile();

            //2. 파일의 이름을 가져온다.
            String originalFilename = boardFile.getOriginalFilename();

            //3. 서버 저장용 이름으로 수정한다. (난수_파일명.확장자)
            String storedFileName = System.currentTimeMillis() + "_" + originalFilename;

            //4. 저장 경로 설정
            String savePath = "C:/springboot_img/" + storedFileName;

            //5. 해당 경로에 파일 저장
            boardFile.transferTo(new File(savePath));

            //6. board_table에 해당 데이터 save 처리
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            Long savedId = boardRepository.save(boardEntity).getId();

            //7. board_file_table에 해당 데이터 save 처리
            BoardEntity board = boardRepository.findById(savedId).get();
            BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board,
                originalFilename, storedFileName);
            boardFileRepository.save(boardFileEntity);
        }
    }

    @Transactional
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();

        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }

        return boardDTOList;
    }

    @Transactional  //JPA에 정의된 메소드가 아닌 별도 추가 메소드를 사용할 경우 @Transactional을 붙인다.
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    @Transactional  //부모 엔티티(BoardEntity)에서 자식 엔티티(BoardFileEntity)에 접근할 때 @Transactional을 붙인다.
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);

        if (optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);

            return boardDTO;
        } else {
            return null;
        }
    }

    public BoardDTO update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);

        boardRepository.save(boardEntity);

        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 3;  //한 페이지에 보여줄 글의 수

        //한 페이지 당 글을 3개씩 보여주고, 정렬 기준은 id 기준으로 내림차순 정렬
        //page의 값은 0부터 시작. 실제로 사용자가 보는 페이지는 1페이지부터
        Page<BoardEntity> boardEntities = boardRepository.findAll(
            PageRequest.of(page, pageLimit,
                Sort.by(Direction.DESC, "id")));    //properties: Entity 기준 컬럼네임

        System.out.println(
            "boardEntities.getContent() = " + boardEntities.getContent()); // 요청 페이지에 해당하는 글
        System.out.println(
            "boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글갯수
        System.out.println(
            "boardEntities.getNumber() = " + boardEntities.getNumber()); // DB로 요청한 페이지 번호
        System.out.println(
            "boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 갯수
        System.out.println(
            "boardEntities.getSize() = " + boardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
        System.out.println(
            "boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전 페이지 존재 여부
        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫 페이지 여부
        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막 페이지 여부

        //목록: id, writer, title, hits, createdTime
        Page<BoardDTO> boardDTOS = boardEntities.map(boardEntity -> new BoardDTO(
            boardEntity.getId(), boardEntity.getBoardWriter(), boardEntity.getBoardTitle(),
            boardEntity.getBoardHits(), boardEntity.getCreatedTime()));

        return boardDTOS;
    }
}
