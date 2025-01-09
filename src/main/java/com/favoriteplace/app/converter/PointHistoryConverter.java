package com.favoriteplace.app.converter;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.domain.enums.PointType;
import com.favoriteplace.app.domain.item.PointHistory;

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
