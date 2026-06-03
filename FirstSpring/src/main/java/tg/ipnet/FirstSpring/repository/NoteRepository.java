package tg.ipnet.FirstSpring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tg.ipnet.FirstSpring.entity.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

}
