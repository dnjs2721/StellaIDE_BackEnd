package shootingstar.stellaide.repository.chatRoom.container;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllChatMessageByRoomIdDto;
import shootingstar.stellaide.repository.chatRoom.dto.QFindAllChatMessageByRoomIdDto;

import java.util.List;

import static shootingstar.stellaide.entity.chat.QContainerChatRoom.containerChatRoom;
import static shootingstar.stellaide.entity.chat.QContainerChatRoomMessage.containerChatRoomMessage;

public class ContainerChatRoomMessageRepositoryImpl implements ContainerChatRoomMessageRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public ContainerChatRoomMessageRepositoryImpl(EntityManager em){ this.queryFactory =new JPAQueryFactory(em);}

    @Override
    public List<FindAllChatMessageByRoomIdDto> findAllByRoomId(Long roomId) {
        return queryFactory
                .select(new QFindAllChatMessageByRoomIdDto(
                        containerChatRoom.chatRoomId,
                        containerChatRoomMessage.sender,
                        containerChatRoomMessage.message,
                        containerChatRoomMessage.createdTime,
                        containerChatRoomMessage.messageType
                ))
                .from(containerChatRoomMessage)
                .where(roomIdEq(roomId))
                .orderBy()
                .fetch();
    }


    @Override
    public Page<FindAllChatMessageByRoomIdDto> findAllMessageById(Long roomId, Pageable pageable) {
        List<FindAllChatMessageByRoomIdDto> content = queryFactory
                .select(new QFindAllChatMessageByRoomIdDto(
                        containerChatRoom.chatRoomId,
                        containerChatRoomMessage.sender,
                        containerChatRoomMessage.message,
                        containerChatRoomMessage.createdTime,
                        containerChatRoomMessage.messageType
                ))
                .from(containerChatRoomMessage)
                .where(roomIdEq(roomId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(containerChatRoomMessage.messageId.asc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(containerChatRoomMessage.count())
                .from(containerChatRoomMessage);

        return PageableExecutionUtils.getPage(content,pageable,countQuery::fetchOne);
    }

    private BooleanExpression roomIdEq(Long roomId){
        return roomId !=null ? containerChatRoomMessage.containerChatRoom.chatRoomId.eq(roomId) : null;
//        return roomId !=null ? chatRoomMessage.chatRoom.chatRoomId.eq(roomId) : null;
    }
}
