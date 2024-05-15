package com.ridesharingapp.passengersideapp

fun call_hash_code_and_to_string_on_array() {
    val primitiveArray = intArrayOf(1, 2, 3)
    val objectArray = arrayOf("A", "B", "C")
    val arrayOfArray = arrayOf(arrayOf("A", "B"), arrayOf("C", "D"))

    println(primitiveArray.toString())       // Noncompliant, output: [I@2acf57e3
    println(primitiveArray.hashCode())       // Noncompliant, output: 718231523
    println(objectArray.toString())          // Noncompliant, output: [Ljava.lang.String;@506e6d5e
    println(objectArray.hashCode())          // Noncompliant, output: 1349414238
    println(arrayOfArray.toString())         // Noncompliant, output: [[Ljava.lang.String;@96532d6
    println(arrayOfArray.contentToString())  // Noncompliant, output: [[Ljava.lang.String;@3796751b, [Ljava.lang.String;@67b64c45]
    println(arrayOfArray.hashCode())         // Noncompliant, output: 157627094
    println(arrayOfArray.contentHashCode())  // Noncompliant, output: 586055243
}

fun IfElseIfWithoutElse(x: Int) {
    if (x == 0) {
        println("x equals 0")
    } else if (x == 1) {
        println("x equals 1")
    }

    val y = x + 5
    if (y == 6) {
        println("y  equals 6")
    } else if (y == 7){
        println("y  equals 7")
    }
}

fun faultyAddition(a: Int, b: Int): Int {
    return a - b;
}