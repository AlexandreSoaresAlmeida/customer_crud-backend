package com.apptek.customer.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.apptek.customer.dto.CredenciaisDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;
	private JWTUtil jwtUtil;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		setAuthenticationFailureHandler(new JWTAuthenticationFailureHandler());
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {
			CredenciaisDTO creds = new ObjectMapper().readValue(req.getInputStream(), CredenciaisDTO.class);

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					creds.getUsuario(),
					creds.getSenha(), 
					new ArrayList<>());

			Authentication auth = authenticationManager.authenticate(authToken);
			
			return auth;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

		String username = ((UserSS) auth.getPrincipal()).getUsername();
		String token = jwtUtil.generateToken(username);
		res.addHeader("Authorization", "Bearer " + token);
		res.addHeader("access-control-expose-headers", "Authorization");
		
		/*
		String responseToClient = token;

		res.setStatus(HttpServletResponse.SC_OK);
		res.getWriter().write(responseToClient);
		res.getWriter().flush();
		*/
		
		res.setStatus(200);
		res.setContentType("application/json");
		res.getWriter().append(json(token, auth, username));
		
		res.getWriter().flush();
	}
	
	private String json(String token, Authentication auth, String username) {
		long date = new Date().getTime();
		return "{\"timestamp\": " + date + ", " + "\"status\": 200, " + "\"error\": \"\", "
		+ "\"message\": \"Usuario autenticado\", " + "\"token\": \""+token+"\", "
		+ "\"username\": \"" + username + "\", "
		+ "\"roles\": \"" + auth.getAuthorities().toString() + "\", "
		+ "\"path\": \"/login\"}";
	}

	private class JWTAuthenticationFailureHandler implements AuthenticationFailureHandler {

		@Override
		public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException exception) throws IOException, ServletException {
			
			String username = "";  
			response.setStatus(401);
			response.setContentType("application/json");
			response.getWriter().append(json(username));
			
			response.getWriter().flush();
		}

		private String json(String username) {
			long date = new Date().getTime();
			return "{\"timestamp\": " + date + ", " + "\"status\": 401, " + "\"error\": \"Nao autorizado\", "
					+ "\"message\": \"Usuario ou senha invalidos\", " + "\"token\": \"\", "
					+ "\"username\": \"" + username + "\", "
			        + "\"roles\": \"\", " 
					+ "\"path\": \"/login\"}";
		}
	}

	public boolean validaCredenciais(String email, String senha) {
		try {
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, senha,
					new ArrayList<>());
			Authentication auth = authenticationManager.authenticate(authToken);
			return auth.isAuthenticated();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}