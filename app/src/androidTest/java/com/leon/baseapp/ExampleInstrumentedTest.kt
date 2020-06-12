package com.leon.baseapp

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.leon.baseapp", appContext.packageName)

    }
}

interface Base {
    fun printMessage()
    fun printMessageLine()
    fun print()
}

class BaseImpl(val x: Int) : Base {
    override fun printMessage() {
        print(x)
    }

    override fun printMessageLine() {
        println(x)
    }

    override fun print() {
        println(x)
    }
}

class Derived(b: Base) : Base by b {
    override fun printMessage() {
        print("abc")
    }
}

fun <T, R> Collection<T>.fold(
    initial: R,
    combine: (acc: R, nextElement: T) -> R
): R {
    var accumulator: R = initial
    for (element: T in this) {
        accumulator = combine(accumulator, element)
    }
    return accumulator
}

fun computeRunTime(action: (() -> Unit)?) {
    val startTime = System.currentTimeMillis()
    action?.invoke()
    println("the code run time is ${System.currentTimeMillis() - startTime}")
}

inline fun <reified T> membersOf() = T::class.members
fun main() {
    var name: String by Delegates.observable("this is default value") { property: KProperty<*>, oldValue: String, newValue: String ->
        println("$oldValue $newValue")
    }
    val items = mutableListOf(1, 1, 3, 4, 5)
    items.drop(3)
//    println(items)
//    println(BaseImpl(1)::class.members.joinToString("\n"))
//// Lambdas 表达式是花括号括起来的代码块。
//    items.fold(0, {
//// 如果一个 lambda 表达式有参数， 前面是参数， 后跟“->”
//            acc: Int, i: Int ->
//        print("acc = $acc, i = $i, ")
//        val result = acc + i
//        println("result = $result")
//// lambda 表达式中的最后一个表达式是返回值：
//        result
//    })

    val lazySeq = sequence {
        print("START ")
        for (i in 1.rangeTo(5)) {
            yield(i)
            print("STEP ")
        }
        print("END")
    }
    // 输出序列的前三个元素
    lazySeq.take(3).forEach { print("$it ") }
    var a =""""a/r/naa"""
}
