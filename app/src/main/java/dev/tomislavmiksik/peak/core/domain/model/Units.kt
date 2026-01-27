package dev.tomislavmiksik.peak.core.domain.model

interface Unit

enum class WeightUnit : Unit {
    KG,
    LBS;
}

enum class LengthUnit : Unit {
    CM,
    IN
}

enum class PercentUnit : Unit {
    PERCENT
}