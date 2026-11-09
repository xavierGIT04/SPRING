package tg.nassara.estimation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tg.nassara.estimation.entity.TransactionImmobiliere;
import tg.nassara.estimation.enums.StatutTransaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionImmobiliere, Long> {

    /** Transactions validées pas encore consommées par le recalibrage. */
    List<TransactionImmobiliere> findByStatutAndIntegreApprentissageFalse(StatutTransaction statut);

    /** Historique d'un quartier (comparaison de marché, correction des biais). */
    @Query("""
            select t from TransactionImmobiliere t
            where t.statut = :statut and t.bien.quartier.id = :quartierId
            order by t.dateTransaction desc
            """)
    List<TransactionImmobiliere> findHistoriqueParQuartier(@Param("quartierId") Long quartierId,
                                                           @Param("statut") StatutTransaction statut);
}