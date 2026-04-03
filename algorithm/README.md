# Алгосы

## Сложность
- [Сложность, коллекции, поиск](../java-core/collections.md)

## Задачки
- [Two Sum](#two-sum) https://leetcode.com/problems/two-sum/description/

#### Two Sum

```kotlin
// Неоптимальное решение O(n²)
fun twoSum(nums: IntArray, target: Int): IntArray {
    for (i in nums.indices) {
        for (j in i + 1 until nums.size) {
            if (nums[i] + nums[j] == target) {
                return intArrayOf(i, j)
            }
        }
    }
    throw IllegalArgumentException("No solution")
}

// Оптимальное решение O(n)
fun twoSum2(nums: IntArray, target: Int): IntArray {
    val map = mutableMapOf<Int, Int>() // value -> index

    for (i in nums.indices) {
        val complement = target - nums[i]

        if (map.containsKey(complement)) {
            return intArrayOf(map[complement]!!, i)
        }

        map[nums[i]] = i
    }

    throw IllegalArgumentException("No solution")
}

fun main() {
    twoSum2(intArrayOf(1, 1, 3), 2)
}
```
