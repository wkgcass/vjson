package vjson.util.collection

class Stack<E> {
  private val list: MutableList<E> = ArrayList()

  fun push(e: E) {
    list.add(e)
  }

  fun pop(): E {
    return list.removeLast()
  }

  fun peek(): E {
    return list[list.size - 1]
  }

  fun isEmpty(): Boolean {
    return list.isEmpty()
  }
}
