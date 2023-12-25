package ru.practicum.shareit.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.model.Comment;

@UtilityClass
public class DtoCommentMapper {
    public static Comment dtoToItem(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

//    public static CommentDto itemToDto(Comment comment) {
//        CommentDto commentDto = new CommentDto();
//        commentDto.setId(comment.getId());
//        commentDto.setId(comment.getId());
//        commentDto.setText(comment.getText());
//        commentDto.setCreated(comment.getCreated());
//        commentDto.setAuthorName(comment.getAuthor().getName());
//        return commentDto;
//    }
}
