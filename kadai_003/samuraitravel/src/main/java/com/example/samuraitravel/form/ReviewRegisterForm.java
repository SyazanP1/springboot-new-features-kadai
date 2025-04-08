package com.example.samuraitravel.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRegisterForm {
	
	@NotNull
	private Integer id;
	
	@NotNull
	private Integer houseId;
	
	@NotNull
	private Integer userId;
	
	@NotNull(message = "点数をつけてください。")
	private Integer score;
	
	@NotBlank(message = "コメントを入力してください。")
	private String content;
	
	
	public ReviewRegisterForm(Integer houseId, Integer userId) {
		this.houseId = houseId;
		this.userId = userId;
	}
}
