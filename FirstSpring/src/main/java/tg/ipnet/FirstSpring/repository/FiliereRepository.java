package tg.ipnet.FirstSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tg.ipnet.FirstSpring.entity.Filiere;

public interface FiliereRepository extends JpaRepository<Filiere, Long> {
	
	@Query(" SELECT f FROM Filiere f order By f.nomFiliere ")
	public List<Filiere> listFiliere();
	
	@Query(value = "SELECT * FROM filiere order By nom_filiere", nativeQuery=true)
	public List<Filiere> listFilieresql();
	
}
