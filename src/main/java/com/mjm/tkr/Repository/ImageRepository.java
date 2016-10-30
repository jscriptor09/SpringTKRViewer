package com.mjm.tkr.Repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mjm.tkr.domain.Image;

public interface ImageRepository extends PagingAndSortingRepository<Image, Long>{
	public Image findByName(String name);
}
