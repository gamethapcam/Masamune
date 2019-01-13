package com.quillraven.masamune.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Align
import com.quillraven.masamune.MainGame

private const val TAG = "InventoryUI"

class InventoryUI constructor(game: MainGame) : Table(game.skin) {
    private val eventMgr = game.gameEventManager
    private val dragAndDrop = DragAndDrop()
    private val dragActor = Image()
    private val payload = DragAndDrop.Payload().apply {
        dragActor = this@InventoryUI.dragActor
        dragActor.setSize(50f, 50f)
    }

    internal var selectedSlotIdx = -1

    private val itemInfoImg = Image()
    private val itemInfoTitle = TextButton("", skin, "label")
    private val itemInfoDesc = TextButton("", skin, "label_small")
    private val contentTable = Table(skin)
    private val slotTable = Table(skin)

    init {
        setFillParent(true)

        // content table
        contentTable.background = skin.getDrawable("dialog_light")
        contentTable.pad(40f, 40f, 35f, 40f)

        // item info of content table
        contentTable.add(itemInfoImg).size(75f, 75f).padLeft(35f)
        val itemInfo = VerticalGroup()
        itemInfo.addActor(itemInfoTitle)
        itemInfo.addActor(itemInfoDesc)
        contentTable.add(itemInfo).expandX().fillX().padRight(35f).minHeight(105f).row()

        // slot table of content table
        slotTable.defaults().space(5f)
        contentTable.add(slotTable).padTop(10f).expand().fill().colspan(2)

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
        add(contentTable).colspan(2).padBottom(30f).padTop(-5f)

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

    private fun addInventorySlot() {
        val slot = WidgetGroup()
        // background graphic
        val imgSlot = Image(skin.getDrawable("slot_cursed"))
        imgSlot.setSize(60f, 60f)
        slot.addActor(imgSlot)
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

        slotTable.add(slot).expand().fill()
        if (slotTable.children.size % 10 == 0) {
            slotTable.row()
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
                imgItem.isVisible = false
                stackLbl.isVisible = false
                dragAndDrop.setDragActorPosition(dragActor.width * 0.5f, -dragActor.height * 0.5f)
                return payload
            }

            override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: DragAndDrop.Payload?, target: DragAndDrop.Target?) {
                imgItem.isVisible = true
                stackLbl.isVisible = true
            }
        })

        // drag target
        dragAndDrop.addTarget(object : DragAndDrop.Target(slot) {
            override fun drag(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int): Boolean {
                // valid drag only on slots that are different from the source slot
                return source.actor != actor
            }

            override fun drop(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int) {
                // valid drop --> add item to new slot and remove it from old slot
                eventMgr.dispatchInputItemMove(slotTable.children.indexOf(source.actor), slotTable.children.indexOf(actor))
            }
        })

        // show item info listener
        slot.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (imgItem.drawable != null) {
                    eventMgr.dispatchInputShowItem(slotTable.children.indexOf(slot))
                }
                return true
            }
        })
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

    fun updateItemSlot(slotIdx: Int, texture: String, amount: Int = 1) {
        if (slotIdx < 0 || slotIdx >= slotTable.children.size) {
            Gdx.app.error(TAG, "Trying to update item $texture to invalid slot $slotIdx")
            return
        }

        val slot = slotTable.children[slotIdx] as WidgetGroup
        val item = slot.children[1] as Image
        item.drawable = if (texture.isBlank()) null else skin.getDrawable(texture)
        val stack = slot.children[2] as Label
        stack.text.setLength(0)
        if (amount > 1) {
            stack.text.append("[BLACK]")
            stack.text.append(amount)
        }
        stack.invalidateHierarchy()
    }

    fun setInventorySize(size: Int) {
        // add missing slots
        while (slotTable.children.size < size) {
            addInventorySlot()
        }

        // remove slots if there are too much
        while (slotTable.children.size > size) {
            slotTable.removeActor(slotTable.children[slotTable.children.size - 1])
        }
    }
}