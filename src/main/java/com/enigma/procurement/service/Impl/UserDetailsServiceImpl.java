package com.enigma.procurement.service.Impl;

import com.enigma.procurement.entity.Role;
import com.enigma.procurement.entity.UserCredential;
import com.enigma.procurement.entity.UserDetailsImpl;
import com.enigma.procurement.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserCredentialRepository userCredentialRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserCredential userCredential = userCredentialRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found!"));
        Role role = userCredential.getRole();
        Collection<SimpleGrantedAuthority> grantedAuthority = Collections.singleton(new SimpleGrantedAuthority(role.getRole().name()));

        return UserDetailsImpl.builder()
                .email(userCredential.getEmail())
                .password(userCredential.getPassword())
                .authorities(grantedAuthority)
                .build();
    }
}
