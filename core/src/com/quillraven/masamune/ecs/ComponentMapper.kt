package com.quillraven.masamune.ecs

import com.badlogic.ashley.core.ComponentMapper
import com.quillraven.masamune.ecs.component.*

class ComponentMapper {
    internal val identify = ComponentMapper.getFor(IdentifyComponent::class.java)
    internal val box2D = ComponentMapper.getFor(Box2DComponent::class.java)
    internal val render = ComponentMapper.getFor(RenderComponent::class.java)
    internal val renderFlip = ComponentMapper.getFor(RenderFlipComponent::class.java)
    internal val move = ComponentMapper.getFor(MoveComponent::class.java)
    internal val transform = ComponentMapper.getFor(TransformComponent::class.java)
    internal val actionable = ComponentMapper.getFor(ActionableComponent::class.java)
    internal val inventory = ComponentMapper.getFor(InventoryComponent::class.java)
    internal val stackable = ComponentMapper.getFor(StackableComponent::class.java)
    internal val description = ComponentMapper.getFor(DescriptionComponent::class.java)
    internal val price = ComponentMapper.getFor(PriceComponent::class.java)
    internal val equipment = ComponentMapper.getFor(EquipmentComponent::class.java)
    internal val equipType = ComponentMapper.getFor(EquiptypeComponent::class.java)
}