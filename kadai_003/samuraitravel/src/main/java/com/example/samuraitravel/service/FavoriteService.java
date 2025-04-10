package com.example.samuraitravel.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;

@Service
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;

	public FavoriteService(FavoriteRepository favoriteRepository) {
		this.favoriteRepository = favoriteRepository;
	}

	// お気に入り登録されているかどうかチェック
	// 未登録：false、登録済み：trueが戻り値となる
	@Transactional
	public Boolean checkFavorite(User user, House house) {

		Optional<Favorite> registedFavorite = favoriteRepository.findByUserIdAndHouseId(user, house);

		Integer id = registedFavorite.map(Favorite::getId).orElse(0);

		if (id == 0) {

			return false;

		} else {

			return true;
		}

	}

	@Transactional
	public String updateFavorite(Boolean checked, User user, House house) {

		if (checked == false) {

			// お気に入り登録がまだされていないので、登録処理を実行する
			Favorite favorite = new Favorite();
			favorite.setUserId(user);
			favorite.setHouseId(house);

			favoriteRepository.save(favorite);
			return "Insert";

		} else {

			// お気に入り登録がすでにされているので、削除処理を実行する
			Optional<Favorite> registedFavorite = favoriteRepository.findByUserIdAndHouseId(user, house);

			//			Integer id = registedFavorite.map(Favorite::getId).orElse(0);
			//
			//			favoriteRepository.deleteById(id);

			registedFavorite.ifPresent(x -> {

				favoriteRepository.deleteById(x.getId());

			});

			return "Delete";
		}

	}
	
//	@Transactional
//	public List<House> selectFavorites(User user) {
//		
//		List<Favorite> favoriteList = favoriteRepository.findAllByUserId(user);
//		List<House> houseList = new ArrayList<House>();
//	
//		for (int i = 0; i < favoriteList.size(); i++) {
//			houseList.add(favoriteList.get(i).getHouseId());
//		}
//		
//		return houseList;
//		
//	}

	@Transactional
	public Page<House> selectFavorites(User user, Pageable pageable) {
		
		Page<Favorite> favoritePage = favoriteRepository.findAllByUserId(user, pageable);
		
		return favoritePage.map(Favorite::getHouseId);
		
	}
}
