package com.example.steamreplica;

import com.example.steamreplica.controller.assembler.DiscountAssembler;
import com.example.steamreplica.controller.assembler.DlcAssembler;
import com.example.steamreplica.controller.assembler.GameAssembler;
import com.example.steamreplica.dtos.request.DlcRequest;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Full;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.DLC.DLCImage;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.repository.DlcRepository;
import com.example.steamreplica.service.DlcImageService;
import com.example.steamreplica.service.DlcService;
import com.example.steamreplica.service.GameService;
import com.example.steamreplica.util.StaticHelper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DlcTest {
    @Mock
    DlcRepository dlcRepository;
    @Mock
    GameService gameService;
    @Mock
    DlcImageService dlcImageService;
    @Mock
    DlcAssembler dlcAssembler;
    @Mock
    GameAssembler gameAssembler;
    @Mock
    Authentication authentication;
    @Mock
    DiscountAssembler discountAssembler;

    @InjectMocks
    DlcService dlcService;

    // Successfully adds a new DLC when all inputs are valid
    @Test
    public void test_add_dlc_success() {
        // Arrange
        DlcRequest dlcRequest = new DlcRequest("New DLC", "Description", Set.of(1L, 2L), ZonedDateTime.now(), BigDecimal.valueOf(10.0), 1L, "thumbnail");
        Game game = new Game();
        game.setId(1L);
        List<DLCImage> dlcImages = List.of(new DLCImage(), new DLCImage());
        
        DLC newDlc = new DLC(dlcRequest.getDlcName(), dlcRequest.getDlcDescription(), dlcRequest.getDlcBasePrice(), StaticHelper.convertToBlob(dlcRequest.getDlcThumbnail()), dlcRequest.getReleaseDate());
        newDlc.setId(123L);
        newDlc.setGame(game);
        newDlc.setDlcImages(new HashSet<>(dlcImages));

        when(dlcRepository.findDLCByDlcName(dlcRequest.getDlcName())).thenReturn(Optional.empty());
        when(gameService.getGameById_entity(dlcRequest.getOwningGameId())).thenReturn(game);
        when(dlcImageService.getDlcImagesByIds_entity(dlcRequest.getDlcImages())).thenReturn(dlcImages);
        when(dlcRepository.save(any(DLC.class))).thenReturn(newDlc);
        when(dlcAssembler.toModel(any(DlcResponse_Full.class), eq(authentication))).thenReturn(EntityModel.of(new DlcResponse_Full(newDlc)));
        when(gameService.getGameById_entity(1L)).thenReturn(newDlc.getGame());
        
        // Act
        EntityModel<DlcResponse_Full> response = dlcService.addDlc(dlcRequest, authentication);

        // Assert
        assertNotNull(response);

        ArgumentCaptor<DLC> dlcArgumentCaptor = ArgumentCaptor.forClass(DLC.class);
        verify(dlcRepository).save(dlcArgumentCaptor.capture());
        DLC captorValue = dlcArgumentCaptor.getValue();
        
        assertEquals(dlcRequest.getDlcName(), captorValue.getDlcName());
        assertEquals(dlcRequest.getDlcDescription(), captorValue.getDlcDescription());
        assertEquals(dlcRequest.getDlcBasePrice(), captorValue.getDlcBasePrice());
        assertEquals(StaticHelper.convertToBlob(dlcRequest.getDlcThumbnail()), captorValue.getDlcThumbnail());
        assertEquals(dlcRequest.getReleaseDate(), captorValue.getReleaseDate());
        assertEquals(game, captorValue.getGame());
        assertEquals(new HashSet<>(dlcImages), captorValue.getDlcImages());
    }
}
