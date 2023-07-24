package com.enigma.procurement.service;

import com.enigma.procurement.models.request.AuthRequest;
import com.enigma.procurement.models.request.SignInResponse;
import com.enigma.procurement.models.request.SignUpResponse;

public interface AuthService {

    SignUpResponse registerAdmin(AuthRequest request);

    SignUpResponse registerVendor(AuthRequest request);
    SignUpResponse superior(AuthRequest request);
    SignInResponse login(AuthRequest request);

}
