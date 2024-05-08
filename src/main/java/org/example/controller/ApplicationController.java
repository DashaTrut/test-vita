package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.ApplicationDto;
import org.example.dto.ApplicationUpdateDto;
import org.example.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/application")
public class ApplicationController {
    @Autowired
    private final ApplicationService applicationService;


    @PostMapping //создание заявки;
    public ApplicationDto addApplication(@RequestBody @Valid ApplicationDto applicationDto, @RequestHeader("X-Sharer-User-Id") Integer id) {
        log.info("Add Application{}", applicationDto);
        return applicationService.create(applicationDto, id);

    }

    @PatchMapping("{idApplication}") // обновление статуса или описания
    public ApplicationDto updateStatusApplication(@PathVariable Integer idApplication,
                                                  @RequestBody ApplicationUpdateDto applicationUpdateDto,
                                                  @RequestHeader("X-Sharer-User-Id") Integer id) {
        log.info("Update status Application{}", applicationUpdateDto);

        return applicationService.updateStatusOrDescription(applicationUpdateDto, id, idApplication);
    }

    @GetMapping
    public Collection<ApplicationDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestParam(required = false) String text,
                                             @RequestParam(defaultValue = "true", required = false) Boolean asc,
                                             @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "5") Integer size) {
        return applicationService.getAllForUser(userId, from, size, asc, text);
    }

}
