package uz.mservice.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.client.RestTemplate;
import uz.mservice.resource.configuration.BrockerProperties;
import uz.mservice.resource.model.Resource;
import uz.mservice.resource.repository.ResourceRepository;
import uz.mservice.resource.service.ResourceService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTests {
    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private BrockerProperties brockerProperties;

    @Mock
    private AmqpTemplate amqpTemplate;

    @InjectMocks
    private ResourceService resourceService;

    private Resource resource;
    private byte[] audioFile;

    @BeforeEach
    void setUP(){
        resource = new Resource();
        audioFile = new byte[]{1,2,3};
        resource.setFile(audioFile);
        resource.setId(1L);
    }
    @Test
    public void saveResource_success(){
        when(resourceRepository.save(any(Resource.class))).thenReturn(resource);
        Resource savedResource = resourceService.saveResource(audioFile);

        assertNotNull(savedResource);
        assertEquals(audioFile, savedResource.getFile());
    }
    @Test
    public void deleteResources_success(){
        String ids="1,2,3";
        List<Long> idList = Arrays.asList(1L, 2L, 3L);
        resourceService.deleteResources(ids);
        verify(resourceRepository, times(1)).deleteAllByIdInBatch(idList);
    }
    @Test
    void listResources_success() {
        Long resourceId = 1L;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        Resource result = resourceService.listResources(resourceId);

        assertNotNull(result);
        assertEquals(resourceId, result.getId());
    }
}
