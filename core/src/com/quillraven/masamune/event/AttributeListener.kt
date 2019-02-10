package com.quillraven.masamune.event

import com.badlogic.ashley.core.Entity
import com.quillraven.masamune.model.EAttributeType

interface AttributeListener {
    fun attributeUpdated(entity: Entity, type: EAttributeType, newValue: Float)
}