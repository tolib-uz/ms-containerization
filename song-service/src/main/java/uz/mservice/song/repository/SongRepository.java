package uz.mservice.song.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mservice.song.model.Song;

public interface SongRepository extends JpaRepository<Song, Long> {

}
