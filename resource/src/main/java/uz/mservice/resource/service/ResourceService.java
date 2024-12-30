package uz.mservice.resource.service;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.LyricsHandler;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;
import uz.mservice.resource.configuration.BrockerProperties;
import uz.mservice.resource.exception.ResourceValidationException;
import uz.mservice.resource.model.Resource;
import uz.mservice.resource.model.ResourceMessage;
import uz.mservice.resource.repository.ResourceRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final RestTemplate restTemplate;
    private final BrockerProperties brockerProperties;
    private final AmqpTemplate amqpTemplate;

    public ResourceService(ResourceRepository resourceRepository, RestTemplate restTemplate, BrockerProperties brockerProperties, AmqpTemplate amqpTemplate) {
        this.resourceRepository = resourceRepository;
        this.restTemplate = restTemplate;
        this.brockerProperties = brockerProperties;
        this.amqpTemplate = amqpTemplate;
    }

    public Resource saveResource(byte[] audioFile) {
        String contentType;

        try {
            contentType =processMp3(audioFile).get("Content-Type");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (TikaException e) {
            throw new RuntimeException(e);
        }
        String fileType;
        Tika tika = new Tika();
        fileType = tika.detect(audioFile);

        if("audio/mpeg".equalsIgnoreCase(contentType)&&"application/octet-stream".equalsIgnoreCase(fileType)) {
            Resource resource = new Resource();
            resource.setFile(audioFile);
            Resource savedResource = resourceRepository.save(resource);
            sendResourceId(savedResource.getId());
            return savedResource;
        }else{
            throw new ResourceValidationException("The provided file is not valid file type.");
        }

    }

    public Metadata processMp3(byte[] mp3FileInByte) throws IOException, SAXException, TikaException {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        InputStream inputstream = new ByteArrayInputStream(mp3FileInByte);
        ParseContext pcontext = new ParseContext();

        Mp3Parser  mp3Parser = new Mp3Parser();
        mp3Parser.parse(inputstream, handler, metadata, pcontext);
        LyricsHandler lyrics = new LyricsHandler(inputstream, handler);

        return metadata;
    }

    public void sendResourceId(Long resourceId) {
        ResourceMessage message = new ResourceMessage(resourceId);
        amqpTemplate.convertAndSend(brockerProperties.getExchange(), brockerProperties.getRoutingkey(), message);
        System.out.println("Resource ID sent: " + resourceId);
    }

    public void deleteResources(String ids){
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        resourceRepository.deleteAllByIdInBatch(idList);
    }

    public Resource listResources(Long id){
        Optional<Resource> resource = resourceRepository.findById(id);
        if(!resource.isPresent()){
            throw new ResourceValidationException("The resource with the specified id does not exist.");
        }
        return resource.get();

    }
}
