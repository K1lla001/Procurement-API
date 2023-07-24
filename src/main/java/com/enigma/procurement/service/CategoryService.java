package com.enigma.procurement.service;

import com.enigma.procurement.entity.Category;

import java.util.Optional;

public interface CategoryService {

    Category findCategory(String name);

}
