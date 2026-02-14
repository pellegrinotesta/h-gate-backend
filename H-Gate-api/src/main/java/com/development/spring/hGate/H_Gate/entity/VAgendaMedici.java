package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.libs.data.models.IdentifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "v_agenda_medici")
public class VAgendaMedici implements IdentifiableEntity<Integer> {

    @Id
    @Column(name = "evento_id")
    private Integer eventoId;

    @Column(name = "inizio")
    private LocalDateTime inizio;

    @Column(name = "fine")
    private LocalDateTime fine;

    // Medico
    @Column(name = "medico_id")
    private Integer medicoId;

    @Column(name = "medico_nome")
    private String medicoNome;

    @Column(name = "specializzazione")
    private String specializzazione;

    // Paziente
    @Column(name = "paziente_id")
    private Integer pazienteId;

    @Column(name = "paziente_nome")
    private String pazienteNome;

    // Tutore (eventuale)
    @Column(name = "tutore_nome")
    private String tutoreNome;

    @Column(name = "tutore_telefono")
    private String tutoreTelefono;

    // Prenotazione
    @Column(name = "tipo_visita")
    private String tipoVisita;

    @Column(name = "stato")
    private String stato;

    @Column(name = "is_prima_visita")
    private Boolean isPrimaVisita;

    @Column(name = "note_paziente")
    private String notePaziente;

    @Column(name = "promemoria_inviato")
    private Boolean promemoriaInviato;

    @Column(name = "conferma_inviata")
    private Boolean confermaInviata;

    @Override
    public Integer getId() {
        return eventoId;
    }

    @Override
    public void setId(Integer paramId) {
        this.eventoId = paramId;
    }
}
