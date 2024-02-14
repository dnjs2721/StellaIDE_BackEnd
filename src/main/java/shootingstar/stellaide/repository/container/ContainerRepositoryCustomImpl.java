package shootingstar.stellaide.repository.container;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.util.StringUtils;
import shootingstar.stellaide.controller.dto.container.FindContainerDto;
import shootingstar.stellaide.controller.dto.container.QFindContainerDto;
import shootingstar.stellaide.controller.dto.container.SaveContainerDto;

import java.util.List;
import java.util.UUID;

import static shootingstar.stellaide.entity.QContainer.*;

public class ContainerRepositoryCustomImpl implements ContainerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ContainerRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<FindContainerDto> findContainer(String group, String query, String align) {
        return queryFactory
                .select(new QFindContainerDto(
                        container.containerId,
                        container.type,
                        container.name,
                        container.description,
                        container.path,
                        container.createTime,
                        container.editTime,
                        container.editUserId))
                .join(container) // 조건 수정: 그룹(모두, 소유, 공유)에 따른 컨테이너 -> 그룹 컨테이너 테이블에 해당 유저의 컨테이너 리스트만 추출
                .where(containQuery(query))
                .fetch();
    }

//    @Override
//    public void deleteById(UUID containerId) {
//        queryFactory
//                .delete(container)
//                .where(container.containerId.eq(containerId))
//                .execute();
//    }

    private BooleanExpression containQuery(String query) {
        return StringUtils.hasText(query) ? container.name.contains(query) : null;
    }
}
