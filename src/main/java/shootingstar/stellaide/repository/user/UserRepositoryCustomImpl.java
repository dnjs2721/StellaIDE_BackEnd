package shootingstar.stellaide.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import shootingstar.stellaide.entity.QUser;

import static shootingstar.stellaide.entity.QUser.*;

public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public UserRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
}
