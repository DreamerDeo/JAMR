package edu.cmu.lti.nlp.amr

import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.io.BufferedOutputStream
import java.io.OutputStreamWriter
import java.lang.Math.abs
import java.lang.Math.log
import java.lang.Math.exp
import java.lang.Math.random
import java.lang.Math.floor
import java.lang.Math.min
import java.lang.Math.max
import scala.io.Source
import scala.util.matching.Regex
import scala.collection.mutable.Map
import scala.collection.mutable.Set
import scala.collection.mutable.ArrayBuffer

case class Annotation[T](tokenized: Array[String], myTokenized: Array[String], get: T) {
    // This class can be used for annotations on the tokens of a sentence.
    // The annotations can use a different tokenization scheme, and getSpan can be used to convert
    // a span 'myTokenized' to a span in 'tokenized'.
    // Public member 'get' is the annotation.

    assert(tokenized.mkString == myTokenized.mkString, "Annotation tokenization schemes do not match")
    assert(tokenized.mkString.count(_ == ' ') == 0, "Spaces not allowed in tokens") // because we count spaces to find the left and right indices

    val left = myTokenized.map(x => 0)
    val right = myTokenized.map(x => 0)

    for (i <- Range(0, myTokenized.size)) {
        val regexr = myTokenized.take(i+1).mkString.split("").drop(1).mkString(" ?").r
        regexr.findPrefixOf(tokenized.mkString(" ")) match {
            case Some(prefix) => { println(prefix); println(prefix.count(_ == ' ')); right(i) = prefix.count(_ == ' ') + 1}
            case None => assert(false, "Error matching the prefix (this should never occur)")
        }
        if (i > 0) {
            val regexl = (myTokenized.take(i).mkString.split("").drop(1).mkString(" ?")+" ").r
            regexl.findPrefixOf(tokenized.mkString(" ")) match {
                case Some(prefix) => { left(i) = right(i-1) }
                case None => { left(i) = right(i-1) - 1 }
            }
        }
    }

    println(right.toList)
    println(left.toList)

    def getSpan(span: (Int, Int)) : (Int, Int) = {
    // Input: Span in myTokenized (indices start from 0, and span goes from span._1 to span._2-1 inclusive ie same as slice(Int,Int))
    // Output: Span in tokenized (indices start from 0, and span goes from ret._1 to ret._2-1 inclusive ie same as slice(Int,Int))
        return (left(span._1), right(span._2))
    }
}

class AnnotationTest /*extends Suite*/ {
    def testGetSpan() {
        val test1 = Annotation[Array[String]](Array("The", "Riyadh", "-", "based", "Naif", "Arab", "Academy"),
                                              Array("The", "Riyadh-based", "Naif", "Arab", "Academy"),
                                              Array())
        println("test1")
        println("tokenized = "+test1.tokenized.toList)
        println("myTokenized = "+test1.myTokenized.toList)
        for (i <- Range(0, test1.myTokenized.size)) {
            println("i=" + i + " l=" + test1.getSpan(i,i)._1 + " r=" + test1.getSpan(i,i)._2)
        }
        val test2 = Annotation[Array[String]](Array("The", "Riyadh-based", "NaifArab", "Academy"),
                                              Array("The", "Riyadh", "-", "based", "Naif", "ArabAcademy"),
                                              Array())
        println("test2")
        println("tokenized = "+test2.tokenized.toList)
        println("myTokenized = "+test2.myTokenized.toList)
        for (i <- Range(0, test2.myTokenized.size)) {
            println("i=" + i + " l=" + test2.getSpan(i,i)._1 + " r=" + test2.getSpan(i,i)._2)
        }
        val test3 = Annotation[Array[String]](Array("The", "Riyadh-", "based", "NaifArab", "Academy"),
                                              Array("The", "Riy", "adh", "-", "based", "Naif", "ArabAcademy"),
                                              Array())
        println("test3")
        println("tokenized = "+test3.tokenized.toList)
        println("myTokenized = "+test3.myTokenized.toList)
        for (i <- Range(0, test3.myTokenized.size)) {
            println("i=" + i + " l=" + test3.getSpan(i,i)._1 + " r=" + test3.getSpan(i,i)._2)
        }


    }
}
