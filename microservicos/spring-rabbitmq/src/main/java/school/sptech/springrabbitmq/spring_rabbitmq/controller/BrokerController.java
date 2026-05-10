package school.sptech.springrabbitmq.spring_rabbitmq.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.sptech.springrabbitmq.spring_rabbitmq.dto.MessageDto;
import school.sptech.springrabbitmq.spring_rabbitmq.service.ProducerService;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class BrokerController {

    private final ProducerService producerService;

    @PostMapping
    public ResponseEntity<Void> enviarMensagem(@RequestBody MessageDto message) {
        producerService.send(message);
        return ResponseEntity.status(202).build();
    }
}