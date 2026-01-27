package dev.tomislavmiksik.peak.core.util.extensions

import dev.tomislavmiksik.peak.core.domain.model.LengthUnit
import dev.tomislavmiksik.peak.core.domain.model.WeightUnit
import dev.tomislavmiksik.peak.core.domain.model.WeightUnit.KG
import dev.tomislavmiksik.peak.core.domain.model.WeightUnit.LBS


//TODO: revisit this

fun WeightUnit.toGrams(value: Float): Float {
    return when (this) {
        KG -> value * 1000f
        LBS -> value * 453.592f
    }
}

fun WeightUnit.toPounds(grams: Float): Float {
    return when (this) {
        KG -> grams / 1000f
        LBS -> grams / 453.592f
    }
}

fun LengthUnit.toCentimeters(value: Float): Float {
    return when (this) {
        LengthUnit.CM -> value
        LengthUnit.IN -> value * 2.54f
    }
}

fun LengthUnit.toInches(value: Float): Float {
    return when (this) {
        LengthUnit.CM -> value / 2.54f
        LengthUnit.IN -> value
    }
}