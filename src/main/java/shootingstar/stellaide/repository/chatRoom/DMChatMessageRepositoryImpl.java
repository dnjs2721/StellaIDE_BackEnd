package shootingstar.stellaide.repository.chatRoom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllDmMessageByRoomIdDTO;
import shootingstar.stellaide.repository.chatRoom.dto.QFindAllDmMessageByRoomIdDTO;
import static shootingstar.stellaide.entity.chat.QDMChatRoom.dMChatRoom;
import static shootingstar.stellaide.entity.chat.QDMChatMessage.dMChatMessage;

import java.util.List;

public class DMChatMessageRepositoryImpl implements DMChatMessageRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public DMChatMessageRepositoryImpl(EntityManager em){ this.queryFactory =new JPAQueryFactory(em);}

    @Override
    public List<FindAllDmMessageByRoomIdDTO> findAllDMMessageById(Long roomId) {
        return queryFactory
                .select(new QFindAllDmMessageByRoomIdDTO(
                        dMChatRoom.dmChatRoomId,
                        dMChatMessage.sender,
                        dMChatMessage.message,
                        dMChatMessage.createTime
                ))
                .from(dMChatMessage)
                .where(roomIdEq(roomId))
                .fetch();
    }

    @Override
    public Page<FindAllDmMessageByRoomIdDTO> findAllDMMessageById(Long roomId, Pageable pageable) {
        List<FindAllDmMessageByRoomIdDTO> content = queryFactory
                .select(new QFindAllDmMessageByRoomIdDTO(
                        dMChatRoom.dmChatRoomId,
                        dMChatMessage.sender,
                        dMChatMessage.message,
                        dMChatMessage.createTime
                ))
                .from(dMChatMessage)
                .where(roomIdEq(roomId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(dMChatMessage.messageId.asc())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(dMChatMessage.count())
                .from(dMChatMessage);

        return PageableExecutionUtils.getPage(content,pageable,countQuery::fetchOne);
    }

    private BooleanExpression roomIdEq(Long roomId){
        return roomId !=null ? dMChatMessage.dmChatRoom.dmChatRoomId.eq(roomId) : null;
    }
}
