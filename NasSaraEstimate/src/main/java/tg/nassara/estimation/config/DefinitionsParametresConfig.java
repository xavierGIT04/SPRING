package tg.nassara.estimation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import tg.nassara.estimation.entity.ParametreGlobal;
import tg.nassara.estimation.entity.PonderationMethode;
import tg.nassara.estimation.entity.reference.EntiteReference;
import tg.nassara.estimation.entity.reference.Quartier;
import tg.nassara.estimation.entity.reference.ReferenceAvecCoefficient;
import tg.nassara.estimation.entity.reference.Standing;
import tg.nassara.estimation.entity.reference.StatutJuridique;
import tg.nassara.estimation.entity.reference.Topographie;
import tg.nassara.estimation.entity.reference.TypeBien;
import tg.nassara.estimation.entity.reference.TypeEquipement;
import tg.nassara.estimation.entity.reference.TypeVoie;
import tg.nassara.estimation.entity.reference.Viabilisation;
import tg.nassara.estimation.enums.MethodeEstimation;
import tg.nassara.estimation.enums.NatureBien;
import tg.nassara.estimation.repository.ParametreGlobalRepository;
import tg.nassara.estimation.repository.PonderationMethodeRepository;
import tg.nassara.estimation.repository.QuartierRepository;
import tg.nassara.estimation.repository.StandingRepository;
import tg.nassara.estimation.repository.StatutJuridiqueRepository;
import tg.nassara.estimation.repository.TopographieRepository;
import tg.nassara.estimation.repository.TypeBienRepository;
import tg.nassara.estimation.repository.TypeEquipementRepository;
import tg.nassara.estimation.repository.TypeVoieRepository;
import tg.nassara.estimation.repository.ViabilisationRepository;
import tg.nassara.estimation.service.parametrage.ChampParametre;
import tg.nassara.estimation.service.parametrage.DefinitionParametre;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DefinitionsParametresConfig {

    /** Fabrique d'un référentiel valorisé par un coefficient. */
    interface FabriqueReferenceCoefficient<T> {
        T creer(String code, String libelle, BigDecimal coefficient);
    }

    @Bean
    public DefinitionParametre<TypeVoie> definitionTypesVoie(TypeVoieRepository repository) {
        return DefinitionsParametresConfig.<TypeVoie>referenceAvecCoefficient(
                "types-voie", "TypeVoie", repository,
                (code, libelle, coefficient) -> new TypeVoie(code, libelle, coefficient));
    }

    @Bean
    public DefinitionParametre<Viabilisation> definitionViabilisations(ViabilisationRepository repository) {
        return DefinitionsParametresConfig.<Viabilisation>referenceAvecCoefficient(
                "viabilisations", "Viabilisation", repository,
                (code, libelle, coefficient) -> new Viabilisation(code, libelle, coefficient));
    }

    @Bean
    public DefinitionParametre<Topographie> definitionTopographies(TopographieRepository repository) {
        return DefinitionsParametresConfig.<Topographie>referenceAvecCoefficient(
                "topographies", "Topographie", repository,
                (code, libelle, coefficient) -> new Topographie(code, libelle, coefficient));
    }

    @Bean
    public DefinitionParametre<StatutJuridique> definitionStatutsJuridiques(StatutJuridiqueRepository repository) {
        return DefinitionsParametresConfig.<StatutJuridique>referenceAvecCoefficient(
                "statuts-juridiques", "StatutJuridique", repository,
                (code, libelle, coefficient) -> new StatutJuridique(code, libelle, coefficient));
    }

    @Bean
    public DefinitionParametre<TypeBien> definitionTypesBien(TypeBienRepository repository) {
        return DefinitionsParametresConfig.<TypeBien>referenceAvecCoefficient(
                "types-bien", "TypeBien", repository,
                (code, libelle, coefficient) -> new TypeBien(code, libelle, coefficient));
    }

    @Bean
    public DefinitionParametre<Standing> definitionStandings(StandingRepository repository) {
        List<ChampParametre<Standing>> champs = List.of(
                ChampParametre.<Standing>texteImmuable("code", Standing::getCode, Standing::setCode),
                ChampParametre.<Standing>texte("libelle", Standing::getLibelle, Standing::setLibelle),
                ChampParametre.<Standing>decimalPositif("coutConstructionM2",
                        Standing::getCoutConstructionM2, Standing::setCoutConstructionM2),
                ChampParametre.<Standing>decimalNonNegatif("tauxVetusteAnnuel",
                        Standing::getTauxVetusteAnnuel, Standing::setTauxVetusteAnnuel),
                ChampParametre.<Standing>decimalNonNegatif("plafondVetuste",
                        Standing::getPlafondVetuste, Standing::setPlafondVetuste),
                ChampParametre.<Standing>activation("actif", Standing::isActif, Standing::setActif));
        return new DefinitionParametre<Standing>("standings", "Standing", repository, Standing::getCode, champs,
                v -> new Standing(v.texte("code"), v.texte("libelle"), v.decimal("coutConstructionM2"),
                        v.decimal("tauxVetusteAnnuel"), v.decimal("plafondVetuste")));
    }

    @Bean
    public DefinitionParametre<TypeEquipement> definitionTypesEquipement(TypeEquipementRepository repository) {
        List<ChampParametre<TypeEquipement>> champs = List.of(
                ChampParametre.<TypeEquipement>texteImmuable("code", TypeEquipement::getCode, TypeEquipement::setCode),
                ChampParametre.<TypeEquipement>texte("libelle", TypeEquipement::getLibelle, TypeEquipement::setLibelle),
                ChampParametre.<TypeEquipement>decimalNonNegatif("montantReference",
                        TypeEquipement::getMontantReference, TypeEquipement::setMontantReference),
                ChampParametre.<TypeEquipement>activation("actif", TypeEquipement::isActif, TypeEquipement::setActif));
        return new DefinitionParametre<TypeEquipement>("types-equipement", "TypeEquipement", repository,
                TypeEquipement::getCode, champs,
                v -> new TypeEquipement(v.texte("code"), v.texte("libelle"), v.decimal("montantReference")));
    }

    @Bean
    public DefinitionParametre<Quartier> definitionQuartiers(QuartierRepository repository) {
        List<ChampParametre<Quartier>> champs = List.of(
                ChampParametre.<Quartier>texteImmuable("code", Quartier::getCode, Quartier::setCode),
                ChampParametre.<Quartier>texte("libelle", Quartier::getLibelle, Quartier::setLibelle),
                ChampParametre.<Quartier>decimalPositif("prixBaseTerrainM2",
                        Quartier::getPrixBaseTerrainM2, Quartier::setPrixBaseTerrainM2),
                ChampParametre.<Quartier>decimalPositif("coefficientZone",
                        Quartier::getCoefficientZone, Quartier::setCoefficientZone),
                ChampParametre.<Quartier>decimalPositifOptionnel("tauxCapitalisation",
                        Quartier::getTauxCapitalisation, Quartier::setTauxCapitalisation),
                ChampParametre.<Quartier>booleen("prixBaseProvisoire",
                        Quartier::isPrixBaseProvisoire, Quartier::setPrixBaseProvisoire),
                ChampParametre.<Quartier>activation("actif", Quartier::isActif, Quartier::setActif));
        return new DefinitionParametre<Quartier>("quartiers", "Quartier", repository, Quartier::getCode, champs,
                v -> new Quartier(v.texte("code"), v.texte("libelle"), v.decimal("prixBaseTerrainM2")));
    }

    @Bean
    public DefinitionParametre<PonderationMethode> definitionPonderations(PonderationMethodeRepository repository) {
        List<ChampParametre<PonderationMethode>> champs = List.of(
                ChampParametre.<PonderationMethode, MethodeEstimation>enumerationImmuable("methode",
                        MethodeEstimation.class, PonderationMethode::getMethode, PonderationMethode::setMethode),
                ChampParametre.<PonderationMethode, NatureBien>enumerationImmuable("natureBien",
                        NatureBien.class, PonderationMethode::getNatureBien, PonderationMethode::setNatureBien),
                ChampParametre.<PonderationMethode>decimalNonNegatif("poids",
                        PonderationMethode::getPoids, PonderationMethode::setPoids),
                ChampParametre.<PonderationMethode>activation("active",
                        PonderationMethode::isActive, PonderationMethode::setActive));
        return new DefinitionParametre<PonderationMethode>("ponderations-methodes", "PonderationMethode",
                repository, p -> p.getMethode() + "/" + p.getNatureBien(), champs,
                v -> new PonderationMethode(v.enumeration("methode", MethodeEstimation.class),
                        v.enumeration("natureBien", NatureBien.class), v.decimal("poids")));
    }

    @Bean
    public DefinitionParametre<ParametreGlobal> definitionParametresGlobaux(ParametreGlobalRepository repository) {
        List<ChampParametre<ParametreGlobal>> champs = List.of(
                ChampParametre.<ParametreGlobal>texteImmuable("cle", ParametreGlobal::getCle, ParametreGlobal::setCle),
                ChampParametre.<ParametreGlobal>decimalNonNegatif("valeur",
                        ParametreGlobal::getValeur, ParametreGlobal::setValeur),
                ChampParametre.<ParametreGlobal>texteOptionnel("unite",
                        ParametreGlobal::getUnite, ParametreGlobal::setUnite),
                ChampParametre.<ParametreGlobal>texteOptionnel("description",
                        ParametreGlobal::getDescription, ParametreGlobal::setDescription));
        return new DefinitionParametre<ParametreGlobal>("parametres-globaux", "ParametreGlobal", repository,
                ParametreGlobal::getCle, champs,
                v -> new ParametreGlobal(v.texte("cle"), v.decimal("valeur"),
                        v.texteOptionnel("unite"), v.texteOptionnel("description")));
    }

    /** Référentiel valorisé par un coefficient : code (immuable), libellé, coefficient, activation. */
    private static <T extends ReferenceAvecCoefficient> DefinitionParametre<T> referenceAvecCoefficient(
            String type, String nomEntite, JpaRepository<T, Long> repository,
            FabriqueReferenceCoefficient<T> fabrique) {
        List<ChampParametre<T>> champs = List.of(
                ChampParametre.<T>texteImmuable("code", EntiteReference::getCode, EntiteReference::setCode),
                ChampParametre.<T>texte("libelle", EntiteReference::getLibelle, EntiteReference::setLibelle),
                ChampParametre.<T>decimalPositif("coefficient",
                        ReferenceAvecCoefficient::getCoefficient, ReferenceAvecCoefficient::setCoefficient),
                ChampParametre.<T>activation("actif", EntiteReference::isActif, EntiteReference::setActif));
        return new DefinitionParametre<T>(type, nomEntite, repository, EntiteReference::getCode, champs,
                v -> fabrique.creer(v.texte("code"), v.texte("libelle"), v.decimal("coefficient")));
    }
}