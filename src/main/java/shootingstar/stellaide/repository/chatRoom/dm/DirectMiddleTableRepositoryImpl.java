package shootingstar.stellaide.repository.chatRoom.dm;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import static shootingstar.stellaide.entity.chat.QDirectMiddleTable.directMiddleTable;
import shootingstar.stellaide.repository.chatRoom.dto.FindAllDmRoomByUserIdDto;
import shootingstar.stellaide.repository.chatRoom.dto.QFindAllDmRoomByUserIdDto;

import java.util.List;
import java.util.UUID;

public class DirectMiddleTableRepositoryImpl implements DirectMiddleTableRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    public DirectMiddleTableRepositoryImpl(EntityManager em){this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public List<FindAllDmRoomByUserIdDto> findAllByUserId(UUID userId) {
        return queryFactory
                .select(new QFindAllDmRoomByUserIdDto(
                        directMiddleTable.user.userId,
                        directMiddleTable.directChatRoom.dmChatRoomId
                ))
                .from(directMiddleTable)
                .where(userIdEq(userId))
                .fetch();
    }
    private BooleanExpression userIdEq(UUID userId){
        return userId !=null ? directMiddleTable.user.userId.eq(userId) : null;
    }
}

