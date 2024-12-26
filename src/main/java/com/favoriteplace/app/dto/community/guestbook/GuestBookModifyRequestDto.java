package com.favoriteplace.app.dto.community.guestbook;

import java.util.List;

public record GuestBookModifyRequestDto(String title, String content, List<String> hashtags) {
}
