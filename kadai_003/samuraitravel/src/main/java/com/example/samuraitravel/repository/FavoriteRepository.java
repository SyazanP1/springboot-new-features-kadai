package com.example.samuraitravel.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Favorite;
//import com.example.samuraitravel.entity.FavoritePrimaryKey;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{

	public Optional<Favorite> findByUserIdAndHouseId(User user, House house);
	
//	public List<Favorite> findAllByUserId(User user);
	public Page<Favorite> findAllByUserId(User user, Pageable pageable);
	
}
