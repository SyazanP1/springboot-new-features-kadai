package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;

@Controller
@RequestMapping("/houses")
public class HouseController {
	private final HouseRepository houseRepository;

	// レビュー
	private final ReviewRepository reviewRepository;

	// お気に入り登録
	private final FavoriteService favoriteService;

	public HouseController(HouseRepository houseRepository, ReviewRepository reviewRepository,
			FavoriteService favoriteService) {
		this.houseRepository = houseRepository;
		this.reviewRepository = reviewRepository;
		this.favoriteService = favoriteService;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "area", required = false) String area,
			@RequestParam(name = "price", required = false) Integer price,
			@RequestParam(name = "order", required = false) String order,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		Page<House> housePage;

		if (keyword != null && !keyword.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			} else {
				housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			}
		} else if (area != null && !area.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
			} else {
				housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
			}
		} else if (price != null) {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
			} else {
				housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
			}
		} else {
			if (order != null && order.equals("priceAsc")) {
				housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
			} else {
				housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);
			}
		}

		model.addAttribute("housePage", housePage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("area", area);
		model.addAttribute("price", price);
		model.addAttribute("order", order);

		return "houses/index";
	}

	// レビューをページネーション表示にするため、引数にpageableを追加
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id,
			@PageableDefault(page = 0, size = 6, sort = "updatedAt", direction = Direction.DESC) Pageable pageable,
			Model model,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		House house = houseRepository.getReferenceById(id);

		Page<Review> reviewPage;

		if (userDetailsImpl != null) {
			// 済ログインの場合
			Integer userId = userDetailsImpl.getUser().getId();
			reviewPage = reviewRepository.findAllByHouseIdAndUserIdNotOrderByUpdatedAtDesc(id, userId, pageable);

			// ログイン者自身のレビュー
			Review myReview = reviewRepository.findByHouseIdAndUserId(id, userId);
			if (myReview != null) {
				// レビューがあればビューに渡す
				model.addAttribute("myReview", myReview);
			}

			// お気に入り登録のチェック
			if (favoriteService.checkFavorite(userDetailsImpl.getUser(), house) == true) {

				// 登録済みの場合
				model.addAttribute("returnMessage", "登録済み");
			}

		} else {

			// 未ログインの場合
			reviewPage = reviewRepository.findAllByHouseIdOrderByUpdatedAtDesc(id, pageable);
		}

		model.addAttribute("house", house);
		model.addAttribute("reservationInputForm", new ReservationInputForm());
		// 登録済みレビューをビューに渡す
		model.addAttribute("reviewPage", reviewPage);

		return "houses/show";
	}

	// お気に入り登録or解除リンククリック時
	@GetMapping("/{id}/favorite")
	public String changeFavorite(@PathVariable(name = "id") Integer id,
			//			@PageableDefault(page = 0, size = 6, sort = "updatedAt", direction = Direction.DESC) Pageable pageable,
			Model model,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

		//			User user = userDetailsImpl.getUser();
		House house = houseRepository.getReferenceById(id);

		Boolean checked = favoriteService.checkFavorite(userDetailsImpl.getUser(), house);

		// 登録状況に応じて処理
		favoriteService.updateFavorite(checked, userDetailsImpl.getUser(), house);

		return "redirect:/houses/" + id;
	}
}
