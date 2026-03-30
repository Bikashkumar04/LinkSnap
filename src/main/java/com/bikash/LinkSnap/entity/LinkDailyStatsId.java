package com.bikash.LinkSnap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class LinkDailyStatsId implements Serializable {

    @Column(name = "link_id", nullable = false)
    private Long linkId;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;
}
