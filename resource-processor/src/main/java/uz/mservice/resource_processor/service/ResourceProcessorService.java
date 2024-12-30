package uz.mservice.resource_processor.service;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import uz.mservice.resource_processor.config.ProcessorBrokerProperties;
import uz.mservice.resource_processor.exception.ResourceValidationException;
import uz.mservice.resource_processor.model.ResourceMessage;
import uz.mservice.resource_processor.model.SongRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ResourceProcessorService {
    private final RestTemplate restTemplate;
    private final ProcessorBrokerProperties brokerProperties;
    private final RestTemplate restTemplateForSong;
    public ResourceProcessorService(RestTemplate restTemplate, ProcessorBrokerProperties brokerProperties, RestTemplate restTemplateForSong) {
        this.restTemplate = restTemplate;
        this.brokerProperties = brokerProperties;
        this.restTemplateForSong = restTemplateForSong;
    }
    @RabbitListener(queues = "${broker.queue}")
    @Retryable(
            value = {RestClientException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public void processResource(ResourceMessage message){
        Long resourceId= message.getResourceId();

        if(resourceId!=null){
            String resourceURI = "/resources/"+resourceId;
            byte[] audioFile = restTemplate.getForObject(resourceURI, byte[].class);

            Metadata metadata = extractMetadata(audioFile);

            SongRequest songRequest = new SongRequest();
            songRequest.setName(metadata.get("name"));
            songRequest.setArtist(metadata.get("xmpDM:artist"));
            songRequest.setAlbum(metadata.get("xmpDM:album"));
            songRequest.setLength(metadata.get("xmpDM:duration"));
            songRequest.setYear(metadata.get("xmpDM:releaseDate"));
            songRequest.setResourceId(resourceId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SongRequest> request = new HttpEntity<>(songRequest, headers);
            String uri = "/songs";
            callSongService(uri,request);
            //System.out.println(restTemplate.getUriTemplateHandler());
        }
    }

    @Recover
    public void recover(RestClientException e, ResourceMessage message){
        System.out.println("Recover is failed after retrying.");
    }

    private void callSongService(String url, HttpEntity<SongRequest> request){
        restTemplateForSong.postForObject(url,request, SongRequest.class);
        System.out.println(restTemplate.getUriTemplateHandler());
    }
    private Metadata extractMetadata(byte[] audioFile) {
        ContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        Parser mp3Parser = new Mp3Parser();
        try(InputStream stream =new ByteArrayInputStream(audioFile)){
            mp3Parser.parse(stream, handler,metadata, new ParseContext());
            return metadata;
        }catch (IOException | TikaException | SAXException ex){
            throw new ResourceValidationException((ex.getMessage()));
        }
    }

}
