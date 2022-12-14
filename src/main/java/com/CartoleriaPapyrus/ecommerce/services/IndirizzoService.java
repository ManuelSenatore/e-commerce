package com.CartoleriaPapyrus.ecommerce.services;

import com.CartoleriaPapyrus.ecommerce.entities.Comune;
import com.CartoleriaPapyrus.ecommerce.entities.Indirizzo;
import com.CartoleriaPapyrus.ecommerce.entities.User;
import com.CartoleriaPapyrus.ecommerce.repositories.IndirizzoRepository;
import com.CartoleriaPapyrus.ecommerce.utils.RequestModels.IndirizzoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IndirizzoService {

    @Autowired
    IndirizzoRepository indirizzoRepository;

    @Autowired
    ComuneService comuneService;

    @Autowired
    UserService userService;

    // GET BY ID
    public Indirizzo getById(Long id ) throws Exception {
        Optional<Indirizzo> indirizzoLegale = indirizzoRepository.findById( id );
        if( indirizzoLegale.isEmpty() )
            throw new Exception( "Indirizzo not available" );
        return indirizzoLegale.get();
    }

    // GET BY USER ID

    public List<Indirizzo> getByUserId(Long userId ) throws Exception {
        return indirizzoRepository.findByUser(userId);
    }

    // GET ALL
    public Page<Indirizzo> getAllPaginate( Pageable p ) {
        return indirizzoRepository.findAll( p );
    }

    // CREATE
    public void save( Indirizzo indirizzo ) {
        indirizzoRepository.save( indirizzo );
    }

    // CREATE AND SAVE
    public Indirizzo createAndSave( IndirizzoRequest indirizzoRequest ) throws Exception {

        Indirizzo indirizzo = Indirizzo.builder()
                .via( indirizzoRequest.getVia() )
                .civico( indirizzoRequest.getCivico() )
                .cap( indirizzoRequest.getCap() )
                .comune( comuneService.getByNome( indirizzoRequest.getNomeComune() ) )
                .user( userService.getById((long) indirizzoRequest.getUser()) )
                .build();

        indirizzoRepository.save( indirizzo );
        return indirizzo;
    }

    // UPDATE
    public void update( Indirizzo indirizzo ) {
        indirizzoRepository.save( indirizzo );
    }

    // UPDATE AND SAVE
    public Indirizzo createAndUpdate( IndirizzoRequest indirizzoRequest ) throws Exception {
        Optional<Indirizzo> indirizzoFind = indirizzoRepository.findById( indirizzoRequest.getId() );
        if( indirizzoFind.isPresent() ) {
            Comune comuneFind = comuneService.getByNome( indirizzoRequest.getNomeComune() );

            Indirizzo indirizzo = Indirizzo.builder()
                    .indirizzoId( indirizzoRequest.getId() )
                    .via( indirizzoRequest.getVia() == null ? indirizzoFind.get().getVia() : indirizzoRequest.getVia() )
                    .civico( indirizzoRequest.getCivico() == 0 ? indirizzoFind.get().getCivico() : indirizzoRequest.getCivico() )
                    .cap( indirizzoRequest.getCap() == 0 ? indirizzoFind.get().getCap() : indirizzoRequest.getCap() )
                    .comune( comuneFind )
                    .build();
            indirizzoRepository.save( indirizzo );

            return indirizzo;
        } else {
            throw new Exception( "Indirizzo not available" );
        }


    }

    // DELETE
    public void delete( Long id ) throws Exception {
        Optional<Indirizzo> indirizzoLegale = indirizzoRepository.findById( id );
        if( indirizzoLegale.isPresent() ) {
            indirizzoRepository.delete( indirizzoLegale.get() );
        } else {
            throw new Exception( "Indirizzo Legale non trovato" );
        }
    }

}
