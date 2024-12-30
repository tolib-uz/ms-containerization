package uz.mservice.song.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.mservice.song.model.Song;
import uz.mservice.song.service.SongService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @PostMapping("/songs")
    public ResponseEntity<Map<String, Long>> createSong(@RequestBody Song song){
        Song savedSong = songService.saveSong(song);

        Map<String, Long> response =  new HashMap<>();
        response.put("id", savedSong.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/songs/{id}")
    public ResponseEntity<Song> listSongs(@PathVariable Long id){
        Song song  =  songService.listSong(id);

        return ResponseEntity.ok(song);
    }

    @GetMapping("/songs")
    public ResponseEntity<List<Song>> listAllSongs(){
        return ResponseEntity.ok(songService.listAllSong());
    }

    @DeleteMapping(".songs")
    public ResponseEntity<Map<String, String>> deleteSongs(@RequestParam String id){
        songService.deleteSong(id);
        Map<String, String> resp = new HashMap<>();
        resp.put("ids", id);
        return ResponseEntity.ok(resp);
    }
}
