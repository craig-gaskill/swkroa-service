package com.cagst.swkroa.service.user;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Handles endpoints for retrieving and persisting {@link User} objects.
 *
 * @author Craig Gaskill
 */
@RestController
@RequestMapping(value = "users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    private final UserService userService;

    @Inject
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles the request to retrieve the {@link User}s within the system.
     *
     * @return A JSON representation of the {@link User}s within the system.
     */
    @GetMapping
    public Flux<User> getUsers() {
        LOGGER.debug("Received request to getUsers");

        return userService.getUsers();
    }
}
