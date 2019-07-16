package com.cagst.swkroa.service.user;

import com.cagst.swkroa.service.person.Person;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Will converter a {@link UserEntity} into a {@link User} and vice-versa.
 *
 * @author Craig Gaskill
 */
public abstract class UserConverter {
    @NonNull
    public static User convert(@NonNull UserEntity entity, @Nullable Person person) {
        return User.builder()
            .userId(entity.userId())
            .person(person)
            .username(entity.username())
            .userType(entity.userType())
            .lastLoginDateTime(entity.lastLoginDateTime())
            .lastLoginIp(entity.lastLoginIp())
            .temporary(entity.temporary())
            .lockedDateTime(entity.lockedDateTime())
            .expiredDateTime(entity.expiredDateTime())
            .changedDateTime(entity.changedDateTime())
            .active(entity.active())
            .updateCount(entity.updateCount())
            .build();
    }

    @NonNull
    public static UserEntity convert(@NonNull User user) {
        return UserEntity.builder()
            .userId(user.userId())
            .personId(user.person() != null ? user.person().personId() : null)
            .username(user.username())
            .userType(user.userType())
            .lastLoginDateTime(user.lastLoginDateTime())
            .lastLoginIp(user.lastLoginIp())
            .temporary(user.temporary())
            .lockedDateTime(user.lockedDateTime())
            .expiredDateTime(user.expiredDateTime())
            .changedDateTime(user.changedDateTime())
            .active(user.active())
            .updateCount(user.updateCount())
            .build();
    }
}
