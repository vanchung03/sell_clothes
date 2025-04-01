package com.example.demo.mapper;

import com.example.demo.dto.ReviewReplyDTO;
import com.example.demo.entity.ReviewReply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReviewReplyMapper {

    ReviewReplyMapper INSTANCE = Mappers.getMapper(ReviewReplyMapper.class);

    // MapStruct sẽ tự động ánh xạ trường giống tên
    // Nếu tên trường khác nhau, bạn có thể dùng @Mapping(source = "...", target = "...")
    @Mapping(target = "replyId", source = "replyId")
    ReviewReply toEntity(ReviewReplyDTO dto);

    @Mapping(target = "replyId", source = "replyId")
    ReviewReplyDTO toDto(ReviewReply entity);
}
