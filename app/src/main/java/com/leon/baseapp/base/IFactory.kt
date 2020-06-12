package com.leon.baseapp.base

/**
 * Author: 千里
 * Date: 2020/6/9 12:02
 * Description:
 */
interface IFactory<T> {
    fun newInstance(): T
}