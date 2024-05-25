package com.overeasy.smartfitness.domain.foods.model

import kotlinx.serialization.Serializable

@Serializable
data class MenuData(
    val foodName: String,
    val taste: String,
    val mainIngredient: String,
    val secondaryIngredient: String,
    val cookMethod: String
)

/**
 * "foodName": "불고기",
 *       "taste": "담백한맛",
 *       "mainIngredient": "소고기",
 *       "secondaryIngredient": "설탕",
 *       "cookMethod": "굽기"
 */