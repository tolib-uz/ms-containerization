package uz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.mservice.song.model.Song;
import uz.mservice.song.repository.SongRepository;
import uz.mservice.song.service.SongService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SongServiceTests {
    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SongService songService;

    private Song song;

    @BeforeEach
    void setUp(){
        song = new Song();
        song.setId(1L);
        song.setName("New Artist");
        song.setAlbum("TestAlbum");
        song.setResourceId(10L);
    }

    @Test
    public void saveSong_success(){
        when(songRepository.save(any(Song.class))).thenReturn(song);
        Song savedSong = songService.saveSong(song);

        assertNotNull(savedSong);
        assertEquals(10L, savedSong.getResourceId());
        assertEquals("TestAlbum",savedSong.getAlbum());
    }

    @Test
    public void deleteSong_success(){
        String resourceIds = "1,2,10";
        List<Long> idList = Arrays.asList(1L,2L, 10L);
        songService.deleteSong(resourceIds);
        verify(songRepository, times(1)).deleteAllByIdInBatch(idList);
    }

    @Test
    public void listSongs_success(){
        Long songResourceId = 1L;
        when(songRepository.findById(songResourceId)).thenReturn(Optional.of(song));
        Song result = songService.listSong(songResourceId);

        assertNotNull(result);
        assertEquals(songResourceId, result.getId());
    }
}
