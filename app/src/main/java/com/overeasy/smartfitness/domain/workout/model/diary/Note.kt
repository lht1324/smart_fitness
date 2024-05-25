package com.overeasy.smartfitness.domain.workout.model.diary

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val noteId: Int,
    val userId: Int,
    val workoutDate: List<Int>,
    val totalScore: Int,
    val totalKcal: Int?,
    val totalPerfect: Int,
    val totalGood: Int,
    val totalBad: Int
)
/*
    @Id
    @Column(name = "note_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "workout_date")
    @CreatedDate
    private LocalDate workoutDate;
    @Column(name = "total_score")
    private int totalScore;
    @Column(name = "total_kcal")
    private int totalKcal;
    @Column(name = "total_perfect")
    private int totalPerfect;
    @Column(name = "total_good")
    private int totalGood;
    @Column(name = "total_bad")
    private int totalBad;
 */