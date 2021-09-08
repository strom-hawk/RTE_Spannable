package io.demoapps.rte_spannable

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.*
import android.text.style.*
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class RichTextActivity : AppCompatActivity() {
    private lateinit var customEditText: EditText
    private lateinit var boldButton: Button
    private lateinit var italicButton: Button
    private lateinit var underlineButton: Button
    private lateinit var strikeThroughButton: Button
    private lateinit var superButton: Button
    private lateinit var subScriptButton: Button
    private lateinit var imageButton: Button

    private var isNormal = true
    private var isBold = false
    private var isItalic = false
    private var isUnderLine = false
    private var isStrikeThrough = false
    private var isSuperScriptEnabled = false
    private var isSubScriptEnabled = false
    private var isImageInserted = false

    private var boldSelectionStart = ArrayList<Int>()
    private var italicSelectionStart = ArrayList<Int>()
    private var underLineSelectionStart = ArrayList<Int>()
    private var strikeThroughSelectionStart = ArrayList<Int>()
    private var superScriptSelectionStart = ArrayList<Int>()
    private var subScriptSelectionStart = ArrayList<Int>()

    private lateinit var spanHandler: SpanHandler
    private var startIndex = -1
    private var endIndex = 0
    private var beforeCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        bindViews()
        addTextWatcher()
    }

    private fun initViews() {
        customEditText = findViewById(R.id.customEditText)
        boldButton = findViewById(R.id.boldButton)
        italicButton = findViewById(R.id.italicButton)
        underlineButton = findViewById(R.id.underlineButton)
        strikeThroughButton = findViewById(R.id.strikeThroughButton)
        superButton = findViewById(R.id.superButton)
        subScriptButton = findViewById(R.id.subScriptButton)
        imageButton = findViewById(R.id.imageButton)

        spanHandler = SpanHandler(this)
    }

    private fun bindViews() {
        boldButton.setOnClickListener {
            boldButtonClicked()
        }

        italicButton.setOnClickListener {
            italicButtonClicked()
        }

        underlineButton.setOnClickListener {
            underLineButtonClicked()
        }

        strikeThroughButton.setOnClickListener {
            strikeThroughButtonClicked()
        }

        superButton.setOnClickListener {
            superScriptButtonClicked()
        }

        subScriptButton.setOnClickListener {
            subScriptButtonClicked()
        }

        imageButton.setOnClickListener {
            insertImageToEditText()
        }
    }

    private fun boldButtonClicked() {
        isBold = !isBold
        if (isBold) {
            boldButton.setTextColor(getColor(R.color.teal_200))
            boldSelectionStart.add(customEditText.selectionStart)
        } else {
            boldButton.setTextColor(getColor(R.color.white))
            boldSelectionStart.removeAt(boldSelectionStart.size - 1)
            addSpaceAndMoveCursorToEnd()
        }

        //println("------${customEditText.selectionStart}")

        var existingSpanStart = -1
        if (boldSelectionStart.isNotEmpty()) {
            existingSpanStart = boldSelectionStart[0]
        }

        spanHandler.applyStyle(
            customEditText.getEditableText(),
            customEditText.selectionStart,
            customEditText.selectionEnd,
            isBold,
            existingSpanStart
        )
    }

    private fun italicButtonClicked() {
        isItalic = !isItalic
        if (isItalic) {
            italicButton.setTextColor(getColor(R.color.teal_200))
            italicSelectionStart.add(customEditText.selectionStart)
        } else {
            italicButton.setTextColor(getColor(R.color.white))
            italicSelectionStart.removeAt(italicSelectionStart.size - 1)
            addSpaceAndMoveCursorToEnd()
        }
    }

    private fun underLineButtonClicked() {
        isUnderLine = !isUnderLine
        if (isUnderLine) {
            underlineButton.setTextColor(getColor(R.color.teal_200))
            underLineSelectionStart.add(customEditText.selectionStart)
        } else {
            underlineButton.setTextColor(getColor(R.color.white))
            underLineSelectionStart.removeAt(underLineSelectionStart.size - 1)
            addSpaceAndMoveCursorToEnd()
        }
    }

    private fun strikeThroughButtonClicked() {
        isStrikeThrough = !isStrikeThrough
        if (isStrikeThrough) {
            strikeThroughButton.setTextColor(getColor(R.color.teal_200))
            strikeThroughSelectionStart.add(customEditText.selectionStart)
        } else {
            strikeThroughButton.setTextColor(getColor(R.color.white))
            strikeThroughSelectionStart.removeAt(strikeThroughSelectionStart.size - 1)
            addSpaceAndMoveCursorToEnd()
        }
    }

    private fun superScriptButtonClicked() {
        isSuperScriptEnabled = !isSuperScriptEnabled
        if (isSuperScriptEnabled) {
            superButton.setTextColor(getColor(R.color.teal_200))
            superScriptSelectionStart.add(customEditText.selectionStart)
        } else {
            superButton.setTextColor(getColor(R.color.white))
            superScriptSelectionStart.removeAt(superScriptSelectionStart.size - 1)
            addSpaceAndMoveCursorToEnd()
        }
    }

    private fun subScriptButtonClicked() {
        isSubScriptEnabled = !isSubScriptEnabled
        if (isSubScriptEnabled) {
            subScriptButton.setTextColor(getColor(R.color.teal_200))
            subScriptSelectionStart.add(customEditText.selectionStart)
        } else {
            subScriptButton.setTextColor(getColor(R.color.white))
            subScriptSelectionStart.removeAt(subScriptSelectionStart.size - 1)
            addSpaceAndMoveCursorToEnd()
        }
    }

    private fun addTextWatcher() {
        customEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //println("----start: ${start}")
                //println("----before: ${s?.length}")
                //println("----count: ${customEditText.selectionEnd}")
                //println("----length: ${s.toString().length}")
                val str: Spannable = customEditText.text
                val endLength = s.toString().length
                val normalSpan = StyleSpan(Typeface.NORMAL)


                if (!isImageInserted) {
                    /*checkAndSetBold(str, endLength, before)
                    checkAndSetItalic(str, endLength, before)
                    checkAndSetUnderLine(str, endLength, before)
                    checkAndSetStrikeThrough(str, endLength, before)
                    checkAndSetSuperScript(str, endLength, before)
                    checkAndSetSubScript(str, endLength, before)*/
                }

                /*startIndex = start
                endIndex = start+count*/
                manipulateData(s?.length!!)
                println("----start" + startIndex)
                println("----end" + (startIndex + endIndex))

            }

            override fun afterTextChanged(s: Editable?) {
                var existingSpanStart = -1
                if (boldSelectionStart.isNotEmpty()) {
                    existingSpanStart = boldSelectionStart[0]
                }

                spanHandler.applyStyle(s, startIndex, startIndex + endIndex, isBold, existingSpanStart)
                isImageInserted = false
            }
        })
    }

    private fun insertImageToEditText() {
        isImageInserted = true
        val editable = customEditText.editableText
        val startSelection = customEditText.selectionStart
        val endSelection = customEditText.selectionEnd

        val imageDrawable: Drawable = ContextCompat.getDrawable(this, R.drawable.ic_launcher_background)!!
        imageDrawable.setBounds(0, 0, imageDrawable.intrinsicWidth, imageDrawable.intrinsicHeight)
        val imageSpan =
            ImageSpan(imageDrawable, "https://flashprep-media-aps1.s3.ap-south-1.amazonaws.com/release/000-create-default/01.jpg")

        val centerSpan = AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
        val builder = SpannableStringBuilder()
        builder.append(Constants.CHAR_NEW_LINE)
        builder.append(Constants.ZERO_WIDTH_SPACE_STR)
        builder.append(Constants.CHAR_NEW_LINE)
        builder.append(Constants.ZERO_WIDTH_SPACE_STR)
        builder.setSpan(imageSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(centerSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val leftSpan: AlignmentSpan = AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL)
        builder.setSpan(leftSpan, 3, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

        editable.replace(startSelection, endSelection, builder)
    }

    private fun checkAndSetBold(str: Spannable, endLength: Int, before: Int) {
        val boldSpan = StyleSpan(Typeface.BOLD)
        if (isBold) {

            if (endLength < boldSelectionStart[boldSelectionStart.size - 1]) {
                boldSelectionStart.removeAt(boldSelectionStart.size - 1)
                boldSelectionStart.add(endLength)
            }
            val topBoldIndexInList = boldSelectionStart[boldSelectionStart.size - 1]

            if (topBoldIndexInList <= endLength) {
                str.setSpan(boldSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else {
            if (boldSelectionStart.isNotEmpty() && before > endLength) {
                val topBoldIndexInList = boldSelectionStart[boldSelectionStart.size - 1]
                if (topBoldIndexInList < endLength) {
                    str.setSpan(boldSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    boldSelectionStart.removeAt(boldSelectionStart.size - 1)
                }
            } else {
                str.removeSpan(boldSpan)
                if (boldSelectionStart.isNotEmpty()) {
                    boldSelectionStart.removeAt(boldSelectionStart.size - 1)
                }
            }
        }
    }

    private fun checkAndSetItalic(str: Spannable, endLength: Int, before: Int) {

        val italicSpan = StyleSpan(Typeface.ITALIC)
        if (isItalic) {

            if (endLength < italicSelectionStart[italicSelectionStart.size - 1]) {
                italicSelectionStart.removeAt(italicSelectionStart.size - 1)
                italicSelectionStart.add(endLength)
            }
            val topBoldIndexInList = italicSelectionStart[italicSelectionStart.size - 1]

            if (topBoldIndexInList <= endLength) {
                str.setSpan(italicSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else {
            if (italicSelectionStart.isNotEmpty() && before > endLength) {
                val topBoldIndexInList = italicSelectionStart[italicSelectionStart.size - 1]
                if (topBoldIndexInList < endLength) {
                    str.setSpan(italicSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    italicSelectionStart.removeAt(italicSelectionStart.size - 1)
                }
            } else {
                str.removeSpan(italicSpan)
                if (italicSelectionStart.isNotEmpty()) {
                    italicSelectionStart.removeAt(italicSelectionStart.size - 1)
                }
            }
        }
    }

    private fun checkAndSetUnderLine(str: Spannable, endLength: Int, before: Int) {
        val underLineSpan = UnderlineSpan()
        if (isUnderLine) {

            if (endLength < underLineSelectionStart[underLineSelectionStart.size - 1]) {
                underLineSelectionStart.removeAt(underLineSelectionStart.size - 1)
                underLineSelectionStart.add(endLength)
            }
            val topBoldIndexInList = underLineSelectionStart[underLineSelectionStart.size - 1]

            if (topBoldIndexInList <= endLength) {
                str.setSpan(underLineSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else {
            if (underLineSelectionStart.isNotEmpty() && before > endLength) {
                val topBoldIndexInList = underLineSelectionStart[underLineSelectionStart.size - 1]
                if (topBoldIndexInList < endLength) {
                    str.setSpan(underLineSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    underLineSelectionStart.removeAt(underLineSelectionStart.size - 1)
                }
            } else {
                str.removeSpan(underLineSpan)
                if (underLineSelectionStart.isNotEmpty()) {
                    underLineSelectionStart.removeAt(underLineSelectionStart.size - 1)
                }
            }
        }
    }

    private fun checkAndSetStrikeThrough(str: Spannable, endLength: Int, before: Int) {
        val strikeThroughSpan = StrikethroughSpan()
        if (isStrikeThrough) {

            if (endLength < strikeThroughSelectionStart[strikeThroughSelectionStart.size - 1]) {
                strikeThroughSelectionStart.removeAt(strikeThroughSelectionStart.size - 1)
                strikeThroughSelectionStart.add(endLength)
            }
            val topBoldIndexInList = strikeThroughSelectionStart[strikeThroughSelectionStart.size - 1]

            if (topBoldIndexInList <= endLength) {
                str.setSpan(strikeThroughSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else {
            if (strikeThroughSelectionStart.isNotEmpty() && before > endLength) {
                val topBoldIndexInList = strikeThroughSelectionStart[strikeThroughSelectionStart.size - 1]
                if (topBoldIndexInList < endLength) {
                    str.setSpan(strikeThroughSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    strikeThroughSelectionStart.removeAt(strikeThroughSelectionStart.size - 1)
                }
            } else {
                str.removeSpan(strikeThroughSpan)
                if (strikeThroughSelectionStart.isNotEmpty()) {
                    strikeThroughSelectionStart.removeAt(strikeThroughSelectionStart.size - 1)
                }
            }
        }
    }

    private fun checkAndSetSuperScript(str: Spannable, endLength: Int, before: Int) {
        val superScriptSpan = SuperscriptSpan()

        if (isSuperScriptEnabled) {
            if (endLength < superScriptSelectionStart[superScriptSelectionStart.size - 1]) {
                superScriptSelectionStart.removeAt(superScriptSelectionStart.size - 1)
                superScriptSelectionStart.add(endLength)
            }
            val topBoldIndexInList = superScriptSelectionStart[superScriptSelectionStart.size - 1]

            if (topBoldIndexInList <= endLength) {
                str.setSpan(superScriptSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else {
            if (superScriptSelectionStart.isNotEmpty() && before > endLength) {
                val topBoldIndexInList = superScriptSelectionStart[superScriptSelectionStart.size - 1]
                if (topBoldIndexInList < endLength) {
                    str.setSpan(superScriptSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    superScriptSelectionStart.removeAt(superScriptSelectionStart.size - 1)
                }
            } else {
                str.removeSpan(superScriptSpan)
                if (superScriptSelectionStart.isNotEmpty()) {
                    superScriptSelectionStart.removeAt(superScriptSelectionStart.size - 1)
                }
            }
        }
    }

    private fun checkAndSetSubScript(str: Spannable, endLength: Int, before: Int) {
        val subScriptSpan = SubscriptSpan()
        if (isSubScriptEnabled) {
            if (endLength < subScriptSelectionStart[subScriptSelectionStart.size - 1]) {
                subScriptSelectionStart.removeAt(subScriptSelectionStart.size - 1)
                subScriptSelectionStart.add(endLength)
            }
            val topBoldIndexInList = subScriptSelectionStart[subScriptSelectionStart.size - 1]

            if (topBoldIndexInList <= endLength) {
                str.setSpan(subScriptSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else {
            if (subScriptSelectionStart.isNotEmpty() && before > endLength) {
                val topBoldIndexInList = subScriptSelectionStart[subScriptSelectionStart.size - 1]
                if (topBoldIndexInList < endLength) {
                    str.setSpan(subScriptSpan, topBoldIndexInList, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    subScriptSelectionStart.removeAt(subScriptSelectionStart.size - 1)
                }
            } else {
                str.removeSpan(subScriptSpan)
                if (subScriptSelectionStart.isNotEmpty()) {
                    subScriptSelectionStart.removeAt(subScriptSelectionStart.size - 1)
                }
            }
        }
    }

    private fun addSpaceAndMoveCursorToEnd() {
        /*val customEditTextStr = customEditText.text.toString()
        val strLength = customEditTextStr.length
        if(customEditText.length() > 0 && customEditTextStr[strLength-1] != ' '){
            customEditText.setText(customEditText.text.append(" "))
            customEditText.setSelection(customEditText.length())
        }*/
    }

    private fun manipulateData(strLength: Int) {
        if (strLength > beforeCount) {
            startIndex += 1
            endIndex = 1
        } else {
            if (endIndex == 1) {
                endIndex -= 1
            } else {
                startIndex -= 1
            }
        }
        beforeCount = strLength
    }

}