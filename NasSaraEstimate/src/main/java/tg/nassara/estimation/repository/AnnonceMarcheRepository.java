package tg.nassara.estimation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tg.nassara.estimation.entity.AnnonceMarche;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.OrigineAnnonce;
import tg.nassara.estimation.enums.TypeAnnonce;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnnonceMarcheRepository extends JpaRepository<AnnonceMarche, Long> {

    boolean existsByUrlSource(String urlSource);

    Optional<AnnonceMarche> findByUrlSource(String urlSource);

    List<AnnonceMarche> findByOrigineAndActifTrue(OrigineAnnonce origine);

    List<AnnonceMarche> findByQuartierIdOrderByDateCollecteDesc(Long quartierId);

    /** Candidats à la comparaison : même quartier, même nature, même type d'annonce, surface proche, récents. */
    @Query("""
            select a from AnnonceMarche a
            where a.actif = true
              and a.quartier.id = :quartierId
              and a.nature = :nature
              and a.typeAnnonce = :typeAnnonce
              and a.dateCollecte >= :depuis
              and a.surfaceTerrainM2 between :surfaceMin and :surfaceMax
            """)
    List<AnnonceMarche> findComparables(@Param("quartierId") Long quartierId,
                                        @Param("nature") NatureBien nature,
                                        @Param("typeAnnonce") TypeAnnonce typeAnnonce,
                                        @Param("depuis") LocalDate depuis,
                                        @Param("surfaceMin") BigDecimal surfaceMin,
                                        @Param("surfaceMax") BigDecimal surfaceMax);
}
