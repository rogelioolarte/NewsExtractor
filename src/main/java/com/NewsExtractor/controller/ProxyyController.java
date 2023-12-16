package com.NewsExtractor.controller;

import com.NewsExtractor.dto.ProxyyDTO;
import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.entity.Proxyy;
import com.NewsExtractor.exception.SpecificException;
import com.NewsExtractor.service.INewsPaperService;
import com.NewsExtractor.service.IProxyyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proxy")
@Tag(name = "Proxy Controller", description = "Manage all items accordingly.")
public class ProxyyController {

    @Autowired
    private IProxyyService proxyyService;

    @Autowired
    private INewsPaperService newsPaperService;

    @Operation(summary = "Find an proxy using a id parameter.",
            description = "Find an proxy using a id parameter that exists.")
    @GetMapping("/find/{id}")
    public ResponseEntity<ProxyyDTO> findById(@PathVariable Long id) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<Proxyy> proxyyOptional = proxyyService.findById(id);
        if (proxyyOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The proxy id does not exist");

        Proxyy proxyy = proxyyOptional.get();
        ProxyyDTO proxyyDTO = ProxyyDTO.builder().id(proxyy.getId()).name(proxyy.getName())
                .type(proxyy.getType()).address(proxyy.getAddress())
                .port(proxyy.getPort()).state(proxyy.getState())
                .newsPaper(proxyy.getNewsPaper()).build();
        return ResponseEntity.ok(proxyyDTO);
    }

