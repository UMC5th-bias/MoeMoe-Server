package com.favoriteplace.app.community.controller.dto.guestbook;

import java.util.List;

public record GuestBookModifyRequestDto(String title, String content, List<String> hashtags) {
}
