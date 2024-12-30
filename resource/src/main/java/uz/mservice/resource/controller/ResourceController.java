package uz.mservice.resource.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uz.mservice.resource.model.Resource;
import uz.mservice.resource.service.ResourceService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ResourceController {
    private final ResourceService resourceService;
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
    @PostMapping(value = "resources", consumes = "audio/mpeg")
    public ResponseEntity<Map<String, Long>> createResource(@RequestBody byte[] audioFile){
        Resource savedResource = resourceService.saveResource(audioFile);
        Map<String, Long> response =  new HashMap<>();
        response.put("id", savedResource.getId());

        return ResponseEntity.ok(response);

    }
    @GetMapping("/resources/{id}")
    public ResponseEntity<byte[]> findResponse(@PathVariable Long id){
        Resource resource = resourceService.listResources(id);
        byte[] resFile = resource.getFile();
        return ResponseEntity.ok(resFile);
    }
    @DeleteMapping("resources")
    public ResponseEntity<Map<String, String>> deleteResources(@RequestParam String id) {
        resourceService.deleteResources(id);
        Map<String, String > response = new HashMap<>();
        response.put("ids", id);
        return ResponseEntity.ok(response);
    }
}
