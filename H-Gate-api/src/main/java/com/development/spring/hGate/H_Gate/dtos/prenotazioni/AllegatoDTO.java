package com.development.spring.hGate.H_Gate.dtos.prenotazioni;

import com.development.spring.hGate.H_Gate.dtos.UserDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AllegatoDTO {

    private String uuid;
    private String nomeFile;
    private String tipoFile;
    private String mimeType;
    private Long sizeBytes;
    private String url;
    private String storagePath;
    private String descrizione;
    private String hashFile;
    private LocalDateTime uploadedAt;
    private PrenotazioneDTO prenotazione;
}
