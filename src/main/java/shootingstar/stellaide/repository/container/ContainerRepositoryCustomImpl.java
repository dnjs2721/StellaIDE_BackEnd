package shootingstar.stellaide.repository.container;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import shootingstar.stellaide.controller.dto.container.FindContainerDto;
import shootingstar.stellaide.controller.dto.container.QFindContainerDto;
import shootingstar.stellaide.entity.QSharedUserContainer;
import shootingstar.stellaide.entity.SharedUserContainer;
import shootingstar.stellaide.entity.container.ContainerAlign;
import shootingstar.stellaide.entity.container.ContainerGroup;
import shootingstar.stellaide.repository.sharedUserContainer.SharedUserContainerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static shootingstar.stellaide.entity.container.QContainer.container;

public class ContainerRepositoryCustomImpl implements ContainerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ContainerRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<FindContainerDto> findContainer(ContainerGroup group, String query, ContainerAlign align, UUID userUuid) {
        if (group == ContainerGroup.SHARE) {
            return queryFactory
                    .select(new QFindContainerDto(
                            container.containerId,
                            container.type,
                            container.name,
                            container.description,
                            container.editUserUuid,
                            container.createdTime,
                            container.lastModifiedTime))
                    .from(QSharedUserContainer.sharedUserContainer)
                    .join(container)
                    .on(container.containerId.eq(QSharedUserContainer.sharedUserContainer.container.containerId))
                    .where(QSharedUserContainer.sharedUserContainer.sharedUser.userId.eq(userUuid), containQuery(query))
                    .orderBy(createOrderSpecifier(align))
                    .fetch();
        }
        else if (group == ContainerGroup.OWN){
            return queryFactory
                    .select(new QFindContainerDto(
                            container.containerId,
                            container.type,
                            container.name,
                            container.description,
                            container.editUserUuid,
                            container.createdTime,
                            container.lastModifiedTime))
                    .from(container)
                    .where(containQuery(query), container.owner.userId.eq(userUuid))
                    .orderBy(createOrderSpecifier(align))
                    .fetch();
        }
        else {
            return queryFactory
                    .select(new QFindContainerDto(
                            container.containerId,
                            container.type,
                            container.name,
                            container.description,
                            container.editUserUuid,
                            container.createdTime,
                            container.lastModifiedTime))
                    .from(container)
                    .leftJoin(QSharedUserContainer.sharedUserContainer)
                    .on(container.containerId.eq(QSharedUserContainer.sharedUserContainer.container.containerId))
                    .where(containQuery(query), container.owner.userId.eq(userUuid).or(QSharedUserContainer.sharedUserContainer.sharedUser.userId.eq(userUuid)))
                    .orderBy(createOrderSpecifier(align))
                    .fetch();
        }
    }

    private BooleanExpression containQuery(String query) {
        return StringUtils.hasText(query) ? container.name.contains(query) : null;
    }

    private OrderSpecifier createOrderSpecifier(ContainerAlign align) {
        return switch (align) {
            case CREATE_TIME_ASC -> new OrderSpecifier<>(Order.ASC, container.createdTime);
            case CREATE_TIME_DESC -> new OrderSpecifier<>(Order.DESC, container.createdTime); // default
            case MODIFIED_TIME_ASC -> new OrderSpecifier<>(Order.ASC, container.lastModifiedTime);
            case MODIFIED_TIME_DESC -> new OrderSpecifier<>(Order.DESC, container.lastModifiedTime);
        };
    }
}
