package com.CartoleriaPapyrus.ecommerce.controllers;

import com.CartoleriaPapyrus.ecommerce.entities.RoleType;
import com.CartoleriaPapyrus.ecommerce.entities.User;
import com.CartoleriaPapyrus.ecommerce.services.RoleService;
import com.CartoleriaPapyrus.ecommerce.services.UserService;
import com.CartoleriaPapyrus.ecommerce.utils.RequestModels.UserRequest;
import com.CartoleriaPapyrus.ecommerce.utils.ResponseModels.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    //GET ALL
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin
    public List<User> getAllUsers() {

        return userService.getAll();

    }

    //GET ALL PAGEABLE
    @GetMapping("/pageable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> getAllUsersPageable(Pageable p ) {

        Page<User> findAll = userService.getAllPaginate( p );

        if( findAll.hasContent() ) {
            return new ResponseEntity<>( findAll, HttpStatus.OK );
        } else {
            return new ResponseEntity<>( null, HttpStatus.NOT_FOUND );
        }

    }

    // GET BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<User> getById( @PathVariable Long id ) throws Exception {

        return new ResponseEntity<>(
                userService.getById( id ),
                HttpStatus.OK
        );
    }

    //GET BY USERNAME (UNIQUE)
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getByUsername( @PathVariable String username ) {

        return new ResponseEntity<>(
                userService.findByUsername( username ).isPresent() ?
                        userService.findByUsername( username ).get() : null,
                HttpStatus.OK
        );

    }

    // AGGIUNGI UN NUOVO UTENTE CON IL BODY COME RICHIESTA
    @PostMapping("/new-raw")
    public UserResponse create(@RequestBody UserRequest user ) {

        try {

            return userService.createAndSave( user );


        } catch( Exception e ) {

            log.error( e.getMessage() );

        }

        return null;

    }


    // UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<UserResponse> update( @RequestBody UserRequest user, @PathVariable("id") Long id ) {

        try {
            return new ResponseEntity<>( userService.updateResponse( user, id ),
                    HttpStatus.OK);


        } catch( Exception e ) {

            log.error( e.getMessage() );

        }
        return new ResponseEntity<>( HttpStatus.NOT_FOUND);
    }

    // ADD ROLE ADMIN
    @PutMapping("/{id}/add-role/{roleType}")
    @PreAuthorize("hasRole('ADMIN')")
    public void addRole(
            @PathVariable("id") Long id,
            @PathVariable("roleType") String roleType
    ) throws Exception {

        User u = userService.getById( id );

        if( roleType.equals( "ADMIN" ) ) {

            u.addRole( roleService.getByRole( RoleType.ROLE_ADMIN ) );

            userService.update( u );

        }

    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById( @PathVariable Long id ) {

        try {

            userService.delete( id );

        } catch( Exception e ) {

            log.error( e.getMessage() );

        }

    }
}