    @Operation(summary = "Find all the proxies.",
            description = "Find all the proxies that exists.")
    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<ProxyyDTO> proxyyDTOList = proxyyService.findAll().stream()
                .map(proxyy -> ProxyyDTO.builder()
                        .id(proxyy.getId()).name(proxyy.getName())
                        .type(String.valueOf(proxyy.getType())).address(proxyy.getAddress())
                        .port(proxyy.getPort()).state(proxyy.getState())
                        .newsPaper(proxyy.getNewsPaper()).build()).toList();
        return ResponseEntity.ok(proxyyDTOList);
    }

    @Operation(summary = "Save an proxy using a request body.",
            description = "Save an proxy using the request body, remember to check " +
                    "the schemas to understand and to save any article you need newspaper " +
                    "object with a unique identifier.")
    @PostMapping("/save")
    public ResponseEntity<URI> save(@RequestBody ProxyyDTO proxyyDTO) throws URISyntaxException {
        if (proxyyDTO.getNewsPaper().getId() == null ||
                proxyyDTO.getNewsPaper().getId().toString().isBlank())
            throw new SpecificException(HttpStatus.BAD_REQUEST, "At least the newspaper id is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(proxyyDTO
                .getNewsPaper().getId());
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The newspaper to be related to the proxy does not exist");
        if (proxyyDTO.getType() == null || proxyyDTO.getAddress() == null ||
                proxyyDTO.getPort() == null) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "No field must be null");
        if (proxyyDTO.getType().isBlank() && proxyyDTO.getAddress().isBlank() &&
                proxyyDTO.getPort().toString().isBlank())
            throw new SpecificException(HttpStatus.BAD_REQUEST,
                    "All complete fields are required");
        final String newName = createName(proxyyDTO);
        if (proxyyService.existsByName(newName)) throw
                new SpecificException(HttpStatus.CONFLICT, "The proxy already exists");

        proxyyService.save(Proxyy.builder().name(newName).type(proxyyDTO.getType())
                .address(proxyyDTO.getAddress()).port(proxyyDTO.getPort())
                .state(404).newsPaper(proxyyDTO.getNewsPaper()).build());
        Optional<Proxyy> proxyyOptional = proxyyService.findByName(createName(proxyyDTO));
        if (proxyyOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The proxy was not added");
        return ResponseEntity.created(new URI(String.format("/api/proxy/find/%d",
                proxyyOptional.get().getId()))).build();
    }

    @Operation(summary = "Update an proxy using a id parameter and a request body.",
            description = "Update an proxy using a id parameter and a request body, " +
                    "remember to check the schemas to understand and to save any article " +
                    "you need newspaper object with a unique identifier.")
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateById(@PathVariable Long id, @RequestBody ProxyyDTO proxyyDTO) {
        if (id == null || id.toString().isBlank()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        if (proxyyDTO.getNewsPaper().getId() == null ||
                proxyyDTO.getNewsPaper().getId().toString().isBlank())
            throw new SpecificException(HttpStatus.BAD_REQUEST, "At least the newspaper id is required");
        Optional<NewsPaper> newsPaperOptional = newsPaperService.findById(proxyyDTO
                .getNewsPaper().getId());
        if (newsPaperOptional.isEmpty()) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The newspaper to be related to the proxy does not exist");
        if (proxyyDTO.getType() == null || proxyyDTO.getAddress() == null ||
                proxyyDTO.getPort() == null) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "No field must be null");
        if (proxyyDTO.getType().isBlank() && proxyyDTO.getAddress().isBlank() &&
                proxyyDTO.getPort().toString().isBlank()) throw
                new SpecificException(HttpStatus.BAD_REQUEST,
                        "All complete fields are required");
        Optional<Proxyy> proxyyOptional = proxyyService.findById(id);
        if (proxyyOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The proxy id does not exist");

        Proxyy proxyy = proxyyOptional.get();
        proxyy.setName(createName(proxyyDTO));
        proxyy.setType(proxyyDTO.getType());
        proxyy.setAddress(proxyyDTO.getAddress());
        proxyy.setPort(proxyyDTO.getPort());
        proxyy.setState(404);
        proxyy.setNewsPaper(proxyyDTO.getNewsPaper());
        proxyyService.save(proxyy);
        return ResponseEntity.ok(String.format("Item with updated Id %d", id));
    }

    @Operation(summary = "Delete an proxy using a id parameter.",
            description = "Delete an proxy using a id parameter that exists.")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        if (id == null) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<Proxyy> proxyyOptional = proxyyService.findById(id);
        if (proxyyOptional.isEmpty()) throw
                new SpecificException(HttpStatus.NOT_FOUND, "The proxy id does not exist");

        proxyyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Tests the proxy and save the " +
            "state using an id parameter that exists.",
            description = "Tests the proxy and save the state using an id " +
                    "parameter that exists, if it can return a connection " +
                    "timeout error, check or change the proxy, ultimately remember " +
                    "that each proxy is tested with the source of the newspaper, so it is " +
                    "the source who does not accept proxies.")
    @GetMapping("/check_proxy/{id}")
    public ResponseEntity<ProxyyDTO> checkProxy(@PathVariable Long id) throws IOException {
        if (id == null) throw new SpecificException(HttpStatus.BAD_REQUEST,
                "The id parameter required");
        Optional<Proxyy> proxyyOptional = proxyyService.findById(id);
        if (proxyyOptional.isEmpty()) throw new SpecificException(HttpStatus.NOT_FOUND,
                "The proxy id does not exist");
        proxyyService.checkProxy(proxyyOptional.get().getNewsPaper().getSource(),proxyyOptional.get());
        Proxyy proxyy = proxyyOptional.get();
        proxyyService.checkProxy(proxyy.getNewsPaper().getSource(), proxyy);
        ProxyyDTO proxyyDTO = ProxyyDTO.builder().id(proxyy.getId()).name(proxyy.getName())
                .type(proxyy.getType()).address(proxyy.getAddress()).port(proxyy.getPort())
                .state(proxyy.getState()).newsPaper(proxyy.getNewsPaper()).build();
        return ResponseEntity.ok(proxyyDTO);
    }

    public String createName(ProxyyDTO proxyyDTO) {
        return proxyyDTO.getType().concat("_").concat(proxyyDTO.getAddress())
                .concat(":").concat(proxyyDTO.getPort().toString());
    }

}
