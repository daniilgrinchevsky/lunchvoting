package ru.madelinn.lunchvoting.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.madelinn.lunchvoting.AuthorizedUser;
import ru.madelinn.lunchvoting.model.User;
import ru.madelinn.lunchvoting.service.UserService;
import ru.madelinn.lunchvoting.to.UserTo;
import ru.madelinn.lunchvoting.util.Util;

import javax.validation.Valid;
import java.net.URI;

import static ru.madelinn.lunchvoting.util.Util.assureIdConsistent;
import static ru.madelinn.lunchvoting.util.Util.checkNew;


@RestController
@RequestMapping(UserRestController.REST_URL)
public class UserRestController {

    static final String REST_URL = "/rest/profile";
    private static final Logger log = LoggerFactory.getLogger(UserRestController.class);

    @Autowired
    private UserService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(@AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("get {}", authUser.getId());
        return service.get(authUser.getId());
    }

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthorizedUser authUser) {
        log.info("delete {}", authUser.getId());
        service.delete(authUser.getId());
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<User> register(@Valid @RequestBody UserTo userTo) {
        User user = Util.createNewFromTo(userTo);
        log.info("create {}" , user);
        checkNew(user);
        User created = service.create(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody UserTo userTo, @AuthenticationPrincipal AuthorizedUser authUser){
        assureIdConsistent(userTo, authUser.getId());
        log.info("update {} with id={}", authUser, authUser.getId());
        service.update(userTo);
    }

    @PutMapping("/vote/{restaurantId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void vote(@AuthenticationPrincipal AuthorizedUser authUser,@PathVariable("restaurantId") int restaurantId){
        log.info("place vote {}", authUser.getId());
        service.vote(authUser.getId(), restaurantId);
    }



}
