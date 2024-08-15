package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.dtos.response.DlcResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class DlcAssembler implements RepresentationModelAssembler<DlcResponse, EntityModel<DlcResponse>> {

    @Override
    public EntityModel<DlcResponse> toModel(DlcResponse entity) {
        return null;
    }
}
