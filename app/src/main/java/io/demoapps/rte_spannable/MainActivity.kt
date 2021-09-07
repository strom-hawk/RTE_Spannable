package io.demoapps.rte_spannable

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.style.*
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val SUBSCRIPT_SUPERSCRIPT_ENABLED = 1
    private val SUBSCRIPT_SUPERSCRIPT_DISABLED = -1

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
    private var isSuperScriptSet = false
    private var isSubScript = SUBSCRIPT_SUPERSCRIPT_DISABLED
    private var isImageInserted = false

    private var currentTypeFace = 0
    private var selectionStart = 0
    private var boldSelectionStart = ArrayList<Int>()
    private var italicSelectionStart = ArrayList<Int>()
    private var underLineSelectionStart = ArrayList<Int>()
    private var strikeThroughSelectionStart = ArrayList<Int>()
    private var superScriptSelectionStart = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        bindViews()
        addTextWatcher()
    }

    private fun initViews(){
        customEditText = findViewById(R.id.customEditText)
        boldButton = findViewById(R.id.boldButton)
        italicButton = findViewById(R.id.italicButton)
        underlineButton = findViewById(R.id.underlineButton)
        strikeThroughButton = findViewById(R.id.strikeThroughButton)
        superButton = findViewById(R.id.superButton)
        subScriptButton = findViewById(R.id.subScriptButton)
        imageButton = findViewById(R.id.imageButton)
    }

    private fun bindViews(){
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
            isSuperScriptEnabled = !isSuperScriptEnabled
            if(isSuperScriptEnabled){
                isSuperScriptSet = false
            }
            superScriptSelectionStart = customEditText.selectionStart
        }

        subScriptButton.setOnClickListener {
            if(isSubScript == SUBSCRIPT_SUPERSCRIPT_ENABLED){
                isSubScript = SUBSCRIPT_SUPERSCRIPT_DISABLED
            }else{
                isSubScript = SUBSCRIPT_SUPERSCRIPT_ENABLED
            }
            selectionStart = customEditText.selectionStart
        }

        imageButton.setOnClickListener {
            insertImageToEditText()
        }
    }

    private fun addTextWatcher(){
        customEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("----count: ${count}")
                println("----before: ${before}")
                println("----length: ${s.toString().length}")
                val str: Spannable = customEditText.text
                val endLength = s.toString().length
                val normalSpan = StyleSpan(Typeface.NORMAL)
                val superScriptSpan = SuperscriptSpan()
                val subScriptSpan = SubscriptSpan()

                /*if(isNormal){
                    str.setSpan(normalSpan, selectionStart, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }*/

                if(!isImageInserted){
                    checkAndSetBold(str, endLength, before)
                    checkAndSetItalic(str, endLength, before)
                    checkAndSetUnderLine(str, endLength, before)
                    checkAndSetStrikeThrough(str, endLength, before)
                }

/*                if(isSuperScriptEnabled){
                    str.setSpan(superScriptSpan, superScriptSelectionStart, endLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    isSuperScriptSet = true
                }else if(!isSuperScriptEnabled){
                    str.removeSpan(superScriptSpan)
                }*/
            }

            override fun afterTextChanged(s: Editable?) {
                isImageInserted = false
                println("--------${isImageInserted}")
            }
        })
    }

    private fun insertImageToEditText(){
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

            if(endLength < boldSelectionStart[boldSelectionStart.size-1]){
                boldSelectionStart.removeAt(boldSelectionStart.size-1)
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
            }else{
                str.removeSpan(boldSpan)
                if(boldSelectionStart.isNotEmpty()){
                    boldSelectionStart.removeAt(boldSelectionStart.size - 1)
                }
            }
        }
    }

    private fun checkAndSetItalic(str: Spannable, endLength: Int, before: Int){

        val italicSpan = StyleSpan(Typeface.ITALIC)
        if (isItalic) {

            if(endLength < italicSelectionStart[italicSelectionStart.size-1]){
                italicSelectionStart.removeAt(italicSelectionStart.size-1)
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
            }else{
                str.removeSpan(italicSpan)
                if(italicSelectionStart.isNotEmpty()){
                    italicSelectionStart.removeAt(italicSelectionStart.size - 1)
                }
            }
        }
    }

    private fun checkAndSetUnderLine(str: Spannable, endLength: Int, before: Int){
        val underLineSpan = UnderlineSpan()
        if (isUnderLine) {

            if(endLength < underLineSelectionStart[underLineSelectionStart.size-1]){
                underLineSelectionStart.removeAt(underLineSelectionStart.size-1)
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
            }else{
                str.removeSpan(underLineSpan)
                if(underLineSelectionStart.isNotEmpty()){
                    underLineSelectionStart.removeAt(underLineSelectionStart.size - 1)
                }
            }
        }
    }

    private fun checkAndSetStrikeThrough(str: Spannable, endLength: Int, before: Int){
        val strikeThroughSpan = StrikethroughSpan()
        if (isStrikeThrough) {

            if(endLength < strikeThroughSelectionStart[strikeThroughSelectionStart.size-1]){
                strikeThroughSelectionStart.removeAt(strikeThroughSelectionStart.size-1)
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
            }else{
                str.removeSpan(strikeThroughSpan)
                if(strikeThroughSelectionStart.isNotEmpty()){
                    strikeThroughSelectionStart.removeAt(strikeThroughSelectionStart.size - 1)
                }
            }
        }
    }

    private fun boldButtonClicked(){
        isBold = !isBold
        if(isBold){
            boldButton.setTextColor(getColor(R.color.teal_200))
            boldSelectionStart.add(customEditText.selectionStart)
        }else{
            boldButton.setTextColor(getColor(R.color.white))
            boldSelectionStart.removeAt(boldSelectionStart.size-1)
        }
    }

    private fun italicButtonClicked(){
        isItalic = !isItalic
        if(isItalic){
            italicButton.setTextColor(getColor(R.color.teal_200))
            italicSelectionStart.add(customEditText.selectionStart)
        }else{
            italicButton.setTextColor(getColor(R.color.white))
            italicSelectionStart.removeAt(italicSelectionStart.size-1)
        }
    }

    private fun underLineButtonClicked(){
        isUnderLine = !isUnderLine
        if(isUnderLine){
            underlineButton.setTextColor(getColor(R.color.teal_200))
            underLineSelectionStart.add(customEditText.selectionStart)
        }else{
            underlineButton.setTextColor(getColor(R.color.white))
            underLineSelectionStart.removeAt(underLineSelectionStart.size-1)
        }
    }

    private fun strikeThroughButtonClicked(){
        isStrikeThrough = !isStrikeThrough
        if(isStrikeThrough){
            strikeThroughButton.setTextColor(getColor(R.color.teal_200))
            strikeThroughSelectionStart.add(customEditText.selectionStart)
        }else{
            strikeThroughButton.setTextColor(getColor(R.color.white))
            strikeThroughSelectionStart.removeAt(strikeThroughSelectionStart.size-1)
        }
    }
}