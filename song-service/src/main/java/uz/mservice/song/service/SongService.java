package uz.mservice.song.service;

import org.springframework.stereotype.Service;
import uz.mservice.song.exception.MetadataMissingExceptions;
import uz.mservice.song.exception.SongNotFoundExeption;
import uz.mservice.song.model.Song;
import uz.mservice.song.repository.SongRepository;

import java.util.Arrays;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SongService {
    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public Song saveSong(Song song){
        if(song.getResourceId() == null){
            throw new MetadataMissingExceptions("Song metadata missing validation error");

        }
        return songRepository.save(song);
    }

    public Song listSong(Long id){
        Optional<Song> songResult = songRepository.findById(id);
        if(!songResult.isPresent()){
            throw new SongNotFoundExeption("The song metadata with the specified id does not exist");
        }

        return songResult.get();
    }

    public List<Song> listAllSong(){
        return songRepository.findAll();
    }

    public void deleteSong(String ids){
        List<Long> songIds = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        songRepository.deleteAllByIdInBatch(songIds);
    }

}
