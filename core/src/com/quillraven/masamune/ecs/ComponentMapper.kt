package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.quillraven.masamune.ecs.component.*

class ComponentMapper {
    internal val box2D = ComponentMapper.getFor(Box2DComponent::class.java)
    internal val render = ComponentMapper.getFor(RenderComponent::class.java)
    internal val renderFlip = ComponentMapper.getFor(RenderFlipComponent::class.java)
    internal val move = ComponentMapper.getFor(MoveComponent::class.java)
    internal val transform = ComponentMapper.getFor(TransformComponent::class.java)
    internal val character = ComponentMapper.getFor(CharacterComponent::class.java)
    internal val obj = ComponentMapper.getFor(ObjectComponent::class.java)
}