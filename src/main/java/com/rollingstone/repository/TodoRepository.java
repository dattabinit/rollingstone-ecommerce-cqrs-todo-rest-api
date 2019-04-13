package com.rollingstone.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.rollingstone.domain.Todo;

public interface TodoRepository extends PagingAndSortingRepository<Todo, Long>{

}
