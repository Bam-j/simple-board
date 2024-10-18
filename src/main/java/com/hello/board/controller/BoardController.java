package com.hello.board.controller;

import com.hello.board.dto.BoardDTO;
import com.hello.board.service.BoardService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")   //각 매핑 주소의 앞에 /board를 붙여줌.
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        System.out.println("boardDTO = " + boardDTO);
        boardService.save(boardDTO);

        return "index";
    }

    @GetMapping("/")
    public String findAll(Model model) {
        //DB에서 전체 게시글 데이터를 가져와서 list.html에서 보여준다.
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);

        return "list";
    }

    @GetMapping("/{id}")    //경로상의 값을 가져올 땐 @PathVariable 사용
    public String findById(@PathVariable Long id, Model model,
        @PageableDefault(page = 1) Pageable pageable) {
        //1. 해당 게시글의 조회수를 하나 올리기
        boardService.updateHits(id);

        //2. 게시글 데이터를 가져와서 detail.html에 출력
        BoardDTO boardDTO = boardService.findById(id);

        model.addAttribute("board", boardDTO);

        model.addAttribute("page", pageable.getPageNumber());

        return "detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);

        model.addAttribute("boardUpdate", boardDTO);

        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        BoardDTO board = boardService.update(boardDTO);

        model.addAttribute("board", board);

        return "detail";
        //return "redirect:/board" + boardDTO.getId();  <- 상세 조회시 조회수가 올라가므로 수정에 조회수가 영향이 간다.
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.delete(id);

        return "redirect:/board/";
    }

    // /board/paging?page=1
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
        Page<BoardDTO> boardList = boardService.paging(pageable);

        int blockLimit = 3; //보여줄 페이지 블록 수
        int startPage =
            (((int) (Math.ceil((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit
                + 1; // 1 4 7 10 ~~
        int endPage =
            ((startPage + blockLimit - 1) < boardList.getTotalPages()) ?
                startPage + blockLimit - 1 : boardList.getTotalPages();

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "paging";
    }
}
