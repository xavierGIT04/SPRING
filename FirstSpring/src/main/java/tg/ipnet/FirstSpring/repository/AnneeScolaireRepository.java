package tg.ipnet.FirstSpring.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import tg.ipnet.FirstSpring.entity.AnneeScolaire;

public interface AnneeScolaireRepository extends 
								JpaRepository<AnneeScolaire, Long> {

	@Query(value = "SELECT a FROM AnneeScolaire a order by a.dateDebut DESC")
	public List<AnneeScolaire> listByDateRecent();
}
