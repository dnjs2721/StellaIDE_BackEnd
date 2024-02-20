package shootingstar.stellaide.repository.chatRoom.dm;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllDmMessageByRoomIdDto;
import shootingstar.stellaide.repository.chatRoom.dto.QFindAllDmMessageByRoomIdDto;

import static shootingstar.stellaide.entity.chat.QDirectChatRoom.directChatRoom;
import static shootingstar.stellaide.entity.chat.QDirectChatRoomMessage.directChatRoomMessage;

import java.util.List;

public class DirectChatRoomMessageRepositoryImpl implements DirectChatRoomMessageRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public DirectChatRoomMessageRepositoryImpl(EntityManager em){ this.queryFactory =new JPAQueryFactory(em);}

    @Override
    public List<FindAllDmMessageByRoomIdDto> findAllDMMessageById(Long roomId) {
        return queryFactory
                .select(new QFindAllDmMessageByRoomIdDto(
                        directChatRoom.dmChatRoomId,
                        directChatRoomMessage.sender,
                        directChatRoomMessage.message,
                        directChatRoomMessage.createdTime
                ))
                .from(directChatRoomMessage)
                .where(roomIdEq(roomId))
                .fetch();
    }

    @Override
    public Page<FindAllDmMessageByRoomIdDto> findAllDMMessageById(Long roomId, Pageable pageable) {
        List<FindAllDmMessageByRoomIdDto> content = queryFactory
                .select(new QFindAllDmMessageByRoomIdDto(
                        directChatRoom.dmChatRoomId,
                        directChatRoomMessage.sender,
                        directChatRoomMessage.message,
                        directChatRoomMessage.createdTime
                ))
                .from(directChatRoomMessage)
                .where(roomIdEq(roomId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(directChatRoomMessage.messageId.asc())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(directChatRoomMessage.count())
                .from(directChatRoomMessage);

        return PageableExecutionUtils.getPage(content,pageable,countQuery::fetchOne);
    }

    private BooleanExpression roomIdEq(Long roomId){
        return roomId !=null ? directChatRoomMessage.directChatRoom.dmChatRoomId.eq(roomId) : null;
    }
}
