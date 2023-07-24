package com.enigma.procurement.service;

import com.enigma.procurement.entity.Admin;
import com.enigma.procurement.models.request.AdminRequest;
import com.enigma.procurement.models.response.AdminResponse;

public interface AdminService {


    Admin create(Admin admin);
    Admin findById(String id);
    String getEmailById(String id);
    AdminResponse update(AdminRequest request);


}
