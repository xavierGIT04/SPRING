package tg.nassara.estimation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tg.nassara.estimation.entity.AnnonceMarche;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.enums.TypeAnnonce;

import java.time.LocalDate;
import java.util.List;

public interface AnnonceMarcheRepository extends JpaRepository<AnnonceMarche, Long> {

    boolean existsByUrlSource(String urlSource);

    @Query("""
            select a from AnnonceMarche a
            where a.actif = true
              and a.quartier.id = :quartierId
              and a.nature = :nature
              and a.typeAnnonce = :typeAnnonce
              and a.dateCollecte >= :depuis
            """)
    List<AnnonceMarche> findComparables(@Param("quartierId") Long quartierId,
                                        @Param("nature") NatureBien nature,
                                        @Param("typeAnnonce") TypeAnnonce typeAnnonce,
                                        @Param("depuis") LocalDate depuis);
}