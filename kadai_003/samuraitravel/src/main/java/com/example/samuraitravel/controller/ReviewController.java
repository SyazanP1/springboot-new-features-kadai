package com.example.samuraitravel.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping
public class ReviewController {
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;
	private final HouseRepository houseRepository;

	public ReviewController(ReviewRepository reviewRepository, ReviewService reviewService,
			HouseRepository houseRepository) {
		this.reviewRepository = reviewRepository;
		this.reviewService = reviewService;
		this.houseRepository = houseRepository;
	}

	@GetMapping("/review/{id}")
	public String display(@PathVariable(name = "id") Integer id, Model model,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

		User user = userDetailsImpl.getUser();
		Integer userId = user.getId();
		House house = new House();
		house = houseRepository.getReferenceById(id);

		model.addAttribute("house", house);
		model.addAttribute("reviewRegisterForm", new ReviewRegisterForm(id, userId));
		return "review/register";
	}

	@PostMapping("/review/{id}")
	public String regist(@PathVariable(name = "id") Integer id,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {

		House house = new House();

		house = houseRepository.getReferenceById(id);

		model.addAttribute("house", house);

		if (bindingResult.hasErrors()) {
			return "review/register";
		}
		User user = userDetailsImpl.getUser();

		reviewService.regist(id, user, reviewRegisterForm);

		redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");

		return "redirect:/houses/" + id;
	}

	@GetMapping("/review/edit/{id}")
	public String editDisplay(@PathVariable(name = "id") Integer houseId, Model model,
			@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

		User user = userDetailsImpl.getUser();
		Integer userId = user.getId();
		Review editReview = reviewRepository.findByHouseIdAndUserId(houseId, userId);

		ReviewEditForm reviewEditForm = new ReviewEditForm(editReview.getId(), houseId, userId, editReview.getScore(),
				editReview.getContent());
		model.addAttribute("reviewEditForm", reviewEditForm);

		return "review/edit";

	}

	@PostMapping("/review/edit")
	public String editUpdate(@ModelAttribute @Validated ReviewEditForm reviewEditForm,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model) {

		if (bindingResult.hasErrors()) {
			return "review/edit";
		}
		reviewService.update(reviewEditForm);

		redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
		model.addAttribute("reviewEditForm", reviewEditForm);
		return "redirect:/review/edit/" + reviewEditForm.getHouseId();
	}

	@PostMapping("review/delete/{id}/{myReviewId}")
	public String delete(@PathVariable(name = "id") Integer houseId,
			@PathVariable(name = "myReviewId") Integer myReviewId, RedirectAttributes redirectAttributes) {

		reviewRepository.deleteById(myReviewId);

		redirectAttributes.addFlashAttribute("deleteMessage", "レビューを削除しました。");
		return "redirect:/houses/" + houseId;
	}
}
