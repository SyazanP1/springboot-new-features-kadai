package com.example.samuraitravel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer>{
	
	// 対象民宿の全レビューを更新日時降順で取得
	public Page<Review> findAllByHouseIdOrderByUpdatedAtDesc(Integer houseId, Pageable pageable);
	
	// 自レビュー以外の、対象民宿のレビューを更新日時降順で取得
	public Page<Review> findAllByHouseIdAndUserIdNotOrderByUpdatedAtDesc(Integer houseId, Integer userId, Pageable pageable);
	
	// houseId,userId指定でデータ取得
	public Review findByHouseIdAndUserId(Integer houseId, Integer userId);
}
