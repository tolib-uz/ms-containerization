package uz.mservice.song.exception;

public class SongNotFoundExeption  extends RuntimeException{
    public SongNotFoundExeption(String m){
        super(m);
    }
}
