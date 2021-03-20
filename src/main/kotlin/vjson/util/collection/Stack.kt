package vjson.util.collection

class Stack<E> {
  private val list = VList<E>()

  fun push(e: E) {
    list.add(e)
  }

  fun pop(): E {
    return list.removeLast()
  }

  fun peek(): E {
    return list.last()
  }

  fun isEmpty(): Boolean {
    return list.isEmpty()
  }
}
