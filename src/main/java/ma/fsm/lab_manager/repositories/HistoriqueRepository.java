package ma.fsm.lab_manager.repositories;

import ma.fsm.lab_manager.entities.Historique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface HistoriqueRepository extends JpaRepository<Historique, Long> {
    @Query("SELECT h.action, COUNT(h) FROM Historique h WHERE YEAR(h.dateAction) = :year GROUP BY h.action")
    List<Object[]> getStatsByYear(@Param("year") int year);
}