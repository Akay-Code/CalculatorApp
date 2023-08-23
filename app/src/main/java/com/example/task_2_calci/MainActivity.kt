package com.example.task_2_calci

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    // initialized working string and result string gloabally
    lateinit var workingText: TextView
    lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workingText = findViewById(R.id.work_view)
        resultText = findViewById(R.id.result_view)
    }

    //bools for checking whether operator and decimal can be added or not
    var canAdd = false
    var canDecimal = true

    fun backSpaceAction(view: View) {
//        // we want to remove the last character
        val length = workingText.length()
        if(length > 0 ){
            workingText.text = workingText.text.subSequence(0 , length-1)
        }
    }

    fun allClearAction(view: View) {
        //resets both working and result string
        workingText.text = ""
        resultText.text = ""
    }

    fun numberAction(view: View) {

        //appends working string with digits and decimal point
        if(view is Button){
            if(view.text == "."){
                if(canDecimal){
                    workingText.append(view.text)
                    canDecimal = false
                }
            }
            else{
                workingText.append(view.text)
            }
            canAdd = true
        }
    }
    fun operatorAction(view: View) {

        //appends working string with operators
        if(view is Button && canAdd){
            workingText.append(view.text)
            canAdd = false
            canDecimal = true
        }
    }

    fun equalsAction(view: View) {
        resultText.text = calculateResult()
    }

    private fun calculateResult(): String {

        val digitsAndOperators = digitsOperator()

        if(digitsAndOperators.isEmpty()) return ""

        // going according to Bodmas , division and multiplication first

        //performing division and multiplication first
        val divisionAndMultiplication = divisionAndMultiplicationCalculate(digitsAndOperators)
        if(divisionAndMultiplication.isEmpty()) return ""

        //later performing addition and subtraction
        val result = addAndSubCalculate(divisionAndMultiplication)
        return result.toString()
    }

    private fun addAndSubCalculate(passList: MutableList<Any>): Float {
        var result = passList[0] as Float

        //adding or subtracting numbers 
        for(i in passList.indices){

            if(passList[i] is Char && i != passList.lastIndex){

                val operator = passList[i]
                val next = passList[i+1] as Float

                if(operator == '+'){
                    result += next
                }
                if(operator == '-'){
                    result -= next
                }
            }
        }
        return result
    }

    private fun divisionAndMultiplicationCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList

        // multiplying or dvidinng two numbers before and after operator
        while(list.contains('x') || list.contains('/')){

            val newList = mutableListOf<Any>()
            var restartIndex = list.size

            for(i in list.indices){
                if(list[i] is Char && i != list.lastIndex && i<restartIndex){
                    val operator = list[i]
                    val prev = list[i-1] as Float
                    val next = list[i+1] as Float

                    when(operator){
                        'x' -> {
                            newList.add(prev * next)
                            restartIndex = i+1
                        }
                        '/' -> {
                            newList.add(prev / next)
                            restartIndex = i+1
                        }
                        else -> {
                            newList.add(prev)
                            newList.add(operator)
                        }
                    }
                }
                if(i > restartIndex){
                    newList.add(list[i])
                }
            }
            list = newList
        }
        return list
    }

    private fun digitsOperator(): MutableList<Any>{

        //changing the string to LIST having values and operators
        //for example if string = 12*55-3/4 , then elements in LIST will be as list = [12 , * , 55 , - , 3 , / 4]
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(character in workingText.text){

            if(character.isDigit() || character == '.'){
                currentDigit += character

            }
            else{
                //an operator was found
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }
        if(currentDigit != ""){
            list.add(currentDigit.toFloat())
        }
        return list
    }
}
