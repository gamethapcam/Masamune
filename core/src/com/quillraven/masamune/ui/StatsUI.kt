package com.quillraven.masamune.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.quillraven.masamune.MainGame


class StatsUI constructor(game: MainGame) : Table(game.skin) {
    private val resourceBundle = game.resourceBundle

    private val contentTable = Table(skin)
    private val skillTable = Table(skin)
    private val attributeTable = Table(skin)

    private val skillInfoImg = Image()
    private val skillInfoTitle = TextButton("", skin, "label")
    private val skillInfoDesc = TextButton("", skin, "label_small")

    init {
        setFillParent(true)

        // content table
        contentTable.background = skin.getDrawable("dialog_light")
        contentTable.pad(40f, 40f, 35f, 0f)

        // add title area and content to table
        // title area
        val label = TextButton("[DIALOG_TITLE_LIGHT]${game.resourceBundle.get("Stats")}", skin, "dialog_title")
        val imgSkull = Image(skin.getDrawable("skull"))
        val btnClose = ImageButton(skin.getDrawable("btn_close"))
        imgSkull.setScale(0.75f, 0.75f)
        add(imgSkull).padBottom(-25f).padLeft(70f).colspan(2).row()
        add(label).size(Value.percentWidth(0.6f, this), Value.prefHeight).height(130f).right().padLeft(90f)
        add(btnClose).left().padLeft(-5f).row()
        imgSkull.toFront()
        // content
        add(contentTable).padBottom(30f).padTop(-5f).colspan(2)

        // info of content table
        val infoTable = Table()
        val itemInfo = VerticalGroup()
        itemInfo.addActor(skillInfoTitle)
        itemInfo.addActor(skillInfoDesc)
        infoTable.add(skillInfoImg).size(75f, 75f)
        infoTable.add(itemInfo).minHeight(105f)
        contentTable.add(infoTable).expandX().fillX().colspan(2).padTop(5f).padRight(290f).row()
        initSkillTable()
        initAttributeTable()

        // close button hides the inventory UI
        btnClose.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                stage.root.removeActor(this@StatsUI)
                return true
            }
        })
    }

    private fun createSkillSlot(skillGraphic: String): Actor {
        val slot = WidgetGroup()
        val imgSlot = Image(skin.getDrawable("slot_cursed"))
        imgSlot.setSize(60f, 60f)
        slot.addActor(imgSlot)
        if (!skillGraphic.isBlank()) {
            val imgSkill = Image(skin.getDrawable(skillGraphic))
            imgSkill.setSize(50f, 50f)
            imgSkill.setPosition(5f, 5f)
            imgSkill.setColor(1f, 1f, 1f, 0.25f)
            slot.addActor(imgSkill)

            slot.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    updateSkillInfo(skillGraphic, "Skill1", "Skill description1")
                }
            })
        } else {
            slot.isVisible = false
        }

        return slot
    }

    private fun updateSkillInfo(skillGraphic: String, skillName: String, skillDescription: String) {
        skillInfoImg.drawable = skin.getDrawable(skillGraphic)
        skillInfoTitle.setText(skillName)
        skillInfoDesc.setText(skillDescription)
    }

    private fun initSkillTable() {
        skillTable.add(TextButton("[DIALOG_TITLE_LIGHT]${resourceBundle.get("Skill")}", skin, "label")).expandX().fillX().colspan(3).row()
        skillTable.add(createSkillSlot("skill_0")).expand().fill()
        skillTable.add(createSkillSlot("skill_7")).expand().fill()
        skillTable.add(createSkillSlot("skill_3")).expand().fill().row()
        skillTable.add(createSkillSlot("skill_1")).expand().fill()
        skillTable.add(createSkillSlot("skill_8")).expand().fill()
        skillTable.add(createSkillSlot("skill_5")).expand().fill().row()
        skillTable.add(createSkillSlot("")).expand().fill()
        skillTable.add(createSkillSlot("")).expand().fill()
        skillTable.add(createSkillSlot("skill_6")).expand().fill()

        contentTable.add(skillTable).expand().fill().top()
    }

    private fun createAttributeLabel(label: String, currentValue: Int, maxValue: Int = 0): TextButton {
        val txtButton = if (maxValue > 0) TextButton("$label: $currentValue / $maxValue", skin, "label") else TextButton("$label: $currentValue", skin, "label")
        txtButton.label.setAlignment(Align.left)
        return txtButton
    }

    private fun initAttributeTable() {
        attributeTable.add(TextButton("[DIALOG_TITLE_LIGHT]${resourceBundle.get("Stat")}", skin, "label")).expandX().fillX().row()
        attributeTable.add(createAttributeLabel("[GOLD]${resourceBundle.get("Experience")}[WHITE]", 0, 350)).expandX().fillX().padTop(20f).padLeft(100f).row()
        attributeTable.add(createAttributeLabel("[GREEN]${resourceBundle.get("Life")}[WHITE]", 100, 100)).expandX().fillX().padLeft(100f).row()
        attributeTable.add(createAttributeLabel("[SKY]${resourceBundle.get("Mana")}[WHITE]", 50, 50)).expandX().fillX().padLeft(100f).row()
        attributeTable.add(createAttributeLabel("[RED]${resourceBundle.get("Strength")}[WHITE]", 25)).expandX().fillX().padLeft(100f).row()
        attributeTable.add(createAttributeLabel("[LIME]${resourceBundle.get("Agility")}[WHITE]", 25)).expandX().fillX().padLeft(100f).row()
        attributeTable.add(createAttributeLabel("[BLUE]${resourceBundle.get("Intelligence")}[WHITE]", 25)).expandX().fillX().padLeft(100f).row()

        contentTable.add(attributeTable).expand().fill().top()
    }

    fun updateExperience(xp: Float, requiredXP: Float) {
        (attributeTable.children[1] as TextButton).setText("[GOLD]${resourceBundle.get("Experience")}[WHITE]: ${xp.toInt()} / ${requiredXP.toInt()}")
    }

    fun updateLife(life: Float, maxLife: Float) {
        (attributeTable.children[2] as TextButton).setText("[GREEN]${resourceBundle.get("Life")}[WHITE]: ${life.toInt()} / ${maxLife.toInt()}")
    }

    fun updateMana(mana: Float, maxMana: Float) {
        (attributeTable.children[3] as TextButton).setText("[SKY]${resourceBundle.get("Mana")}[WHITE]: ${mana.toInt()} / ${maxMana.toInt()}")
    }

    fun updateStrength(strength: Float) {
        (attributeTable.children[4] as TextButton).setText("[RED]${resourceBundle.get("Strength")}[WHITE]: ${strength.toInt()}")
    }

    fun updateAgility(agility: Float) {
        (attributeTable.children[5] as TextButton).setText("[LIME]${resourceBundle.get("Agility")}[WHITE]: ${agility.toInt()}")
    }

    fun updateIntelligence(intelligence: Float) {
        (attributeTable.children[6] as TextButton).setText("[BLUE]${resourceBundle.get("Intelligence")}[WHITE]: ${intelligence.toInt()}")
    }
}