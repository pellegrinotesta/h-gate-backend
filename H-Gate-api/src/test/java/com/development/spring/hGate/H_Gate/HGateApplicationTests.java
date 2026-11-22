package com.development.spring.hGate.H_Gate;

import com.development.spring.hGate.H_Gate.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Il contesto parte e lo scenario di test viene creato (stesso contesto degli altri test). */
class HGateApplicationTests extends IntegrationTestBase {

	@Test
	void contextLoads() {
		assertThat(prenotazioneA.getId()).isNotNull();
	}
}
