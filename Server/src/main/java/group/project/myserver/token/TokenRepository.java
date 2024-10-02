package group.project.myserver.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.expression.spel.ast.OpLT;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query(value = "SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id WHERE u.id = :id AND (t.expired = FALSE OR t.revoked = FALSE)")
    List<Token> findAllValidTokenByUser(@Param("id")Integer id);

    @Query(value = "SELECT t.revoked FROM Token t WHERE  t.token = :token")
    Optional<Boolean> tokenIsRevoked(@Param("token") String token);

    Optional<Token> findByToken(String token);

}
