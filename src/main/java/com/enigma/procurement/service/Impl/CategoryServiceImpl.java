package com.enigma.procurement.service.Impl;

import com.enigma.procurement.entity.Category;
import com.enigma.procurement.entity.Role;
import com.enigma.procurement.repository.CategoryRepository;
import com.enigma.procurement.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category findCategory(String name) {
        Optional<Category> optionalCategory = categoryRepository.findCategoryByName(name);

        if (optionalCategory.isPresent()) {
            return optionalCategory.get();
        } else {
            Category newCategory = Category.builder()
                    .name(name)
                    .build();
            return categoryRepository.save(newCategory);
        }
    }
}
