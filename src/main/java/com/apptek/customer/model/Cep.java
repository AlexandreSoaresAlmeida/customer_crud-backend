package com.apptek.customer.model;

import lombok.Data;

@Data
public class Cep {
	private String cep;
	private String logradouro;
	private String complemento;
	private String bairro;
	private String localidade;
	private String uf;
	private String ibge;
	private String gia;
	private String ddd;
	private String siafi;
	
	public Cep() {
		
	}
	
	public Cep(String cep, String logradouro, String complemento, String bairro, String localidade,
			   String uf, String ibge, String gia, String ddd, String siafi) {
		super();
		this.cep = cep;
		this.logradouro = logradouro;
		this.complemento = complemento;
		this.bairro = bairro;
		this.localidade = localidade;
		this.uf = uf;
		this.ibge = ibge;
		this.gia = gia;
		this.ddd = ddd;
		this.siafi = siafi;
	}
}
