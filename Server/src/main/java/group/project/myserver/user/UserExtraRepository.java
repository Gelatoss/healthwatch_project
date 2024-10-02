package group.project.myserver.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface UserExtraRepository extends JpaRepository<UserExtra, Integer> {

    @Query("SELECT ue FROM UserExtra ue WHERE ue.user.id = :userId")
    Optional<UserExtra> findExtraByUserId(Integer userId);
}
