package tg.nassara.estimation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tg.nassara.estimation.entity.TransactionImmobiliere;
import tg.nassara.estimation.enums.StatutTransaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionImmobiliere, Long> {

    /** Transactions validées pas encore consommées par le recalibrage du coefficient de zone et du taux. */
    List<TransactionImmobiliere> findByStatutAndIntegreApprentissageFalse(StatutTransaction statut);

    /** Transactions validées pas encore consommées par le recalibrage du coût de construction (étape 9c-coût). */
    List<TransactionImmobiliere> findByStatutAndIntegreCoutFalse(StatutTransaction statut);

    /** Historique d'un quartier (comparaison de marché, correction des biais). */
    @Query("""
            select t from TransactionImmobiliere t
            where t.statut = :statut and t.bien.quartier.id = :quartierId
            order by t.dateTransaction desc
            """)
    List<TransactionImmobiliere> findHistoriqueParQuartier(@Param("quartierId") Long quartierId,
                                                           @Param("statut") StatutTransaction statut);

    Page<TransactionImmobiliere> findAllByOrderByDateTransactionDesc(Pageable pageable);

    Page<TransactionImmobiliere> findByStatutOrderByDateTransactionDesc(StatutTransaction statut, Pageable pageable);

    /** Détection de doublon : une autre transaction non annulée existe-t-elle pour ce bien ? */
    boolean existsByBienIdAndStatutNot(Long bienId, StatutTransaction statut);
}