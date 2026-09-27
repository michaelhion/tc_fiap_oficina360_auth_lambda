package com.techchallenger.oficina360;

import static java.util.Objects.isNull;

public class DocumentValidator {
	
	public boolean execute(String document){
		if(isNull(document)){
			return false;
		}
		if(document.length() > 11){
			return isCnpjValid(document);
		}
		return isCpfValid(document);
	}

	private boolean isCnpjValid(String cnpj) {
		if (cnpj == null) {
			return false;
		}

		// 1. Limpa a máscara (mantém apenas números e letras maiúsculas)
		String cnpjLimpo = cnpj.replaceAll("[^0-9a-zA-Z]", "").toUpperCase();

		// 2. Verifica se possui exatamente 14 caracteres
		if (cnpjLimpo.length() != 14) {
			return false;
		}

		// 3. Elimina CNPJs inválidos conhecidos (todos os caracteres iguais)
		if (cnpjLimpo.matches("(.)\\1{13}")) {
			return false;
		}

		// 4. Os dois últimos caracteres (DVs) DEVEM ser estritamente numéricos
		if (!Character.isDigit(cnpjLimpo.charAt(12)) || !Character.isDigit(cnpjLimpo.charAt(13))) {
			return false;
		}

		try {
			// Cálculo do 1º Dígito Verificador
			int soma = 0;
			int peso = 2;
			for (int i = 11; i >= 0; i--) {
				// Regra oficial: valor do caractere na tabela ASCII - 48
				int valorMatematico = cnpjLimpo.charAt(i) - 48;
				soma += valorMatematico * peso;
				peso = (peso == 9) ? 2 : peso + 1;
			}
			int digito1 = 11 - (soma % 11);
			if (digito1 >= 10) digito1 = 0;

			// Cálculo do 2º Dígito Verificador
			soma = 0;
			peso = 2;
			for (int i = 12; i >= 0; i--) {
				int valorMatematico = cnpjLimpo.charAt(i) - 48;
				soma += valorMatematico * peso;
				peso = (peso == 9) ? 2 : peso + 1;
			}
			int digito2 = 11 - (soma % 11);
			if (digito2 >= 10) digito2 = 0;

			// 5. Valida se os DVs calculados batem com os informados no CNPJ
			return digito1 == (cnpjLimpo.charAt(12) - '0') &&
					digito2 == (cnpjLimpo.charAt(13) - '0');

		} catch (Exception e) {
			return false;
		}
	}

	private boolean isCpfValid(String cpf) {
		if (cpf == null) {
			return false;
		}

		// Limpa a máscara do CPF, mantendo apenas números
		String numeros = cpf.replaceAll("[^0-9]", "");

		// CPF deve ter exatamente 11 dígitos
		if (numeros.length() != 11) {
			return false;
		}

		// Elimina CPFs com todos os dígitos iguais (ex: 111.111.111-11)
		if (numeros.matches("(\\d)\\1{10}")) {
			return false;
		}

		try {
			// Cálculo do 1º Dígito Verificador
			int soma = 0;
			int peso = 10;
			for (int i = 0; i < 9; i++) {
				soma += (numeros.charAt(i) - '0') * peso;
				peso--;
			}
			int digito1 = 11 - (soma % 11);
			if (digito1 >= 10) digito1 = 0;

			// Cálculo do 2º Dígito Verificador
			soma = 0;
			peso = 11;
			for (int i = 0; i < 10; i++) {
				soma += (numeros.charAt(i) - '0') * peso;
				peso--;
			}
			int digito2 = 11 - (soma % 11);
			if (digito2 >= 10) digito2 = 0;

			// Valida se os DVs calculados batem com os informados
			return digito1 == (numeros.charAt(9) - '0') &&
					digito2 == (numeros.charAt(10) - '0');

		} catch (Exception e) {
			return false;
		}
	}
}
