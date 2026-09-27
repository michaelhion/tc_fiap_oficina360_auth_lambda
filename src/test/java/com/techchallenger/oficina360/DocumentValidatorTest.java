package com.techchallenger.oficina360;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentValidatorTest {

	private DocumentValidator validator;

	@BeforeEach
	void setup() {
		validator = new DocumentValidator();
	}

	@Test
	void shouldReturnFalseWhenDocumentIsNull() {

		assertFalse(validator.execute(null));
	}

	@Test
	void shouldValidateValidCpf() {

		assertTrue(validator.execute("52998224725"));
	}

	@Test
	void shouldReturnFalseForInvalidCpf() {

		assertFalse(validator.execute("52998224724"));
	}

	@Test
	void shouldReturnFalseForCpfWithRepeatedDigits() {

		assertFalse(validator.execute("11111111111"));
	}

	@Test
	void shouldReturnFalseForCpfWithInvalidLength() {

		assertFalse(validator.execute("123456789"));
	}

	@Test
	void shouldValidateValidCnpj() {

		assertTrue(validator.execute("11222333000181"));
	}

	@Test
	void shouldValidateValidCnpjWithMask() {

		assertTrue(validator.execute("11.222.333/0001-81"));
	}

	@Test
	void shouldReturnFalseForInvalidCnpj() {

		assertFalse(validator.execute("11222333000180"));
	}

	@Test
	void shouldReturnFalseForCnpjWithRepeatedDigits() {

		assertFalse(validator.execute("11111111111111"));
	}

	@Test
	void shouldReturnFalseForCnpjWithInvalidLength() {

		assertFalse(validator.execute("112223330001"));
	}

	@Test
	void shouldReturnFalseForCnpjWithInvalidCheckDigits() {

		assertFalse(validator.execute("112223330001AA"));
	}
}