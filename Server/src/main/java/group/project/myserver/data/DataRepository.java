package group.project.myserver.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface DataRepository extends JpaRepository<Data, Integer> {

    @Query("SELECT HOUR(d.time_stamp) AS hour, AVG(d.heart_rate) AS averageHeartRate, SUM(d.steps) AS totalSteps " +
            "FROM Data d " +
            "WHERE d.id_user = :userId AND d.time_stamp >= :startTime " +
            "GROUP BY HOUR(d.time_stamp) " +
            "ORDER BY HOUR(d.time_stamp)")
    List<Object[]> dataLast24Hours(@Param("userId") Integer userId,
                                   @Param("startTime") LocalDateTime startTime);

    @Query("SELECT DATE(d.time_stamp) AS date, AVG(d.heart_rate) AS averageHeartRate, SUM(d.steps) AS totalSteps " +
            "FROM Data d " +
            "WHERE d.id_user = :userId AND d.time_stamp >= :startTime " +
            "GROUP BY DATE(d.time_stamp) " +
            "ORDER BY DATE(d.time_stamp)")
    List<Object[]> averageDataPerDay(@Param("userId") Integer userId,
                                     @Param("startTime") LocalDateTime startTime);

    @Query("SELECT CAST(AVG(d.heart_rate) AS INTEGER), Sum(d.steps)" +
            " FROM Data d " +
            "WHERE d.id_user = :userId AND FUNCTION('DATE', d.time_stamp) = CURRENT_DATE")
    List<Object[]> todayData(@Param("userId") Integer userId);

}
