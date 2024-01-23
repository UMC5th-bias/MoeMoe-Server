package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {

    private SecurityUtil securityUtil;

    @GetMapping("/limited")
    public void getLimitedProduct(HttpServletRequest request) {
        Member member = securityUtil.getUserFromHeader(request);



        if (member == null) {

        } else {

        }
    }
}
