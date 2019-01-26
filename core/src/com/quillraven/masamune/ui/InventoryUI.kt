package com.quillraven.masamune.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Align
import com.quillraven.masamune.MainGame
import com.quillraven.masamune.model.EEquipType

private const val TAG = "InventoryUI"

class InventoryUI constructor(game: MainGame) : Table(game.skin) {
    private val eventMgr = game.gameEventManager
    private val dragAndDrop = DragAndDrop()
    private val dragActor = Image()
    private val invalidDragActor = Image()
    private val payload = DragAndDrop.Payload().apply {
        dragActor = this@InventoryUI.dragActor
        dragActor.setSize(50f, 50f)
        invalidDragActor = this@InventoryUI.invalidDragActor
        invalidDragActor.setSize(50f, 50f)
        invalidDragActor.setColor(1f, 0f, 0f, 1f)
    }

    internal var selectedSlotIdx = -1

    private val itemInfoImg = Image()
    private val itemInfoTitle = TextButton("", skin, "label")
    private val itemInfoDesc = TextButton("", skin, "label_small")
    private val contentTable = Table(skin)
    private val inventorySlotTable = Table(skin)
    private val equipSlotTable = Table(skin)

    init {
        setFillParent(true)

        // content table
        contentTable.background = skin.getDrawable("dialog_light")
        contentTable.pad(40f, 40f, 35f, 0f)

        // item info of content table
        contentTable.add(itemInfoImg).size(75f, 75f).padLeft(35f)
        val itemInfo = VerticalGroup()
        itemInfo.addActor(itemInfoTitle)
        itemInfo.addActor(itemInfoDesc)
        contentTable.add(itemInfo).expandX().fillX().padRight(35f).minHeight(105f).row()

        // slot table of content table
        inventorySlotTable.defaults().space(5f)
        contentTable.add(inventorySlotTable).padTop(10f).expand().fill().colspan(2)

        // add title area and content to table
        // title area
        val label = TextButton("[DIALOG_TITLE_LIGHT]${game.resourceBundle.get("Inventory")}", skin, "dialog_title")
        val imgSkull = Image(skin.getDrawable("skull"))
        val btnClose = ImageButton(skin.getDrawable("btn_close"))
        imgSkull.setScale(0.75f, 0.75f)
        add(imgSkull).padBottom(-25f).padLeft(70f).colspan(2).row()
        add(label).size(Value.percentWidth(0.6f, this), Value.prefHeight).height(130f).right().padLeft(90f)
        add(btnClose).left().padLeft(-5f).row()
        imgSkull.toFront()
        // content
        add(contentTable).padBottom(30f).padTop(-5f).colspan(2)

        // equipment table
        equipSlotTable.background = skin.getDrawable("dialog_light")
        equipSlotTable.defaults().pad(5f)
        equipSlotTable.add(TextButton("[DIALOG_TITLE_LIGHT]${game.resourceBundle.get("Equipment")}", skin, "label")).expandX().fillX().pad(40f, 0f, 0f, 0f).colspan(3).row()
        // helmet
        addInventorySlot(equipSlotTable, "slot_helmet", EEquipType.HELMET).colspan(3).padLeft(94f).padTop(20f).row()
        // weapon, armor, shield
        addInventorySlot(equipSlotTable, "slot_weapon", EEquipType.WEAPON).padLeft(25f)
        addInventorySlot(equipSlotTable, "slot_armor", EEquipType.ARMOR).padLeft(-26f)
        addInventorySlot(equipSlotTable, "slot_shield", EEquipType.SHIELD).padLeft(-23f).row()
        // gloves, boots
        addInventorySlot(equipSlotTable, "slot_gloves", EEquipType.GLOVES).padLeft(62f)
        addInventorySlot(equipSlotTable, "slot_boots", EEquipType.BOOTS).row()
        // ring, necklace
        addInventorySlot(equipSlotTable, "slot_ring", EEquipType.RING).padLeft(62f).padBottom(50f)
        addInventorySlot(equipSlotTable, "slot_amulet", EEquipType.NECKLACE).padBottom(50f).row()
        add(equipSlotTable).width(240f).height(400f).pad(0f, -60f, 35f, 40f)

        // move little bit to the right to center between touchpad and action button
        padLeft(60f)

        // close button hides the inventory UI
        btnClose.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                // clear item info and hide inventory UI
                updateItemInfo(-1, "", "", "")
                stage.root.removeActor(this@InventoryUI)
                return true
            }
        })
    }

    private fun addInventorySlot(slotTable: Table, slotInfoTexture: String = "", userObject: Any? = null): Cell<WidgetGroup> {
        val slot = WidgetGroup()
        // background graphic
        val imgSlot = Image(skin.getDrawable("slot_cursed"))
        imgSlot.setSize(60f, 60f)
        slot.addActor(imgSlot)
        slot.userObject = userObject
        // item graphic
        val imgItem = Image()
        imgItem.setSize(50f, 50f)
        imgItem.setPosition(10f, 10f)
        imgItem.scaleBy(-0.25f)
        slot.addActor(imgItem)
        // item stack size
        val stackLbl = Label("", skin)
        stackLbl.setAlignment(Align.bottomRight)
        stackLbl.scaleBy(0.75f)
        stackLbl.setPosition(53f, 1f)
        slot.addActor(stackLbl)
        if (!slotInfoTexture.isBlank()) {
            // slot info graphic. used f.e. for equipment slots
            val imgSlotInfo = Image(skin.getDrawable(slotInfoTexture))
            imgSlotInfo.setSize(50f, 50f)
            imgSlotInfo.setPosition(10f, 10f)
            imgSlotInfo.scaleBy(-0.25f)
            slot.addActor(imgSlotInfo)
        }

        // drag source
        dragAndDrop.addSource(object : DragAndDrop.Source(slot) {
            override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): DragAndDrop.Payload? {
                if (imgItem.drawable == null) {
                    // no item in slot -> ignore drag
                    return null
                }

                // add item as drag actor --> item gets visually removed from slot but the real remove is not triggered yet
                dragActor.drawable = imgItem.drawable
                dragActor.userObject = imgItem.userObject
                invalidDragActor.drawable = imgItem.drawable
                imgItem.isVisible = false
                stackLbl.isVisible = false
                if (slot.children.size >= 4) {
                    slot.children[3].isVisible = true
                }
                dragAndDrop.setDragActorPosition(dragActor.width * 0.5f, -dragActor.height * 0.5f)
                return payload
            }

            override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: DragAndDrop.Payload?, target: DragAndDrop.Target?) {
                imgItem.isVisible = true
                stackLbl.isVisible = true
                if (slot.children.size >= 4) {
                    slot.children[3].isVisible = imgItem.drawable == null
                }
            }
        })

        // drag target
        dragAndDrop.addTarget(object : DragAndDrop.Target(slot) {
            override fun drag(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int): Boolean {
                val targetTable = actor.parent
                if (targetTable == equipSlotTable) {
                    // can only drop on valid type
                    return actor.userObject == dragActor.userObject
                }
                return true
            }

            override fun drop(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int) {
                if (source.actor == actor) {
                    // same slot nothing to do
                    return
                }

                val sourceTable = source.actor.parent
                val targetTable = actor.parent

                if (sourceTable == inventorySlotTable && targetTable == inventorySlotTable) {
                    // move item within inventory
                    eventMgr.dispatchInputItemMove(sourceTable.children.indexOf(source.actor), sourceTable.children.indexOf(actor))
                } else if (sourceTable == inventorySlotTable && targetTable == equipSlotTable) {
                    // equip item
                    eventMgr.dispatchInputItemEquip(sourceTable.children.indexOf(source.actor), actor.userObject as EEquipType)
                } else if (targetTable == inventorySlotTable && sourceTable == equipSlotTable) {
                    // unequip item
                    eventMgr.dispatchInputItemUnequip(inventorySlotTable.children.indexOf(actor), source.actor.userObject as EEquipType)
                }
            }
        })

        // show item info listener
        slot.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (imgItem.drawable != null) {
                    if (slotTable == inventorySlotTable) {
                        eventMgr.dispatchInputShowInventoryItem(slotTable.children.indexOf(slot))
                    } else {
                        eventMgr.dispatchInputShowEquipmentItem(slot.userObject as EEquipType)
                    }
                }
                return true
            }
        })

        return slotTable.add(slot).expand().fill()
    }

    fun updateItemInfo(slotIdx: Int, name: String, description: String, texture: String) {
        selectedSlotIdx = slotIdx
        itemInfoTitle.label.text.apply {
            setLength(0)
            append("[HIGHLIGHT]")
            append(name)
        }
        itemInfoTitle.label.invalidateHierarchy()
        itemInfoDesc.label.text.apply {
            setLength(0)
            append("[BLACK]")
            append(description)
        }
        itemInfoDesc.label.invalidateHierarchy()
        if (texture.isBlank()) {
            itemInfoImg.drawable = null
        } else {
            itemInfoImg.drawable = skin.getDrawable(texture)
        }
    }

    private fun updateItemSlot(slot: WidgetGroup, texture: String, amount: Int, equipType: EEquipType?) {
        // update slot graphic
        val item = slot.children[1] as Image
        if (texture.isBlank()) {
            item.drawable = null
            item.userObject = null
        } else {
            item.drawable = skin.getDrawable(texture)
            item.userObject = equipType
        }

        // update stack size information
        val stack = slot.children[2] as Label
        stack.text.setLength(0)
        if (amount > 1) {
            stack.text.append("[BLACK]")
            stack.text.append(amount)
        }
        stack.invalidateHierarchy()

        if (slot.children.size >= 4) {
            // update slot information image (visible if no item in slot)
            slot.children[3].isVisible = item.drawable == null
        }
    }

    fun updateItemSlot(slotIdx: Int, texture: String, amount: Int = 1, equipType: EEquipType?) {
        if (slotIdx < 0 || slotIdx >= inventorySlotTable.children.size) {
            Gdx.app.error(TAG, "Trying to update item $texture to invalid slot $slotIdx")
            return
        }

        updateItemSlot(inventorySlotTable.children[slotIdx] as WidgetGroup, texture, amount, equipType)
    }

    fun setInventorySize(size: Int) {
        // add missing slots
        while (inventorySlotTable.children.size < size) {
            if (inventorySlotTable.children.size % 10 == 9) {
                addInventorySlot(inventorySlotTable).padRight(30f).row()
            } else {
                addInventorySlot(inventorySlotTable)
            }
        }

        // remove slots if there are too much
        while (inventorySlotTable.children.size > size) {
            inventorySlotTable.removeActor(inventorySlotTable.children[inventorySlotTable.children.size - 1])
        }
    }

    fun updateEquipSlot(type: EEquipType, texture: String) {
        for (slot in equipSlotTable.children) {
            if (slot.userObject == type) {
                updateItemSlot(slot as WidgetGroup, texture, 1, if (texture.isBlank()) null else type)
                break
            }
        }
    }
}