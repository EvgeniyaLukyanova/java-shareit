package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItem(toItemDto(comment.getItem()));
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static Comment toComment(CommentDto commentDto, User user) {
        if (commentDto != null) {
            Comment comment = new Comment();
            comment.setId(commentDto.getId());
            comment.setText(commentDto.getText());
            comment.setItem(toItem(commentDto.getItem()));
            comment.setAuthor(user);
            comment.setCreated(commentDto.getCreated());
            return comment;
        } else {
            return null;
        }
    }
}
