package com.uade.tg.entities;


import com.uade.tg.enums.VoteType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "review_id"}))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;
}