package com.leon.baseapp.utils

import com.google.gson.JsonElement
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Auther: 千里
 * Date: 2019/9/24 18:02
 * Description:
 */
object FormatUtil {
    fun strIsBlank(str: String?): Boolean {
        return str == null || strIsEmpty(str)
    }

    fun strIsEmpty(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    /**
     * auther: 千里
     * date: 2019/9/25 11:55
     * describe:不是double类型 返回true
     */
    fun isNotDouble(num: Any?): Boolean {
        return !isDouble(num)
    }

    /**
     * auther: 千里
     * date: 2019/10/14 17:59
     * describe:是double类型 返回true
     */
    fun isDouble(num: Any?): Boolean {
        if (num == null) return false
        try {
            val aDouble = num.toString().toDouble()
            if (java.lang.Double.isNaN(aDouble)) return false
        } catch (e: Exception) {
            return false
        }
        return true
    }

    /**
     * auther: 千里
     * date: 2019/10/21 14:43
     * describe:obj转int
     *
     * @param defaultValue 默认值
     */
    @JvmOverloads
    fun obj2int(obj: Any?, defaultValue: Int = 0): Int {
        return if (obj == null) defaultValue else try {
            if (obj is Number) return obj.toInt()
            if (obj is JsonElement) obj.asInt else obj.toString().toInt()
            //            if (obj instanceof JsonNode) return ((JsonNode) obj).asInt();
        } catch (e: Exception) {
            defaultValue
        }
    }

    /**
     * auther: 千里
     * date: 2019/10/21 11:58
     * describe:obj转double
     *
     * @param defaultValue 默认值
     */
    @JvmOverloads
    fun obj2double(obj: Any?, defaultValue: Double = 0.0): Double {
        return if (obj == null) defaultValue else try {
            if (obj is Number) return obj.toDouble()
            if (obj is JsonElement) return obj.asDouble
            //            if (obj instanceof JsonNode) return ((JsonNode) obj).asDouble();
            if (isNotDouble(obj)) defaultValue else obj.toString().toDouble()
        } catch (e: Exception) {
            defaultValue
        }
    }

    /**
     * auther: 千里
     * date: 2019/10/21 14:29
     * describe:obj转Long
     *
     * @param defaultValue 默认值
     */
    @JvmOverloads
    fun obj2Long(obj: Any?, defaultValue: Long? = null): Long? {
        return if (obj == null) defaultValue else try {
            if (obj is Number) obj.toLong()
            if (obj is JsonElement) obj.asLong else java.lang.Long.valueOf(obj.toString())
            //            if (obj instanceof JsonNode) return ((JsonNode) obj).asLong();
        } catch (e: Exception) {
            defaultValue
        }
    }

    @JvmOverloads
    fun obj2Str(`object`: Any?, defaultValue: String = ""): String {
        return if (`object` == null) defaultValue else try {
            if (`object` is String) return `object`
            if (`object` is JsonElement) return `object`.asString
            //            if (object instanceof JsonNode) return ((JsonNode) object).asText();
            val value = `object`.toString()
            if (value.isEmpty()) defaultValue else value
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    fun obj2Boolean(`object`: Any?): Boolean {
        return if (`object` == null) false else try {
            if (`object` is Boolean) return `object`
            if (`object` is JsonElement) `object`.asBoolean else java.lang.Boolean.parseBoolean(`object`.toString())
            //            if (object instanceof JsonNode) return ((JsonNode) object).asBoolean();
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    //    public static Date obj2Date(Object object, Date defaultValue) {
    //        if (object == null) return defaultValue;
    //        if (object instanceof Date) return (Date) object;
    //        try {
    //            return DateUtils.parseDate(FormatUtil.obj2Str(object), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd");
    //        } catch (Exception e) {
    //            return defaultValue;
    //        }
    //    }
    //
    //    public static Date obj2Date(Object object) {
    //        return obj2Date(object, null);
    //    }
    /**
     * author: 千里
     * date: 2020/1/19 15:32
     * describe:时间戳转换为日期字符串
     */
    @JvmOverloads
    fun dateTimeMillis2Str(timeMillis: Long, parsePattern: String? = "yyyy-MM-dd HH:mm:ss"): String {
        var timeMillis = timeMillis
        return try {
            val timeStr = StringBuilder(timeMillis.toString())
            val length = timeStr.length
            if (length < 13) { //不足后面补0
                val i = 13 - length
                for (j in 0 until i) {
                    timeStr.append("0")
                }
                timeMillis = timeStr.toString().toLong()
            }
            SimpleDateFormat(parsePattern).format(Date(timeMillis))
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * auther: 千里
     * date: 2019/11/22 15:40
     * describe:str数组转LinkedList<Long>
    </Long> */
    fun strArray2LongLinkedList(strArray: Array<String?>?): LinkedList<Long?> {
        val list = LinkedList<Long?>()
        if (strArray == null) return list
        for (s in strArray) {
            val aLong = obj2Long(s, -1L)
            if (aLong!! > -1) list.add(aLong)
        }
        return list
    }

    /**
     * auther: 千里
     * date: 2019/9/25 11:57
     * describe:判断map是否为空
     */
    fun mapIsBlank(map: Map<*, *>?): Boolean {
        return map == null || map.isEmpty()
    }

    /**
     * auther: 千里
     * date: 2019/9/25 11:57
     * describe:判断list是否为空
     */
    fun listIsBlank(list: List<*>?): Boolean {
        return list == null || list.isEmpty()
    }

    /**
     * auther: 千里
     * date: 2019/9/25 11:55
     * describe:验证身份证号码
     */
    fun isIdCardNumber(idCardNumber: String?): Boolean {
        if (idCardNumber == null || idCardNumber.isEmpty()) {
            return false
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        val regularExpression =
            "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                    "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)"
        //假设18位身份证号码:41000119910101123X  410001 19910101 123X
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //(18|19|20)                19（现阶段可能取值范围18xx-20xx年）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十七位奇数代表男，偶数代表女）
        //[0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
        //$结尾
        //假设15位身份证号码:410001910101123  410001 910101 123
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十五位奇数代表男，偶数代表女），15位身份证不含X
        //$结尾
        val matches = idCardNumber.matches(Regex(regularExpression))
        //判断第18位校验值
        if (matches) {
            if (idCardNumber.length == 18) {
                return try {
                    val charArray = idCardNumber.toCharArray()
                    //前十七位加权因子
                    val idCardWi = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
                    //这是除以11后，可能产生的11位余数对应的验证码
                    val idCardY = arrayOf("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2")
                    var sum = 0
                    for (i in idCardWi.indices) {
                        val current = charArray[i].toString().toInt()
                        val count = current * idCardWi[i]
                        sum += count
                    }
                    val idCardLast = charArray[17]
                    val idCardMod = sum % 11
                    if (idCardY[idCardMod].toUpperCase() == idCardLast.toString().toUpperCase()) {
                        true
                    } else {
                        println(
                            "身份证最后一位:" + idCardLast.toString().toUpperCase() +
                                    "错误,正确的应该是:" + idCardY[idCardMod].toUpperCase()
                        )
                        false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("异常:$idCardNumber")
                    false
                }
            }
        }
        return matches
    }

    /**
     * auther: 千里
     * date: 2019/9/27 12:19
     * describe:字符串长度是否在start和end之间  （开区间）
     */
    fun isLengthBetween(str: String, start: Int, end: Int): Boolean {
        if (strIsBlank(str)) return false
        val length = str.length
        return length >= start && length <= end
    }

    /**
     * auther: 千里
     * date: 2019/10/14 16:28
     * describe:是否手机号
     */
    fun isMobileNumber(mobiles: String?): Boolean {
        if (strIsBlank(mobiles)) return false
        val p = Pattern.compile("^1\\d{10}$")
        val m = p.matcher(mobiles)
        return m.matches()
    }

    fun addTime(number: Int, type: Int): Date {
        return addTime(Date(), number, type)
    }

    /**
     * auther: 千里
     * date: 2019/11/4 10:07
     * describe:追加时间
     */
    fun addTime(date: Date?, number: Int, type: Int): Date {
        var date = date
        val calendar = Calendar.getInstance()
        if (date == null) date = Date()
        calendar.time = date
        if (type == Calendar.SECOND) calendar.add(Calendar.SECOND, number) //分钟
        if (type == Calendar.MINUTE) calendar.add(Calendar.MINUTE, number) //分钟
        else if (type == Calendar.HOUR) calendar.add(Calendar.HOUR, number) //小时
        else if (type == Calendar.DATE) calendar.add(Calendar.DATE, number) //天
        else if (type == Calendar.WEEK_OF_YEAR) calendar.add(Calendar.WEEK_OF_YEAR, number) //周
        else if (type == Calendar.MONTH) calendar.add(Calendar.MONTH, number) //月
        else if (type == Calendar.YEAR) calendar.add(Calendar.YEAR, number) //年
        return calendar.time
    }

    /**
     * auther: 千里
     * date: 2019/11/22 17:05
     * describe:相差多少
     */
    fun timeDifrrent(date: Date, type: Int): Long {
        val l = System.currentTimeMillis() - date.time
        if (type == Calendar.SECOND) return l / 1000
        if (type == Calendar.MINUTE) return l / (1000 * 60)
        return if (type == Calendar.HOUR) l / (1000 * 60 * 60) else l / (1000 * 60 * 60 * 24)
        //默认天
    }

    fun dayDifrrent(date: Date): Long {
        return timeDifrrent(date, Calendar.DATE)
    }

    /**
     * auther: 千里
     * date: 2019/11/11 10:26
     * describe:格式化文件名  windows下文件名中不能含有：\ / : * ? " < > | 英文的这些字符
     */
    fun formatFileName(fileName: String?): String {
        var fileName = fileName ?: return ""
        fileName = removePunctuation(fileName)
        fileName = fileName.replace(" ".toRegex(), "")
        if (fileName.length > 80) fileName = fileName.substring(0, 80)
        return fileName
    }

    /**
     * author: 千里
     * describe:剔除标点符号
     */
    fun removePunctuation(text: String): String {
        var text = text
        text = text.trim { it <= ' ' }
        text = text.replace("[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×/\\\\:*?\".《》\t\r\n]".toRegex(), "")
        val specailSymbol = "	 "
        val arr = specailSymbol.split("".toRegex()).toTypedArray()
        for (str in arr) {
            text = text.replace(str, "")
        }
        return text
    }

    /**
     * auther: 千里
     * date: 2019/11/18 15:54
     * describe:保留几位小数
     */
    @JvmOverloads
    fun formatDecimals(value: Double, defaultValue: Int = 2): Double {
        val bigDecimal = BigDecimal(value)
        return bigDecimal.setScale(defaultValue, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    fun formatUUID(): String {
        return UUID.randomUUID().toString().replace("-".toRegex(), "")
    }

    fun formatPageRows(page: Int, rows: Int, listSize: Int): IntArray {
        var page = page
        val pages = (listSize - 1) / rows + 1 //总页数
        page = Math.min(page, pages)
        val start = (page - 1) * rows
        val end = Math.min(page * rows, listSize)
        return intArrayOf(start, end)
    }

    /**
     * author: 千里
     * date: 2020/1/20 10:14
     * describe:计算字符串字节长度
     */
    fun getStringCharCount(str: String): Int {
        var length = 0
        for (i in 0 until str.length) {
            val ascii = Character.codePointAt(str, i)
            if (ascii >= 0 && ascii <= 255) length++ else length += 2
        }
        return length
    }

    /**
     * author: 千里
     * date: 2020/4/10 9:14
     * describe:拆分字符串为Long集合
     */
    fun splitString2Longs(ids: String, regex: String): List<Long> {
        val list: MutableList<Long> = ArrayList()
        val split = ids.split(regex.toRegex()).toTypedArray()
        for (s in split) {
            val aLong = obj2Long(s)
            if (aLong != null) list.add(aLong)
        }
        return list
    }
}