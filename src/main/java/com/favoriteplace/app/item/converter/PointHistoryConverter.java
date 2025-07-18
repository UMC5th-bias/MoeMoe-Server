package com.favoriteplace.app.item.converter;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.domain.enums.PointType;
import com.favoriteplace.app.item.domain.PointHistory;

import java.time.LocalDateTime;

public class PointHistoryConverter {
    public static PointHistory toPointHistory(Member member, Long point, PointType type) {
        member.updatePoint(point);
        return PointHistory.builder()
                .member(member)
                .point(point)
                .dealtAt(LocalDateTime.now())
                .pointType(type)
                .build();
    }
}
