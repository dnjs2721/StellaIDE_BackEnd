package shootingstar.stellaide.repository.chatRoom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllChatMessageByRoomIdDTO;
import shootingstar.stellaide.repository.chatRoom.dto.QFindAllChatMessageByRoomIdDTO;

import java.util.List;

import static shootingstar.stellaide.entity.chat.QChatRoom.chatRoom;
import static shootingstar.stellaide.entity.chat.QChatRoomMessage.chatRoomMessage;

public class ChatRoomMessageRepositoryImpl implements ChatRoomMessageRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public ChatRoomMessageRepositoryImpl(EntityManager em){ this.queryFactory =new JPAQueryFactory(em);}

    @Override
    public List<FindAllChatMessageByRoomIdDTO> findAllByRoomId(Long roomId) {
        return queryFactory
                .select(new QFindAllChatMessageByRoomIdDTO(
                        chatRoom.chatRoomId,
                        chatRoomMessage.sender,
                        chatRoomMessage.message,
                        chatRoomMessage.createTime,
                        chatRoomMessage.messageType
                ))
                .from(chatRoomMessage)
                .where(roomIdEq(roomId))
                .orderBy()
                .fetch();
    }


    @Override
    public Page<FindAllChatMessageByRoomIdDTO> findAllMessageById(Long roomId, Pageable pageable) {
        List<FindAllChatMessageByRoomIdDTO> content = queryFactory
                .select(new QFindAllChatMessageByRoomIdDTO(
                        chatRoom.chatRoomId,
                        chatRoomMessage.sender,
                        chatRoomMessage.message,
                        chatRoomMessage.createTime,
                        chatRoomMessage.messageType
                ))
                .from(chatRoomMessage)
                .where(roomIdEq(roomId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(chatRoomMessage.messageId.asc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(chatRoomMessage.count())
                .from(chatRoomMessage);

        return PageableExecutionUtils.getPage(content,pageable,countQuery::fetchOne);
    }

    private BooleanExpression roomIdEq(Long roomId){
        return roomId !=null ? chatRoomMessage.chatRoom.chatRoomId.eq(roomId) : null;
//        return roomId !=null ? chatRoomMessage.chatRoom.chatRoomId.eq(roomId) : null;
    }
}
