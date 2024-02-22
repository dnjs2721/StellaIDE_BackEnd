package shootingstar.stellaide.repository.container;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import shootingstar.stellaide.controller.dto.container.AllContainerDto;
import shootingstar.stellaide.controller.dto.container.ContainerDto;
import shootingstar.stellaide.controller.dto.container.QContainerDto;
import shootingstar.stellaide.entity.QSharedUserContainer;

import java.util.List;
import java.util.UUID;

import static shootingstar.stellaide.entity.container.QContainer.container;

public class ContainerRepositoryCustomImpl implements ContainerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ContainerRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public AllContainerDto findContainer(UUID userUuid) {
        List<ContainerDto> ownContainers = queryFactory
                .select(new QContainerDto(
                        container.containerId,
                        container.type,
                        container.name,
                        container.description,
                        container.createdTime,
                        container.lastModifiedTime,
                        container.editUserNickname
                        ))
                .from(container)
                .leftJoin(QSharedUserContainer.sharedUserContainer)
                .on(container.containerId.eq(QSharedUserContainer.sharedUserContainer.container.containerId))
                .where(container.owner.userId.eq(userUuid))
                .fetch();

        List<ContainerDto> shareContainers = queryFactory
                .select(new QContainerDto(
                        container.containerId,
                        container.type,
                        container.name,
                        container.description,
                        container.createdTime,
                        container.lastModifiedTime,
                        container.editUserNickname
                        ))
                .from(QSharedUserContainer.sharedUserContainer)
                .join(container)
                .on(container.containerId.eq(QSharedUserContainer.sharedUserContainer.container.containerId))
                .where(QSharedUserContainer.sharedUserContainer.sharedUser.userId.eq(userUuid))
                .fetch();

        return new AllContainerDto(ownContainers, shareContainers);
    }
}
