package com.example.digitalsignatureapi.controller;

import com.example.digitalsignatureapi.dto.DocumentRequest;
import com.example.digitalsignatureapi.dto.VerifyRequest;
import com.example.digitalsignatureapi.model.KeyPair;
import com.example.digitalsignatureapi.service.KeyPairService;
import com.example.digitalsignatureapi.service.SignatureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class KeyPairControllerTest {

    private MockMvc mockMvc;

    @Mock
    private KeyPairService keyPairService;

    @Mock
    private SignatureService signatureService;

    @InjectMocks
    private KeyPairController keyPairController;

    private static final String USER_ID = "testuser";
    private static final String DOCUMENT = "ZXN0byBlcyB1biBkb2N1bWVudG8K";
    private static final String SIGNATURE = "signature";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(keyPairController).build();
    }

    @Test
    public void testGenerateKeyPair() throws Exception {
        KeyPair mockKeyPair = new KeyPair();
        mockKeyPair.setUserId(USER_ID);

        when(keyPairService.generateKeyPair(anyString())).thenReturn(mockKeyPair);

        mockMvc.perform(post("/api/keys/generate?userId=" + USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKeyPair() throws Exception {
        KeyPair mockKeyPair = new KeyPair();
        mockKeyPair.setUserId(USER_ID);

        when(keyPairService.getKeyPair(anyString())).thenReturn(Optional.of(mockKeyPair));

        mockMvc.perform(get("/api/keys/" + USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void testSignDocument() throws Exception {
        when(signatureService.signDocument(anyString(), anyString())).thenReturn(SIGNATURE);

        DocumentRequest documentRequest = new DocumentRequest();
        documentRequest.setDocument(DOCUMENT);

        mockMvc.perform(post("/api/keys/sign?userId=" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"document\":\"" + DOCUMENT + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testVerifySignature() throws Exception {
        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setDocument(DOCUMENT);
        verifyRequest.setSignature(SIGNATURE);

        when(signatureService.verifySignature(anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/api/keys/verify?userId=" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"document\":\"" + DOCUMENT + "\", \"signature\":\"" + SIGNATURE + "\"}"))
                .andExpect(status().isOk());
    }
}
