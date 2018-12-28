package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.quillraven.masamune.ecs.component.Box2DComponent
import com.quillraven.masamune.ecs.component.MoveComponent
import com.quillraven.masamune.ecs.component.RenderComponent
import com.quillraven.masamune.ecs.component.RenderFlipComponent

class ComponentMapper {
    internal val box2D = ComponentMapper.getFor(Box2DComponent::class.java)
    internal val render = ComponentMapper.getFor(RenderComponent::class.java)
    internal val renderFlip = ComponentMapper.getFor(RenderFlipComponent::class.java)
    internal val move = ComponentMapper.getFor(MoveComponent::class.java)
}