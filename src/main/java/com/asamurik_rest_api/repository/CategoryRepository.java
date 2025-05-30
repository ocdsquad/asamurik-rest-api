package com.asamurik_rest_api.repository;

import com.asamurik_rest_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
